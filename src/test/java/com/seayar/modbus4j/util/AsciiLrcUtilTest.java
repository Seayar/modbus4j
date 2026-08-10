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
package com.seayar.modbus4j.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AsciiLrcUtilTest {

    @Test
    public void testCalculateLrc() {
        byte[] data = new byte[]{1, 3, 0, 0, 0, 2};
        assertEquals((byte) 0xfa, AsciiLrcUtil.calculateLRC(data));
    }

    @Test
    public void testCalculateLrcOffset() {
        byte[] data = new byte[]{0x0a, 1, 3, 0, 0, 0, 2};
        assertEquals((byte) 0xfa, AsciiLrcUtil.calculateLRC(data, 1, 6));
    }

    @Test
    public void testCalculateLrcEmpty() {
        assertEquals((byte) 0, AsciiLrcUtil.calculateLRC(new byte[0]));
    }
}
