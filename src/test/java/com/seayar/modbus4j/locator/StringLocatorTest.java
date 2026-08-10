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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringLocatorTest {

    @Test
    public void testCharType() {
        StringLocator locator = new StringLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.CHAR, 4);
        byte[] data = "abcdefgh".getBytes();
        assertEquals("abcdefgh", locator.bytesToValue(data, 0));
        short[] shorts = locator.valueToShorts("abcdefgh");
        assertEquals(4, shorts.length);
        assertEquals((short) 0x6162, shorts[0]);
    }

    @Test
    public void testVarcharType() {
        StringLocator locator = new StringLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.VARCHAR, 4);
        byte[] data = "abcd\0efg".getBytes();
        assertEquals("abcd", locator.bytesToValue(data, 0));
        byte[] data2 = "abcdefgh".getBytes();
        assertEquals("abcdefgh", locator.bytesToValue(data2, 0));
    }

    @Test
    public void testCharPadding() {
        StringLocator locator = new StringLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.CHAR, 4);
        short[] shorts = locator.valueToShorts("ab");
        assertEquals((short) 0x6162, shorts[0]);
        assertEquals((short) 0x2020, shorts[1]);
    }

    @Test
    public void testVarcharPadding() {
        StringLocator locator = new StringLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.VARCHAR, 4);
        short[] shorts = locator.valueToShorts("abc");
        assertEquals((short) 0x6162, shorts[0]);
        assertEquals((short) 0x6300, shorts[1]);
    }

    @Test
    public void testVarcharTruncate() {
        StringLocator locator = new StringLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.VARCHAR, 4);
        short[] shorts = locator.valueToShorts("abcdefghij");
        assertEquals(4, shorts.length);
        assertEquals((short) 0x6162, shorts[0]);
        assertEquals((short) 0x6364, shorts[1]);
        assertEquals((short) 0x6566, shorts[2]);
        assertEquals((short) 0x6700, shorts[3]);
    }

    @Test
    public void testNullValue() {
        StringLocator locator = new StringLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.CHAR, 2);
        short[] shorts = locator.valueToShorts(null);
        assertEquals((short) 0x2020, shorts[0]);
    }

    @Test
    public void testGetters() {
        StringLocator locator = new StringLocator(1, RegisterRange.HOLDING_REGISTER, 5, DataType.CHAR, 3);
        assertEquals(DataType.CHAR, locator.getDataType());
        assertEquals(3, locator.getRegisterCount());
        assertEquals(7, locator.getEndOffset());
        assertEquals(StringLocator.ASCII, java.nio.charset.StandardCharsets.US_ASCII);
    }

    @Test
    public void testToString() {
        StringLocator locator = new StringLocator(1, RegisterRange.HOLDING_REGISTER, 5, DataType.CHAR, 3);
        assertEquals("StringLocator(slaveId=1, range=4, offset=5, dataType=18, registerCount=3, charset=US-ASCII)",
                locator.toString());
    }
}
