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
package com.seayar.modbus4j.msg;

import com.seayar.modbus4j.base.FunctionCode;
import io.netty.buffer.ByteBuf;

public final class MessageUtil {
    private MessageUtil() {}

    public static AbstractModbusResponse createResponse(AbstractModbusRequest request, ByteBuf data) {
        byte functionCode = data.readByte();
        int slaveId = request.getSlaveId();
        if (FunctionCode.isException(functionCode))
            return new ExceptionResponse(slaveId, data.readByte() & 0xff);
        switch (functionCode) {
            case FunctionCode.READ_COILS:
                return new ReadCoilsResponse(slaveId, data);
            case FunctionCode.READ_DISCRETE_INPUTS:
                return new ReadDiscreteInputsResponse(slaveId, data);
            case FunctionCode.READ_HOLDING_REGISTERS:
                return new ReadHoldingRegistersResponse(slaveId, data);
            case FunctionCode.READ_INPUT_REGISTERS:
                return new ReadInputRegistersResponse(slaveId, data);
            case FunctionCode.WRITE_COIL:
                return new WriteCoilResponse(slaveId, data);
            case FunctionCode.WRITE_REGISTER:
                return new WriteRegisterResponse(slaveId, data);
            case FunctionCode.WRITE_COILS:
                return new WriteCoilsResponse(slaveId, data);
            case FunctionCode.WRITE_REGISTERS:
                return new WriteRegistersResponse(slaveId, data);
            case FunctionCode.READ_EXCEPTION_STATUS:
                return new ReadExceptionStatusResponse(slaveId, data);
            case FunctionCode.REPORT_SLAVE_ID:
                return new ReportSlaveIdResponse(slaveId, data);
            case FunctionCode.READ_FILE_RECORD:
                return new ReadFileRecordResponse(slaveId, data);
            case FunctionCode.WRITE_FILE_RECORD:
                return new WriteFileRecordResponse(slaveId, data);
            case FunctionCode.WRITE_MASK_REGISTER:
                return new WriteMaskRegisterResponse(slaveId, data);
            case FunctionCode.READ_WRITE_MULTIPLE_REGISTERS:
                return new ReadWriteMultipleRegistersResponse(slaveId, data);
        }
        throw new IllegalArgumentException("Unsupported function code: " + (functionCode & 0xff));
    }

    public static AbstractModbusResponse createResponse(int slaveId, byte functionCode, ByteBuf data) {
        if (FunctionCode.isException(functionCode))
            return new ExceptionResponse(slaveId, data.readByte() & 0xff);
        switch (functionCode) {
            case FunctionCode.READ_COILS:
                return new ReadCoilsResponse(slaveId, data);
            case FunctionCode.READ_DISCRETE_INPUTS:
                return new ReadDiscreteInputsResponse(slaveId, data);
            case FunctionCode.READ_HOLDING_REGISTERS:
                return new ReadHoldingRegistersResponse(slaveId, data);
            case FunctionCode.READ_INPUT_REGISTERS:
                return new ReadInputRegistersResponse(slaveId, data);
            case FunctionCode.WRITE_COIL:
                return new WriteCoilResponse(slaveId, data);
            case FunctionCode.WRITE_REGISTER:
                return new WriteRegisterResponse(slaveId, data);
            case FunctionCode.WRITE_COILS:
                return new WriteCoilsResponse(slaveId, data);
            case FunctionCode.WRITE_REGISTERS:
                return new WriteRegistersResponse(slaveId, data);
            case FunctionCode.READ_EXCEPTION_STATUS:
                return new ReadExceptionStatusResponse(slaveId, data);
            case FunctionCode.REPORT_SLAVE_ID:
                return new ReportSlaveIdResponse(slaveId, data);
            case FunctionCode.READ_FILE_RECORD:
                return new ReadFileRecordResponse(slaveId, data);
            case FunctionCode.WRITE_FILE_RECORD:
                return new WriteFileRecordResponse(slaveId, data);
            case FunctionCode.WRITE_MASK_REGISTER:
                return new WriteMaskRegisterResponse(slaveId, data);
            case FunctionCode.READ_WRITE_MULTIPLE_REGISTERS:
                return new ReadWriteMultipleRegistersResponse(slaveId, data);
        }
        throw new IllegalArgumentException("Unsupported function code: " + (functionCode & 0xff));
    }
}
