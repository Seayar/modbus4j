/**
 * Copyleft (c) 2026 Seayar. All rights reversed.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * @author Seayar
 * @date 2026-08-10
 */
package io.github.seayar.modbus4j;

import io.github.seayar.modbus4j.base.KeyedModbusLocator;
import io.github.seayar.modbus4j.base.ReadFunctionGroup;
import io.github.seayar.modbus4j.base.RegisterRange;
import io.github.seayar.modbus4j.exception.ModbusCodeException;
import io.github.seayar.modbus4j.exception.ModbusInitException;
import io.github.seayar.modbus4j.exception.ModbusTransportException;
import io.github.seayar.modbus4j.locator.BaseLocator;
import io.github.seayar.modbus4j.locator.BatchRead;
import io.github.seayar.modbus4j.locator.BatchResults;
import io.github.seayar.modbus4j.locator.BinaryLocator;
import io.github.seayar.modbus4j.locator.ModbusLocator;
import io.github.seayar.modbus4j.locator.NumericLocator;
import io.github.seayar.modbus4j.locator.StringLocator;
import io.github.seayar.modbus4j.msg.AbstractModbusRequest;
import io.github.seayar.modbus4j.msg.AbstractModbusResponse;
import io.github.seayar.modbus4j.msg.ExceptionResponse;
import io.github.seayar.modbus4j.msg.FileRecord;
import io.github.seayar.modbus4j.msg.ReadCoilsRequest;
import io.github.seayar.modbus4j.msg.ReadCoilsResponse;
import io.github.seayar.modbus4j.msg.ReadDiscreteInputsRequest;
import io.github.seayar.modbus4j.msg.ReadDiscreteInputsResponse;
import io.github.seayar.modbus4j.msg.ReadExceptionStatusRequest;
import io.github.seayar.modbus4j.msg.ReadExceptionStatusResponse;
import io.github.seayar.modbus4j.msg.ReadFileRecordRequest;
import io.github.seayar.modbus4j.msg.ReadFileRecordResponse;
import io.github.seayar.modbus4j.msg.ReadHoldingRegistersRequest;
import io.github.seayar.modbus4j.msg.ReadHoldingRegistersResponse;
import io.github.seayar.modbus4j.msg.ReadInputRegistersRequest;
import io.github.seayar.modbus4j.msg.ReadInputRegistersResponse;
import io.github.seayar.modbus4j.msg.ReadResponse;
import io.github.seayar.modbus4j.msg.ReadWriteMultipleRegistersRequest;
import io.github.seayar.modbus4j.msg.ReadWriteMultipleRegistersResponse;
import io.github.seayar.modbus4j.msg.ReportSlaveIdRequest;
import io.github.seayar.modbus4j.msg.ReportSlaveIdResponse;
import io.github.seayar.modbus4j.msg.WriteCoilRequest;
import io.github.seayar.modbus4j.msg.WriteCoilsRequest;
import io.github.seayar.modbus4j.msg.WriteFileRecordRequest;
import io.github.seayar.modbus4j.msg.WriteFileRecordResponse;
import io.github.seayar.modbus4j.msg.WriteMaskRegisterRequest;
import io.github.seayar.modbus4j.msg.WriteRegisterRequest;
import io.github.seayar.modbus4j.msg.WriteRegistersRequest;
import io.github.seayar.modbus4j.msg.WriteResponse;
import io.github.seayar.modbus4j.transport.ModbusTransport;

import java.util.Collections;
import java.util.List;

public abstract class ModbusMaster {
    protected final ModbusTransport transport;
    protected final boolean validateResponse;

    protected ModbusMaster(ModbusTransport transport, boolean validateResponse) {
        this.transport = transport;
        this.validateResponse = validateResponse;
    }

    public void init() throws ModbusInitException {
        transport.init();
    }

    public void destroy() {
        transport.destroy();
    }

    public ModbusTransport getTransport() {
        return transport;
    }

    public boolean isInitialized() {
        return transport.isInitialized();
    }

    public <T> T getValue(BaseLocator<T> locator) throws ModbusTransportException, ModbusCodeException {
        ModbusLocator<T> l = locator;
        ReadResponse response;
        if (l instanceof NumericLocator || l instanceof StringLocator) {
            int range = l.getRange();
            int count = l.getRegisterCount();
            if (range == RegisterRange.HOLDING_REGISTER)
                response = read(new ReadHoldingRegistersRequest(l.getSlaveId(),
                        l.getOffset(), count));
            else if (range == RegisterRange.INPUT_REGISTER)
                response = read(new ReadInputRegistersRequest(l.getSlaveId(),
                        l.getOffset(), count));
            else
                throw new ModbusTransportException("Unsupported range for numeric locator: " + range);
            return locator.bytesToValue(response.getData(), locator.getOffset());
        }
        if (l instanceof BinaryLocator) {
            int range = l.getRange();
            if (range == RegisterRange.COIL_STATUS)
                response = read(new ReadCoilsRequest(l.getSlaveId(), l.getOffset(), 1));
            else if (range == RegisterRange.INPUT_STATUS)
                response = read(new ReadDiscreteInputsRequest(l.getSlaveId(), l.getOffset(),
                        1));
            else
                response = read(new ReadHoldingRegistersRequest(l.getSlaveId(), l.getOffset(),
                        1));
            return locator.bytesToValue(response.getData(), locator.getOffset());
        }
        throw new ModbusTransportException("Unsupported locator: " + locator);
    }

    private ReadResponse read(AbstractModbusRequest request) throws ModbusTransportException, ModbusCodeException {
        AbstractModbusResponse response = request(request);
        checkException(response);
        return (ReadResponse) response;
    }

    public <T> void setValue(BaseLocator<T> locator, T value) throws ModbusTransportException, ModbusCodeException {
        int range = locator.getRange();
        int slaveId = locator.getSlaveId();
        int offset = locator.getOffset();
        if (locator instanceof NumericLocator && value instanceof Number) {
            short[] shorts = ((NumericLocator) locator).valueToShorts((Number) value);
            AbstractModbusResponse response;
            if (range != RegisterRange.HOLDING_REGISTER)
                throw new ModbusTransportException("Only holding registers can be written");
            if (shorts.length == 1)
                response = request(new WriteRegisterRequest(slaveId, offset, shorts[0] & 0xffff));
            else
                response = request(new WriteRegistersRequest(slaveId, offset, shortsToBytes(shorts)));
            checkException(response);
        } else if (locator instanceof StringLocator && value instanceof String) {
            if (range != RegisterRange.HOLDING_REGISTER)
                throw new ModbusTransportException("Only holding registers can be written");
            short[] shorts = ((StringLocator) locator).valueToShorts((String) value);
            checkException(request(new WriteRegistersRequest(slaveId, offset, shortsToBytes(shorts))));
        } else if (locator instanceof BinaryLocator && value instanceof Boolean) {
            if (range == RegisterRange.COIL_STATUS)
                checkException(request(new WriteCoilRequest(slaveId, offset, (Boolean) value)));
            else if (range == RegisterRange.HOLDING_REGISTER)
                writeBit(slaveId, offset, ((BinaryLocator) locator).getBit(), (Boolean) value);
            else
                throw new ModbusTransportException("Unsupported write range: " + range);
        } else {
            throw new ModbusTransportException("Unsupported locator/value: " + locator + " / " + value);
        }
    }

    private void writeBit(int slaveId, int offset, int bit, boolean value)
            throws ModbusTransportException, ModbusCodeException {
        ReadHoldingRegistersResponse read = (ReadHoldingRegistersResponse) request(
                new ReadHoldingRegistersRequest(slaveId, offset, 1));
        checkException(read);
        short reg = (short) (((read.getData()[0] & 0xff) << 8) | (read.getData()[1] & 0xff));
        if (value)
            reg |= (short) (1 << bit);
        else
            reg &= (short) ~(1 << bit);
        checkException(request(new WriteRegisterRequest(slaveId, offset, reg & 0xffff)));
    }

    protected byte[] shortsToBytes(short[] shorts) {
        byte[] bytes = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            bytes[i * 2] = (byte) (shorts[i] >> 8);
            bytes[i * 2 + 1] = (byte) (shorts[i] & 0xff);
        }
        return bytes;
    }

    public <K> BatchResults<K> send(BatchRead<K> batch) throws ModbusTransportException, ModbusCodeException {
        return send(batch, false);
    }

    public <K> BatchResults<K> send(BatchRead<K> batch, boolean retry)
            throws ModbusTransportException, ModbusCodeException {
        return send(batch, retry, true);
    }

    public <K> BatchResults<K> send(BatchRead<K> batch, boolean retry, boolean primary)
            throws ModbusTransportException, ModbusCodeException {
        BatchResults<K> results = new BatchResults<>();
        if (batch.isCancel())
            return results;
        for (ReadFunctionGroup<K> group : batch.getReadFunctionGroups()) {
            if (batch.isCancel())
                break;
            sendReadGroup(batch, group, results);
        }
        return results;
    }

    private <K> void sendReadGroup(BatchRead<K> batch, ReadFunctionGroup<K> group, BatchResults<K> results)
            throws ModbusTransportException, ModbusCodeException {
        if (batch.isCancel())
            return;
        AbstractModbusRequest request = createReadRequest(group);
        AbstractModbusResponse response = request(request, false);
        if (!(response instanceof ExceptionResponse)) {
            processGroupResponse(batch, group, response, results);
            return;
        }
        List<KeyedModbusLocator<K>> locators = group.getLocators();
        if (batch.isSplitOnException() && locators.size() > 1) {
            int mid = locators.size() / 2;
            sendReadGroup(batch, buildGroup(locators.subList(0, mid)), results);
            sendReadGroup(batch, buildGroup(locators.subList(mid, locators.size())), results);
        } else if (batch.isSplitOnException()) {
            results.setError(locators.get(0).getKey());
        } else if (batch.isExceptionsInResults()) {
            for (KeyedModbusLocator<K> locator : locators)
                results.setError(locator.getKey());
        } else {
            throw ((ExceptionResponse) response).toException();
        }
    }

    private <K> ReadFunctionGroup<K> buildGroup(List<KeyedModbusLocator<K>> locators) {
        ReadFunctionGroup<K> group = new ReadFunctionGroup<>(locators.get(0));
        for (int i = 1; i < locators.size(); i++)
            group.add(locators.get(i));
        return group;
    }

    private AbstractModbusRequest createReadRequest(ReadFunctionGroup<?> group) {
        int slaveId = group.getSlaveId();
        int startOffset = group.getStartOffset();
        int endOffset = group.getEndOffset();
        switch (group.getFunctionCode()) {
            case 1:
                return new ReadCoilsRequest(slaveId, startOffset, endOffset - startOffset + 1);
            case 2:
                return new ReadDiscreteInputsRequest(slaveId, startOffset, endOffset - startOffset + 1);
            case 3:
                return new ReadHoldingRegistersRequest(slaveId, startOffset, endOffset - startOffset + 1);
            case 4:
                return new ReadInputRegistersRequest(slaveId, startOffset, endOffset - startOffset + 1);
        }
        throw new IllegalArgumentException("Unsupported function code: " + group.getFunctionCode());
    }

    private <K> void processGroupResponse(BatchRead<K> batch, ReadFunctionGroup<K> group,
            AbstractModbusResponse response, BatchResults<K> results) {
        if (response instanceof ExceptionResponse) {
            if (batch.isExceptionsInResults()) {
                for (KeyedModbusLocator<K> locator : group.getLocators())
                    results.setError(locator.getKey());
            } else {
                throw ((ExceptionResponse) response).toException();
            }
        } else {
            ReadResponse readResponse = (ReadResponse) response;
            for (KeyedModbusLocator<K> keyed : group.getLocators()) {
                Object value = keyed.getLocator().bytesToValue(readResponse.getData(), group.getStartOffset());
                results.setValue(keyed.getKey(), value);
            }
        }
    }

    private void checkException(AbstractModbusResponse response) throws ModbusCodeException {
        if (response instanceof ExceptionResponse)
            throw ((ExceptionResponse) response).toException();
    }

    public byte getExceptionStatus(int slaveId) throws ModbusTransportException, ModbusCodeException {
        AbstractModbusResponse response = request(new ReadExceptionStatusRequest(slaveId));
        checkException(response);
        return (byte) ((ReadExceptionStatusResponse) response).getExceptionStatus();
    }

    public byte[] reportSlaveId(int slaveId) throws ModbusTransportException, ModbusCodeException {
        AbstractModbusResponse response = request(new ReportSlaveIdRequest(slaveId));
        checkException(response);
        return ((ReportSlaveIdResponse) response).getData();
    }

    public List<FileRecord> readFileRecords(int slaveId, List<FileRecord> records)
            throws ModbusTransportException, ModbusCodeException {
        AbstractModbusResponse response = request(new ReadFileRecordRequest(slaveId, records));
        checkException(response);
        return ((ReadFileRecordResponse) response).getFileData();
    }

    public byte[] readFileRecord(int slaveId, int fileNumber, int recordNumber, int recordLength)
            throws ModbusTransportException, ModbusCodeException {
        return readFileRecords(slaveId, Collections.singletonList(
                new FileRecord(fileNumber, recordNumber, recordLength))).get(0).getData();
    }

    public void writeFileRecords(int slaveId, List<FileRecord> records)
            throws ModbusTransportException, ModbusCodeException {
        checkException(request(new WriteFileRecordRequest(slaveId, records)));
    }

    public void writeFileRecord(int slaveId, int fileNumber, int recordNumber, byte[] data)
            throws ModbusTransportException, ModbusCodeException {
        writeFileRecords(slaveId, Collections.singletonList(new FileRecord(fileNumber, recordNumber, data)));
    }

    public void writeMaskRegister(int slaveId, int offset, int andMask, int orMask)
            throws ModbusTransportException, ModbusCodeException {
        checkException(request(new WriteMaskRegisterRequest(slaveId, offset, andMask, orMask)));
    }

    public byte[] readWriteMultipleRegisters(int slaveId, int readStartOffset, int readQuantity,
            int writeStartOffset, byte[] writeData) throws ModbusTransportException, ModbusCodeException {
        AbstractModbusResponse response = request(new ReadWriteMultipleRegistersRequest(slaveId, readStartOffset,
                readQuantity, writeStartOffset, writeData));
        checkException(response);
        return ((ReadWriteMultipleRegistersResponse) response).getData();
    }

    protected AbstractModbusResponse request(AbstractModbusRequest request)
            throws ModbusTransportException, ModbusCodeException {
        return request(request, true);
    }

    protected AbstractModbusResponse request(AbstractModbusRequest request, boolean validate)
            throws ModbusTransportException, ModbusCodeException {
        AbstractModbusResponse response = transport.send(request);
        if (validate && validateResponse && response.isException())
            throw ((ExceptionResponse) response).toException();
        return response;
    }
}
