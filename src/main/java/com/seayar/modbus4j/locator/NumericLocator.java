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
package com.seayar.modbus4j.locator;

import com.seayar.modbus4j.base.DataType;
import com.seayar.modbus4j.base.RegisterRange;

public class NumericLocator extends BaseLocator<Number> {

    private final int dataType;

    public NumericLocator(int slaveId, int range, int offset, int dataType) {
        super(slaveId, range, offset);
        this.dataType = dataType;
        validate(getRegisterCount());
    }

    @Override
    public int getDataType() {
        return dataType;
    }

    @Override
    public int getRegisterCount() {
        return DataType.getRegisterCount(dataType);
    }

    @Override
    public Number bytesToValueRealOffset(byte[] data, int offset) {
        offset *= 2;
        if (dataType == DataType.TWO_BYTE_INT_UNSIGNED)
            return ((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff);
        if (dataType == DataType.TWO_BYTE_INT_SIGNED)
            return (short) (((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff));
        if (dataType == DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED)
            return ((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff);
        if (dataType == DataType.TWO_BYTE_INT_SIGNED_SWAPPED)
            return (short) (((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff));
        if (dataType == DataType.ONE_BYTE_INT_UNSIGNED_LOWER)
            return data[offset + 1] & 0xff;
        if (dataType == DataType.ONE_BYTE_INT_UNSIGNED_UPPER)
            return data[offset] & 0xff;
        if (dataType == DataType.FOUR_BYTE_INT_UNSIGNED)
            return ((long) (data[offset] & 0xff) << 24) | ((long) (data[offset + 1] & 0xff) << 16)
                    | ((long) (data[offset + 2] & 0xff) << 8) | (data[offset + 3] & 0xff);
        if (dataType == DataType.FOUR_BYTE_INT_SIGNED)
            return ((data[offset] & 0xff) << 24) | ((data[offset + 1] & 0xff) << 16)
                    | ((data[offset + 2] & 0xff) << 8) | (data[offset + 3] & 0xff);
        if (dataType == DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED)
            return ((long) (data[offset + 2] & 0xff) << 24) | ((long) (data[offset + 3] & 0xff) << 16)
                    | ((long) (data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff);
        if (dataType == DataType.FOUR_BYTE_INT_SIGNED_SWAPPED)
            return ((data[offset + 2] & 0xff) << 24) | ((data[offset + 3] & 0xff) << 16)
                    | ((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff);
        if (dataType == DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED)
            return ((long) (data[offset + 3] & 0xff) << 24) | ((long) (data[offset + 2] & 0xff) << 16)
                    | ((long) (data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff);
        if (dataType == DataType.FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED)
            return ((data[offset + 3] & 0xff) << 24) | ((data[offset + 2] & 0xff) << 16)
                    | ((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff);
        if (dataType == DataType.FOUR_BYTE_FLOAT)
            return Float.intBitsToFloat(((data[offset] & 0xff) << 24) | ((data[offset + 1] & 0xff) << 16)
                    | ((data[offset + 2] & 0xff) << 8) | (data[offset + 3] & 0xff));
        if (dataType == DataType.FOUR_BYTE_FLOAT_SWAPPED)
            return Float.intBitsToFloat(((data[offset + 2] & 0xff) << 24) | ((data[offset + 3] & 0xff) << 16)
                    | ((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff));
        if (dataType == DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED)
            return Float.intBitsToFloat(((data[offset + 2] & 0xff) << 24) | ((data[offset + 3] & 0xff) << 16)
                    | ((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff));
        if (dataType == DataType.EIGHT_BYTE_INT_SIGNED)
            return ((long) (data[offset] & 0xff) << 56) | ((long) (data[offset + 1] & 0xff) << 48)
                    | ((long) (data[offset + 2] & 0xff) << 40) | ((long) (data[offset + 3] & 0xff) << 32)
                    | ((long) (data[offset + 4] & 0xff) << 24) | ((long) (data[offset + 5] & 0xff) << 16)
                    | ((long) (data[offset + 6] & 0xff) << 8) | (data[offset + 7] & 0xff);
        if (dataType == DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED)
            return ((long) (data[offset + 6] & 0xff) << 56) | ((long) (data[offset + 7] & 0xff) << 48)
                    | ((long) (data[offset + 4] & 0xff) << 40) | ((long) (data[offset + 5] & 0xff) << 32)
                    | ((long) (data[offset + 2] & 0xff) << 24) | ((long) (data[offset + 3] & 0xff) << 16)
                    | ((long) (data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff);
        if (dataType == DataType.EIGHT_BYTE_FLOAT)
            return Double.longBitsToDouble(((long) (data[offset] & 0xff) << 56)
                    | ((long) (data[offset + 1] & 0xff) << 48) | ((long) (data[offset + 2] & 0xff) << 40)
                    | ((long) (data[offset + 3] & 0xff) << 32) | ((long) (data[offset + 4] & 0xff) << 24)
                    | ((long) (data[offset + 5] & 0xff) << 16) | ((long) (data[offset + 6] & 0xff) << 8)
                    | (data[offset + 7] & 0xff));
        if (dataType == DataType.EIGHT_BYTE_FLOAT_SWAPPED)
            return Double.longBitsToDouble(((long) (data[offset + 6] & 0xff) << 56)
                    | ((long) (data[offset + 7] & 0xff) << 48) | ((long) (data[offset + 4] & 0xff) << 40)
                    | ((long) (data[offset + 5] & 0xff) << 32) | ((long) (data[offset + 2] & 0xff) << 24)
                    | ((long) (data[offset + 3] & 0xff) << 16) | ((long) (data[offset] & 0xff) << 8)
                    | (data[offset + 1] & 0xff));
        throw new IllegalArgumentException("Unsupported data type: " + dataType);
    }

    @Override
    public short[] valueToShorts(Number value) {
        if (dataType == DataType.TWO_BYTE_INT_UNSIGNED || dataType == DataType.TWO_BYTE_INT_SIGNED)
            return new short[]{toShort(value)};
        if (dataType == DataType.TWO_BYTE_INT_SIGNED_SWAPPED || dataType == DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED) {
            short sval = toShort(value);
            return new short[]{(short) (((sval & 0xff00) >> 8) | ((sval & 0x00ff) << 8))};
        }
        if (dataType == DataType.ONE_BYTE_INT_UNSIGNED_LOWER)
            return new short[]{(short) (toShort(value) & 0x00ff)};
        if (dataType == DataType.ONE_BYTE_INT_UNSIGNED_UPPER)
            return new short[]{(short) ((toShort(value) << 8) & 0xff00)};
        if (dataType == DataType.FOUR_BYTE_INT_UNSIGNED || dataType == DataType.FOUR_BYTE_INT_SIGNED) {
            int i = value.intValue();
            return new short[]{(short) (i >> 16), (short) i};
        }
        if (dataType == DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED || dataType == DataType.FOUR_BYTE_INT_SIGNED_SWAPPED) {
            int i = value.intValue();
            return new short[]{(short) i, (short) (i >> 16)};
        }
        if (dataType == DataType.FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED
                || dataType == DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED) {
            int i = value.intValue();
            short topWord = (short) (((i & 0xff) << 8) | ((i >> 8) & 0xff));
            short bottomWord = (short) (((i >> 24) & 0x000000ff) | ((i >> 8) & 0x0000ff00));
            return new short[]{topWord, bottomWord};
        }
        if (dataType == DataType.FOUR_BYTE_FLOAT) {
            int i = Float.floatToIntBits(value.floatValue());
            return new short[]{(short) (i >> 16), (short) i};
        }
        if (dataType == DataType.FOUR_BYTE_FLOAT_SWAPPED || dataType == DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED) {
            int i = Float.floatToIntBits(value.floatValue());
            return new short[]{(short) i, (short) (i >> 16)};
        }
        if (dataType == DataType.EIGHT_BYTE_INT_UNSIGNED || dataType == DataType.EIGHT_BYTE_INT_SIGNED) {
            long l = value.longValue();
            return new short[]{(short) (l >> 48), (short) (l >> 32), (short) (l >> 16), (short) l};
        }
        if (dataType == DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED || dataType == DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED) {
            long l = value.longValue();
            return new short[]{(short) l, (short) (l >> 16), (short) (l >> 32), (short) (l >> 48)};
        }
        if (dataType == DataType.EIGHT_BYTE_FLOAT) {
            long l = Double.doubleToLongBits(value.doubleValue());
            return new short[]{(short) (l >> 48), (short) (l >> 32), (short) (l >> 16), (short) l};
        }
        if (dataType == DataType.EIGHT_BYTE_FLOAT_SWAPPED) {
            long l = Double.doubleToLongBits(value.doubleValue());
            return new short[]{(short) l, (short) (l >> 16), (short) (l >> 32), (short) (l >> 48)};
        }
        throw new IllegalArgumentException("Unsupported data type: " + dataType);
    }

    private short toShort(Number value) {
        return (short) value.intValue();
    }

    @Override
    public String toString() {
        return "NumericLocator(slaveId=" + getSlaveId() + ", range=" + range + ", offset=" + offset + ", dataType="
                + dataType + ")";
    }
}
