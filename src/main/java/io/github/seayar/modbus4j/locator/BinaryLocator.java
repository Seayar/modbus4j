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

public class BinaryLocator extends BaseLocator<Boolean> {
    private final int bit;

    public BinaryLocator(int slaveId, int range, int offset) {
        super(slaveId, range, offset);
        if (!isBinaryRange(range))
            throw new IllegalArgumentException(
                    "Non-bit requests can only be made from coil status and input status ranges");
        this.bit = -1;
        validate(1);
    }

    public BinaryLocator(int slaveId, int range, int offset, int bit) {
        super(slaveId, range, offset);
        if (isBinaryRange(range))
            throw new IllegalArgumentException(
                    "Bit requests can only be made from holding registers and input registers");
        this.bit = bit;
        validate(1);
    }

    public static boolean isBinaryRange(int range) {
        return range == RegisterRange.COIL_STATUS || range == RegisterRange.INPUT_STATUS;
    }

    public int getBit() {
        return bit;
    }

    @Override
    public int getDataType() {
        return DataType.BINARY;
    }

    @Override
    public int getRegisterCount() {
        return 1;
    }

    @Override
    public Boolean bytesToValueRealOffset(byte[] data, int offset) {
        if (range == RegisterRange.COIL_STATUS || range == RegisterRange.INPUT_STATUS)
            return ((data[offset / 8] & 0xff) >> (offset % 8) & 0x1) == 1;
        offset *= 2;
        return ((data[offset + 1 - bit / 8] & 0xff) >> (bit % 8) & 0x1) == 1;
    }

    @Override
    public short[] valueToShorts(Boolean value) {
        return value ? new short[]{1} : new short[]{0};
    }

    @Override
    public String toString() {
        return "BinaryLocator(slaveId=" + getSlaveId() + ", range=" + range + ", offset=" + offset + ", bit=" + bit
                + ")";
    }
}
