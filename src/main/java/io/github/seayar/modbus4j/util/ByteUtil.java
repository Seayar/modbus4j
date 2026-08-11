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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public final class ByteUtil {
    private ByteUtil() {}

    public static byte[] toByteArray(int[] values) {
        ByteBuffer bf = ByteBuffer.allocate(4 * values.length);
        for (int value : values)
            bf.putInt(value);
        return bf.array();
    }

    public static float[] toFloatArray(byte[] bytes) {
        return toFloatArray(bytes, true);
    }

    public static float[] toFloatArray(byte[] bytes, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        FloatBuffer fb = buffer.asFloatBuffer();
        float[] floatArray = new float[fb.limit()];
        fb.get(floatArray);
        return floatArray;
    }

    public static double[] toDoubleArray(byte[] bytes) {
        return toDoubleArray(bytes, true);
    }

    public static double[] toDoubleArray(byte[] bytes, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        DoubleBuffer db = buffer.asDoubleBuffer();
        double[] doubleArray = new double[db.limit()];
        db.get(doubleArray);
        return doubleArray;
    }

    public static long[] toLongArray(byte[] bytes) {
        return toLongArray(bytes, true);
    }

    public static long[] toLongArray(byte[] bytes, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        LongBuffer lb = buffer.asLongBuffer();
        long[] longArray = new long[lb.limit()];
        lb.get(longArray);
        return longArray;
    }

    public static int[] toIntArray(byte[] bytes) {
        return toIntArray(bytes, true);
    }

    public static int[] toIntArray(byte[] bytes, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        IntBuffer ib = buffer.asIntBuffer();
        int[] intArray = new int[ib.limit()];
        ib.get(intArray);
        return intArray;
    }

    public static int[] toUShortArray(byte[] bytes) {
        return toUShortArray(bytes, true);
    }

    public static int[] toUShortArray(byte[] bytes, boolean bigEndian) {
        short[] shortArray = toShortArray(bytes, bigEndian);
        int[] ushortArray = new int[shortArray.length];
        for (int i = 0; i < shortArray.length; i++)
            ushortArray[i] = shortArray[i] & 0xffff;
        return ushortArray;
    }

    public static short[] toShortArray(byte[] bytes) {
        return toShortArray(bytes, true);
    }

    public static short[] toShortArray(byte[] bytes, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        ShortBuffer sb = buffer.asShortBuffer();
        short[] shortArray = new short[sb.limit()];
        sb.get(shortArray);
        return shortArray;
    }

    public static char[] toCharArray(byte[] bytes) {
        return toCharArray(bytes, true);
    }

    public static char[] toCharArray(byte[] bytes, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        CharBuffer cb = buffer.asCharBuffer();
        char[] charArray = new char[cb.limit()];
        cb.get(charArray);
        return charArray;
    }
}
