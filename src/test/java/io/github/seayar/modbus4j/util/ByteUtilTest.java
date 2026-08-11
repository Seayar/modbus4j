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

public class ByteUtilTest {

    @Test
    public void testToByteArrayInt() {
        assertArrayEquals(new byte[]{0, 0, 0, 1, 0, 0, 0, 2},
                ByteUtil.toByteArray(new int[]{1, 2}));
    }

    @Test
    public void testToFloatArrayBigEndian() {
        byte[] data = new byte[]{(byte) 0x3f, (byte) 0x80, 0, 0};
        assertArrayEquals(new float[]{1.0f}, ByteUtil.toFloatArray(data), 0.001f);
    }

    @Test
    public void testToFloatArrayLittleEndian() {
        byte[] data = new byte[]{0, 0, (byte) 0x80, (byte) 0x3f};
        assertArrayEquals(new float[]{1.0f}, ByteUtil.toFloatArray(data, false), 0.001f);
    }

    @Test
    public void testToDoubleArray() {
        byte[] data = new byte[]{(byte) 0x3f, (byte) 0xf0, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(new double[]{1.0}, ByteUtil.toDoubleArray(data), 0.001);
    }

    @Test
    public void testToLongArray() {
        byte[] data = new byte[]{0, 0, 0, 0, 0, 0, 0, 5};
        assertArrayEquals(new long[]{5}, ByteUtil.toLongArray(data));
    }

    @Test
    public void testToIntArray() {
        byte[] data = new byte[]{0, 0, 0, 9};
        assertArrayEquals(new int[]{9}, ByteUtil.toIntArray(data));
    }

    @Test
    public void testToUShortArray() {
        byte[] data = new byte[]{(byte) 0xff, (byte) 0xfe};
        assertArrayEquals(new int[]{0xfffe}, ByteUtil.toUShortArray(data));
    }

    @Test
    public void testToShortArray() {
        byte[] data = new byte[]{0, (byte) 0x80};
        assertArrayEquals(new short[]{(short) 0x80}, ByteUtil.toShortArray(data));
    }

    @Test
    public void testToCharArray() {
        byte[] data = new byte[]{0, 0x41};
        assertArrayEquals(new char[]{'A'}, ByteUtil.toCharArray(data));
    }
}
