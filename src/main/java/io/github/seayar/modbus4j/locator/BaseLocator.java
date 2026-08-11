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
package io.github.seayar.modbus4j.locator;

import io.github.seayar.modbus4j.base.DataType;
import io.github.seayar.modbus4j.base.RegisterRange;
import io.github.seayar.modbus4j.base.SlaveAndRange;

import java.nio.charset.Charset;

public abstract class BaseLocator<T> implements ModbusLocator<T> {

    public static BaseLocator<Boolean> coilStatus(int slaveId, int offset) {
        return new BinaryLocator(slaveId, RegisterRange.COIL_STATUS, offset);
    }

    public static BaseLocator<Boolean> inputStatus(int slaveId, int offset) {
        return new BinaryLocator(slaveId, RegisterRange.INPUT_STATUS, offset);
    }

    public static BaseLocator<Number> inputRegister(int slaveId, int offset, int dataType) {
        return new NumericLocator(slaveId, RegisterRange.INPUT_REGISTER, offset, dataType);
    }

    public static BaseLocator<Boolean> inputRegisterBit(int slaveId, int offset, int bit) {
        return new BinaryLocator(slaveId, RegisterRange.INPUT_REGISTER, offset, bit);
    }

    public static BaseLocator<Number> holdingRegister(int slaveId, int offset, int dataType) {
        return new NumericLocator(slaveId, RegisterRange.HOLDING_REGISTER, offset, dataType);
    }

    public static BaseLocator<Boolean> holdingRegisterBit(int slaveId, int offset, int bit) {
        return new BinaryLocator(slaveId, RegisterRange.HOLDING_REGISTER, offset, bit);
    }

    public static BaseLocator<String> holdingRegisterString(int slaveId, int offset, int dataType,
            int registerCount) {
        return new StringLocator(slaveId, RegisterRange.HOLDING_REGISTER, offset, dataType, registerCount);
    }

    public static BaseLocator<String> inputRegisterString(int slaveId, int offset, int dataType,
            int registerCount) {
        return new StringLocator(slaveId, RegisterRange.INPUT_REGISTER, offset, dataType, registerCount);
    }

    public static BaseLocator<?> createLocator(int slaveId, int registerId, int dataType, int bit,
            int registerCount) {
        return createLocator(slaveId, registerId, dataType, bit, registerCount, StringLocator.ASCII);
    }

    public static BaseLocator<?> createLocator(int slaveId, int registerId, int dataType, int bit,
            int registerCount, Charset charset) {
        RangeAndOffset rao = new RangeAndOffset(registerId);
        return createLocator(slaveId, rao.getRange(), rao.getOffset(), dataType, bit, registerCount, charset);
    }

    public static BaseLocator<?> createLocator(int slaveId, int range, int offset, int dataType, int bit,
            int registerCount) {
        return createLocator(slaveId, range, offset, dataType, bit, registerCount, StringLocator.ASCII);
    }

    public static BaseLocator<?> createLocator(int slaveId, int range, int offset, int dataType, int bit,
            int registerCount, Charset charset) {
        if (dataType == DataType.BINARY) {
            if (BinaryLocator.isBinaryRange(range))
                return new BinaryLocator(slaveId, range, offset);
            return new BinaryLocator(slaveId, range, offset, bit);
        }
        if (dataType == DataType.CHAR || dataType == DataType.VARCHAR)
            return new StringLocator(slaveId, range, offset, dataType, registerCount, charset);
        return new NumericLocator(slaveId, range, offset, dataType);
    }

    private final int slaveId;
    protected final int range;
    protected final int offset;

    protected BaseLocator(int slaveId, int range, int offset) {
        this.slaveId = slaveId;
        this.range = range;
        this.offset = offset;
    }

    protected void validate(int registerCount) {
        validateOffset(offset);
        validateEndOffset(offset + registerCount - 1);
    }

    public static void validateOffset(int offset) {
        if (offset < 0 || offset > 65535)
            throw new IllegalArgumentException("Invalid offset: " + offset);
    }

    public static void validateEndOffset(int offset) {
        if (offset > 65535)
            throw new IllegalArgumentException("Invalid end offset: " + offset);
    }

    @Override
    public int getSlaveId() {
        return slaveId;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getEndOffset() {
        return offset + getRegisterCount() - 1;
    }

    @Override
    public SlaveAndRange getSlaveAndRange() {
        return new SlaveAndRange(slaveId, range);
    }

    @Override
    public T bytesToValue(byte[] data, int requestOffset) {
        return bytesToValueRealOffset(data, offset - requestOffset);
    }
}
