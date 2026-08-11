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

import java.math.BigInteger;

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
        if (dataType == DataType.TWO_BYTE_BCD)
            return (short) (bcdToInt(data[offset]) * 100 + bcdToInt(data[offset + 1]));
        if (dataType == DataType.FOUR_BYTE_BCD)
            return bcdToInt(data[offset]) * 1000000 + bcdToInt(data[offset + 1]) * 10000
                    + bcdToInt(data[offset + 2]) * 100 + bcdToInt(data[offset + 3]);
        if (dataType == DataType.FOUR_BYTE_BCD_SWAPPED)
            return bcdToInt(data[offset + 2]) * 1000000 + bcdToInt(data[offset + 3]) * 10000
                    + bcdToInt(data[offset]) * 100 + bcdToInt(data[offset + 1]);
        if (dataType == DataType.FOUR_BYTE_MOD_10K)
            return mod10kToValue(data, offset, 2, false);
        if (dataType == DataType.FOUR_BYTE_MOD_10K_SWAPPED)
            return mod10kToValue(data, offset, 2, true);
        if (dataType == DataType.SIX_BYTE_MOD_10K)
            return mod10kToValue(data, offset, 3, false);
        if (dataType == DataType.SIX_BYTE_MOD_10K_SWAPPED)
            return mod10kToValue(data, offset, 3, true);
        if (dataType == DataType.EIGHT_BYTE_MOD_10K)
            return mod10kToValue(data, offset, 4, false);
        if (dataType == DataType.EIGHT_BYTE_MOD_10K_SWAPPED)
            return mod10kToValue(data, offset, 4, true);
        if (dataType == DataType.EIGHT_BYTE_INT_UNSIGNED)
            return bytesToUnsignedLong(data, offset, false);
        if (dataType == DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED)
            return bytesToUnsignedLong(data, offset, true);
        throw new IllegalArgumentException("Unsupported data type: " + dataType);
    }

    private int bcdToInt(byte b) {
        return ((b >> 4) & 0x0f) * 10 + (b & 0x0f);
    }

    private BigInteger mod10kToValue(byte[] data, int offset, int registerCount, boolean swapped) {
        BigInteger value = BigInteger.ZERO;
        for (int i = 0; i < registerCount; i++) {
            int index = swapped ? registerCount - 1 - i : i;
            int register = ((data[offset + index * 2] & 0xff) << 8) | (data[offset + index * 2 + 1] & 0xff);
            value = value.multiply(BigInteger.valueOf(10000)).add(BigInteger.valueOf(register));
        }
        return value;
    }

    private BigInteger bytesToUnsignedLong(byte[] data, int offset, boolean swapped) {
        byte[] bytes = new byte[8];
        int[] order = swapped ? new int[]{6, 7, 4, 5, 2, 3, 0, 1} : new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        for (int i = 0; i < 8; i++)
            bytes[i] = data[offset + order[i]];
        return new BigInteger(1, bytes);
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
        if (dataType == DataType.TWO_BYTE_BCD)
            return new short[]{bcdEncode(value.shortValue() & 0xffff, 4)};
        if (dataType == DataType.FOUR_BYTE_BCD)
            return bytesToShorts(bcdEncodeBytes(value.intValue(), 8), false);
        if (dataType == DataType.FOUR_BYTE_BCD_SWAPPED)
            return bytesToShorts(bcdEncodeBytes(value.intValue(), 8), true);
        if (dataType == DataType.FOUR_BYTE_MOD_10K)
            return valueToMod10k(toBigInteger(value), 2, false);
        if (dataType == DataType.FOUR_BYTE_MOD_10K_SWAPPED)
            return valueToMod10k(toBigInteger(value), 2, true);
        if (dataType == DataType.SIX_BYTE_MOD_10K)
            return valueToMod10k(toBigInteger(value), 3, false);
        if (dataType == DataType.SIX_BYTE_MOD_10K_SWAPPED)
            return valueToMod10k(toBigInteger(value), 3, true);
        if (dataType == DataType.EIGHT_BYTE_MOD_10K)
            return valueToMod10k(toBigInteger(value), 4, false);
        if (dataType == DataType.EIGHT_BYTE_MOD_10K_SWAPPED)
            return valueToMod10k(toBigInteger(value), 4, true);
        throw new IllegalArgumentException("Unsupported data type: " + dataType);
    }

    private short bcdEncode(int value, int digitCount) {
        byte[] bytes = bcdEncodeBytes(value, digitCount);
        return (short) (((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
    }

    private byte[] bcdEncodeBytes(int value, int digitCount) {
        if (value < 0)
            throw new IllegalArgumentException("BCD value must be non-negative: " + value);
        long max = 1;
        for (int i = 0; i < digitCount; i++)
            max *= 10;
        if (value >= max)
            throw new IllegalArgumentException("BCD value out of range for " + digitCount + " digits: " + value);
        byte[] bytes = new byte[digitCount / 2];
        for (int i = digitCount / 2 - 1; i >= 0; i--) {
            int low = value % 10;
            value /= 10;
            int high = value % 10;
            value /= 10;
            bytes[i] = (byte) ((high << 4) | low);
        }
        return bytes;
    }

    private short[] bytesToShorts(byte[] bytes, boolean swapped) {
        short[] result = new short[bytes.length / 2];
        for (int i = 0; i < result.length; i++) {
            int src = swapped ? result.length - 1 - i : i;
            result[i] = (short) (((bytes[src * 2] & 0xff) << 8) | (bytes[src * 2 + 1] & 0xff));
        }
        return result;
    }

    private short[] valueToMod10k(BigInteger value, int registerCount, boolean swapped) {
        short[] result = new short[registerCount];
        for (int i = registerCount - 1; i >= 0; i--) {
            BigInteger[] qr = value.divideAndRemainder(BigInteger.valueOf(10000));
            result[i] = qr[1].shortValue();
            value = qr[0];
        }
        if (swapped) {
            for (int i = 0; i < result.length / 2; i++) {
                short tmp = result[i];
                result[i] = result[result.length - 1 - i];
                result[result.length - 1 - i] = tmp;
            }
        }
        return result;
    }

    private BigInteger toBigInteger(Number value) {
        if (value instanceof BigInteger)
            return (BigInteger) value;
        return BigInteger.valueOf(value.longValue());
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
