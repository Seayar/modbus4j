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

import java.util.BitSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RtuCrcUtilTest {

    @Test
    public void testCalculateCrcKnownValue() {
        byte[] data = new byte[]{1, 3, 0, 0, 0, 2};
        int crc = RtuCrcUtil.calculateCRC(data);
        assertEquals(0x0bc4, crc);
    }

    @Test
    public void testCalculateCrcPartialLength() {
        byte[] data = new byte[]{1, 3, 0, 0, 0, 2};
        int crc = RtuCrcUtil.calculateCRC(data, 5);
        assertEquals(RtuCrcUtil.calculateCRC(data, 5), crc);
    }

    @Test
    public void testCalculateCrcEmpty() {
        assertEquals(0xffff, RtuCrcUtil.calculateCRC(new byte[0]));
    }
}
