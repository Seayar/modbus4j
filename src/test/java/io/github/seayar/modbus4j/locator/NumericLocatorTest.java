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
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class NumericLocatorTest {

    private static byte[] shorts(short... values) {
        byte[] data = new byte[values.length * 2];
        for (int i = 0; i < values.length; i++) {
            data[i * 2] = (byte) (values[i] >> 8);
            data[i * 2 + 1] = (byte) (values[i] & 0xff);
        }
        return data;
    }

    @Test
    public void testGetDataType() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_FLOAT);
        assertEquals(DataType.FOUR_BYTE_FLOAT, locator.getDataType());
    }

    @Test
    public void testTwoByteIntUnsigned() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.TWO_BYTE_INT_UNSIGNED);
        byte[] data = shorts((short) 0xffff);
        assertEquals(65535, locator.bytesToValue(data, 0).intValue());
        assertEquals(0xffff, locator.valueToShorts(65535)[0] & 0xffff);
    }

    @Test
    public void testTwoByteIntSigned() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.TWO_BYTE_INT_SIGNED);
        byte[] data = shorts((short) 0xfffe);
        assertEquals(-2, locator.bytesToValue(data, 0).shortValue());
        assertEquals((short) 0xfffe, locator.valueToShorts(-2)[0]);
    }

    @Test
    public void testTwoByteIntUnsignedSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED);
        byte[] data = shorts((short) 0x0102);
        assertEquals(0x0201, locator.bytesToValue(data, 0).intValue());
        assertEquals((short) 0x0102, locator.valueToShorts(0x0201)[0]);
    }

    @Test
    public void testTwoByteIntSignedSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.TWO_BYTE_INT_SIGNED_SWAPPED);
        byte[] data = shorts((short) 0x01fe);
        assertEquals((short) 0xfe01, locator.bytesToValue(data, 0).shortValue());
    }

    @Test
    public void testOneByteUnsignedLower() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.ONE_BYTE_INT_UNSIGNED_LOWER);
        byte[] data = shorts((short) 0x1200);
        assertEquals(0, locator.bytesToValue(data, 0).intValue());
        byte[] data2 = shorts((short) 0x1200);
        assertEquals(0, locator.bytesToValue(data2, 0).intValue());
        byte[] data3 = shorts((short) 0x0001);
        assertEquals(1, locator.bytesToValue(data3, 0).intValue());
        assertEquals((short) 0x0001, locator.valueToShorts(1)[0]);
    }

    @Test
    public void testOneByteUnsignedUpper() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.ONE_BYTE_INT_UNSIGNED_UPPER);
        byte[] data = shorts((short) 0x12ff);
        assertEquals(0x12, locator.bytesToValue(data, 0).intValue());
        assertEquals((short) 0x1200, locator.valueToShorts(0x12)[0]);
    }

    @Test
    public void testFourByteIntUnsigned() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_INT_UNSIGNED);
        byte[] data = shorts((short) 0x1234, (short) 0x5678);
        assertEquals(0x12345678L, locator.bytesToValue(data, 0).longValue());
        short[] s = locator.valueToShorts(0x12345678L);
        assertEquals((short) 0x1234, s[0]);
        assertEquals((short) 0x5678, s[1]);
    }

    @Test
    public void testFourByteIntSigned() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_INT_SIGNED);
        byte[] data = shorts((short) 0xffff, (short) 0xfffe);
        assertEquals(-2, locator.bytesToValue(data, 0).intValue());
        short[] s = locator.valueToShorts(-2);
        assertEquals((short) 0xffff, s[0]);
        assertEquals((short) 0xfffe, s[1]);
    }

    @Test
    public void testFourByteIntUnsignedSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED);
        byte[] data = shorts((short) 0x1234, (short) 0x5678);
        assertEquals(0x56781234L, locator.bytesToValue(data, 0).longValue());
        short[] s = locator.valueToShorts(0x56781234L);
        assertEquals((short) 0x1234, s[0]);
        assertEquals((short) 0x5678, s[1]);
    }

    @Test
    public void testFourByteIntSignedSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_INT_SIGNED_SWAPPED);
        byte[] data = shorts((short) 0xfffe, (short) 0xffff);
        assertEquals(-2, locator.bytesToValue(data, 0).intValue());
    }

    @Test
    public void testFourByteIntUnsignedSwappedSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED);
        byte[] data = shorts((short) 0x1234, (short) 0x5678);
        assertEquals(0x78563412L, locator.bytesToValue(data, 0).longValue());
    }

    @Test
    public void testFourByteIntSignedSwappedSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED);
        byte[] data = shorts((short) 0xfeff, (short) 0xffff);
        assertEquals(-2, locator.bytesToValue(data, 0).intValue());
        short[] s = locator.valueToShorts(-2);
        assertEquals(2, s.length);
        assertEquals((short) 0xfeff, s[0]);
        assertEquals((short) 0xffff, s[1]);
    }

    @Test
    public void testFourByteFloat() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_FLOAT);
        byte[] data = shorts((short) 0x3f80, (short) 0x0000);
        assertEquals(1.0f, locator.bytesToValue(data, 0).floatValue(), 0.001f);
        short[] s = locator.valueToShorts(1.0f);
        assertEquals((short) 0x3f80, s[0]);
        assertEquals((short) 0x0000, s[1]);
    }

    @Test
    public void testFourByteFloatSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_FLOAT_SWAPPED);
        byte[] data = shorts((short) 0x0000, (short) 0x3f80);
        assertEquals(1.0f, locator.bytesToValue(data, 0).floatValue(), 0.001f);
        short[] s = locator.valueToShorts(1.0f);
        assertEquals((short) 0x0000, s[0]);
        assertEquals((short) 0x3f80, s[1]);
    }

    @Test
    public void testFourByteFloatSwappedInverted() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED);
        byte[] data = shorts((short) 0x0000, (short) 0x3f80);
        assertEquals(1.0f, locator.bytesToValue(data, 0).floatValue(), 0.001f);
    }

    @Test
    public void testEightByteIntSigned() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_INT_SIGNED);
        byte[] data = shorts((short) 0xffff, (short) 0xffff, (short) 0xffff, (short) 0xfffe);
        assertEquals(-2L, locator.bytesToValue(data, 0).longValue());
        short[] s = locator.valueToShorts(-2L);
        assertEquals(4, s.length);
    }

    @Test
    public void testEightByteIntSignedSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED);
        byte[] data = shorts((short) 0xfffe, (short) 0xffff, (short) 0xffff, (short) 0xffff);
        assertEquals(-2L, locator.bytesToValue(data, 0).longValue());
    }

    @Test
    public void testEightByteFloat() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_FLOAT);
        byte[] data = shorts((short) 0x3ff0, (short) 0, (short) 0, (short) 0);
        assertEquals(1.0, locator.bytesToValue(data, 0).doubleValue(), 0.001);
        short[] s = locator.valueToShorts(1.0);
        assertEquals((short) 0x3ff0, s[0]);
    }

    @Test
    public void testEightByteFloatSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_FLOAT_SWAPPED);
        byte[] data = shorts((short) 0, (short) 0, (short) 0, (short) 0x3ff0);
        assertEquals(1.0, locator.bytesToValue(data, 0).doubleValue(), 0.001);
    }

    @Test
    public void testRegisterCountAndEndOffset() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 100,
                DataType.FOUR_BYTE_FLOAT);
        assertEquals(2, locator.getRegisterCount());
        assertEquals(101, locator.getEndOffset());
    }

    @Test
    public void testToString() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 100,
                DataType.TWO_BYTE_INT_UNSIGNED);
        assertEquals("NumericLocator(slaveId=1, range=4, offset=100, dataType=2)", locator.toString());
    }

    @Test
    public void testOffsetConversion() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 100,
                DataType.TWO_BYTE_INT_UNSIGNED);
        byte[] data = shorts((short) 0x0000, (short) 0x0042, (short) 0x0000);
        assertEquals(0x42, locator.bytesToValue(data, 99).intValue());
    }

    @Test
    public void testFourByteIntUnsignedSwappedSwappedWrite() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED);
        short[] s = locator.valueToShorts(0x78563412L);
        assertEquals(2, s.length);
        assertEquals((short) 0x1234, s[0]);
        assertEquals((short) 0x5678, s[1]);
    }

    @Test
    public void testEightByteIntUnsigned() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_INT_UNSIGNED);
        short[] s = locator.valueToShorts(0x0102030405060708L);
        assertEquals(4, s.length);
        assertEquals((short) 0x0102, s[0]);
        assertEquals((short) 0x0708, s[3]);
    }

    @Test
    public void testEightByteIntUnsignedSwappedWrite() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED);
        short[] s = locator.valueToShorts(0x0102030405060708L);
        assertEquals(4, s.length);
        assertEquals((short) 0x0708, s[0]);
        assertEquals((short) 0x0102, s[3]);
    }

    @Test
    public void testFourByteFloatSwappedInvertedWrite() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED);
        short[] s = locator.valueToShorts(1.0f);
        assertEquals(2, s.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedBytesToValue() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.BINARY);
        byte[] data = shorts((short) 0x0001);
        locator.bytesToValue(data, 0);
    }

    @Test
    public void testTwoByteBcd() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.TWO_BYTE_BCD);
        byte[] data = shorts((short) 0x1234);
        assertEquals((short) 1234, locator.bytesToValue(data, 0));
        assertEquals(0x1234, locator.valueToShorts((short) 1234)[0] & 0xffff);
    }

    @Test
    public void testFourByteBcd() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.FOUR_BYTE_BCD);
        byte[] data = shorts((short) 0x1234, (short) 0x5678);
        assertEquals(12345678, locator.bytesToValue(data, 0).intValue());
        short[] s = locator.valueToShorts(12345678);
        assertEquals(0x1234, s[0] & 0xffff);
        assertEquals(0x5678, s[1] & 0xffff);
    }

    @Test
    public void testFourByteBcdSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_BCD_SWAPPED);
        byte[] data = shorts((short) 0x1234, (short) 0x5678);
        assertEquals(56781234, locator.bytesToValue(data, 0).intValue());
        short[] s = locator.valueToShorts(56781234);
        assertEquals(0x1234, s[0] & 0xffff);
        assertEquals(0x5678, s[1] & 0xffff);
    }

    @Test
    public void testFourByteMod10k() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_MOD_10K);
        byte[] data = shorts((short) 1234, (short) 5678);
        assertEquals(new BigInteger("12345678"), locator.bytesToValue(data, 0));
        short[] s = locator.valueToShorts(new BigInteger("12345678"));
        assertEquals(1234, s[0] & 0xffff);
        assertEquals(5678, s[1] & 0xffff);
    }

    @Test
    public void testSixByteMod10k() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.SIX_BYTE_MOD_10K);
        byte[] data = shorts((short) 1, (short) 2345, (short) 6789);
        assertEquals(new BigInteger("123456789"), locator.bytesToValue(data, 0));
        short[] s = locator.valueToShorts(new BigInteger("123456789"));
        assertEquals(1, s[0] & 0xffff);
        assertEquals(2345, s[1] & 0xffff);
        assertEquals(6789, s[2] & 0xffff);
    }

    @Test
    public void testEightByteMod10kSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_MOD_10K_SWAPPED);
        byte[] data = shorts((short) 6789, (short) 2345, (short) 1, (short) 0);
        assertEquals(new BigInteger("123456789"), locator.bytesToValue(data, 0));
        short[] s = locator.valueToShorts(new BigInteger("123456789"));
        assertEquals(6789, s[0] & 0xffff);
        assertEquals(2345, s[1] & 0xffff);
        assertEquals(1, s[2] & 0xffff);
        assertEquals(0, s[3] & 0xffff);
    }

    @Test
    public void testEightByteIntUnsignedMaxValue() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_INT_UNSIGNED);
        byte[] data = shorts((short) 0xffff, (short) 0xffff, (short) 0xffff, (short) 0xffff);
        BigInteger value = (BigInteger) locator.bytesToValue(data, 0);
        assertEquals(new BigInteger("18446744073709551615"), value);
        short[] s = locator.valueToShorts(new BigInteger("18446744073709551615"));
        for (short v : s)
            assertEquals((short) 0xffff, v);
    }

    @Test
    public void testEightByteIntUnsignedSwapped() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED);
        byte[] data = shorts((short) 0xffff, (short) 0xffff, (short) 0xffff, (short) 0xffff);
        BigInteger value = (BigInteger) locator.bytesToValue(data, 0);
        assertEquals(new BigInteger("18446744073709551615"), value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedWriteBcd() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.TWO_BYTE_BCD);
        locator.valueToShorts((short) 20000);
    }

    @Test
    public void testMod10kSwappedReads() {
        NumericLocator f = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_MOD_10K_SWAPPED);
        assertEquals(new BigInteger("12345678"), f.bytesToValue(shorts((short) 5678, (short) 1234), 0));
        NumericLocator s = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.SIX_BYTE_MOD_10K_SWAPPED);
        assertEquals(new BigInteger("123456789"), s.bytesToValue(shorts((short) 6789, (short) 2345, (short) 1), 0));
        NumericLocator e = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_MOD_10K);
        assertEquals(new BigInteger("1234567890123456"),
                e.bytesToValue(shorts((short) 1234, (short) 5678, (short) 9012, (short) 3456), 0));
    }

    @Test
    public void testMod10kWrites() {
        NumericLocator f = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.FOUR_BYTE_MOD_10K_SWAPPED);
        short[] sf = f.valueToShorts(12345678L);
        assertEquals(5678, sf[0] & 0xffff);
        assertEquals(1234, sf[1] & 0xffff);
        NumericLocator s = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.SIX_BYTE_MOD_10K_SWAPPED);
        short[] ss = s.valueToShorts(new BigInteger("123456789"));
        assertEquals(6789, ss[0] & 0xffff);
        NumericLocator e = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_MOD_10K);
        short[] se = e.valueToShorts(new BigInteger("1234567890"));
        assertEquals(0, se[0] & 0xffff);
        assertEquals(12, se[1] & 0xffff);
        assertEquals(3456, se[2] & 0xffff);
        assertEquals(7890, se[3] & 0xffff);
    }

    @Test
    public void testEightByteFloatSwappedWrite() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0,
                DataType.EIGHT_BYTE_FLOAT_SWAPPED);
        short[] s = locator.valueToShorts(1.0d);
        assertEquals(4, s.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedValueToShorts() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.BINARY);
        locator.valueToShorts(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeBcdWrite() {
        NumericLocator locator = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.TWO_BYTE_BCD);
        locator.valueToShorts((short) -1);
    }
}
