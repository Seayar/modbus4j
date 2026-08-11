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

public class HexUtilTest {

    @Test
    public void testStr2HexStr() {
        assertEquals("4142", HexUtil.str2HexStr("AB"));
    }

    @Test
    public void testHexStringToByte() {
        assertArrayEquals(new byte[]{0x01, 0x0a, (byte) 0xff}, HexUtil.hexStringToByte("010AFF"));
    }

    @Test
    public void testBytesToHexString() {
        assertEquals(" 01 0A FF", HexUtil.bytesToHexString(new byte[]{1, 0x0a, (byte) 0xff}, " "));
    }

    @Test
    public void testBytesToHexStringDefaultSplit() {
        assertEquals("010AFF", HexUtil.bytesToHexString(new byte[]{1, 0x0a, (byte) 0xff}));
    }

    @Test
    public void testBytesToHexStringLimited() {
        assertEquals("01", HexUtil.bytesToHexString(new byte[]{1, 0x0a}, 1, ""));
    }
    @Test
    public void testIsOdd() {
        assertEquals(1, HexUtil.isOdd(3));
        assertEquals(0, HexUtil.isOdd(4));
    }

    @Test
    public void testHexToInt() {
        assertEquals(255, HexUtil.hexToInt("FF"));
    }

    @Test
    public void testHexToByte() {
        assertEquals((byte) 0xff, HexUtil.hexToByte("FF"));
    }

    @Test
    public void testByte2Hex() {
        assertEquals("FF", HexUtil.byte2Hex((byte) 0xff));
    }
}
