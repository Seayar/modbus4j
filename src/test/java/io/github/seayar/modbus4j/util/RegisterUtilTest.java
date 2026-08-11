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
package io.github.seayar.modbus4j.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RegisterUtilTest {

    @Test
    public void testToHex() {
        byte[] data = new byte[]{1, 0x0a, (byte) 0xff};
        assertEquals("01 0A FF", RegisterUtil.toHex(data));
    }

    @Test
    public void testToHexOffsetLength() {
        byte[] data = new byte[]{1, 2, 3, 4};
        assertEquals("02 03", RegisterUtil.toHex(data, 1, 2));
    }

    @Test
    public void testRegisterToUnsignedShort() {
        assertEquals(0x1234, RegisterUtil.registerToUnsignedShort(new byte[]{0x12, 0x34}));
    }

    @Test
    public void testUnsignedShortToRegister() {
        assertArrayEquals(new byte[]{0x12, 0x34}, RegisterUtil.unsignedShortToRegister(0x1234));
    }

    @Test
    public void testRegisterToShort() {
        assertEquals((short) 0x1234, RegisterUtil.registerToShort(new byte[]{0x12, 0x34}));
    }

    @Test
    public void testRegisterToShortAtOffset() {
        assertEquals((short) 0x3456, RegisterUtil.registerToShort(new byte[]{0x12, 0x34, 0x56}, 1));
    }

    @Test
    public void testShortToRegister() {
        assertArrayEquals(new byte[]{0x12, 0x34}, RegisterUtil.shortToRegister((short) 0x1234));
    }    @Test
    public void testRegistersToInt() {
        assertEquals(0x12345678, RegisterUtil.registersToInt(new byte[]{0x12, 0x34, 0x56, 0x78}));
    }

    @Test
    public void testIntToRegisters() {
        assertArrayEquals(new byte[]{0x12, 0x34, 0x56, 0x78}, RegisterUtil.intToRegisters(0x12345678));
    }

    @Test
    public void testRegistersToLong() {
        assertEquals(0x0102030405060708L,
                RegisterUtil.registersToLong(new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));
    }

    @Test
    public void testLongToRegisters() {
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8},
                RegisterUtil.longToRegisters(0x0102030405060708L));
    }

    @Test
    public void testRegistersToFloat() {
        assertEquals(1.0f, RegisterUtil.registersToFloat(new byte[]{0x3f, (byte) 0x80, 0, 0}), 0.001f);
    }

    @Test
    public void testFloatToRegisters() {
        assertArrayEquals(new byte[]{0x3f, (byte) 0x80, 0, 0}, RegisterUtil.floatToRegisters(1.0f));
    }

    @Test
    public void testRegistersToDouble() {
        assertEquals(1.0, RegisterUtil.registersToDouble(new byte[]{0x3f, (byte) 0xf0, 0, 0, 0, 0, 0, 0}), 0.001);
    }

    @Test
    public void testDoubleToRegisters() {
        assertArrayEquals(new byte[]{0x3f, (byte) 0xf0, 0, 0, 0, 0, 0, 0}, RegisterUtil.doubleToRegisters(1.0));
    }

    @Test
    public void testUnsignedByteToInt() {
        assertEquals(255, RegisterUtil.unsignedByteToInt((byte) 0xff));
    }

    @Test
    public void testLowByteHiByte() {
        assertEquals((byte) 0x34, RegisterUtil.lowByte(0x1234));
        assertEquals((byte) 0x12, RegisterUtil.hiByte(0x1234));
    }

    @Test
    public void testMakeWord() {
        assertEquals(0x1234, RegisterUtil.makeWord(0x12, 0x34));
    }
}
