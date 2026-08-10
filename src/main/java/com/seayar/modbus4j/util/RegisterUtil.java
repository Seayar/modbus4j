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

public final class RegisterUtil {
    private RegisterUtil() {}

    public static String toHex(byte[] data) {
        return toHex(data, 0, data.length);
    }

    public static String toHex(byte[] data, int off, int length) {
        StringBuilder buf = new StringBuilder(data.length * 2);
        for (int i = off; i < off + length; i++) {
            if ((data[i] & 255) < 16)
                buf.append("0");
            buf.append(Integer.toHexString(data[i] & 255).toUpperCase());
            if (i < off + length - 1)
                buf.append(" ");
        }
        return buf.toString();
    }

    public static int registerToUnsignedShort(byte[] bytes) {
        return (bytes[0] & 255) << 8 | bytes[1] & 255;
    }

    public static byte[] unsignedShortToRegister(int v) {
        return new byte[]{(byte) (255 & v >> 8), (byte) (255 & v)};
    }

    public static short registerToShort(byte[] bytes) {
        return (short) (bytes[0] << 8 | bytes[1] & 255);
    }

    public static short registerToShort(byte[] bytes, int idx) {
        return (short) (bytes[idx] << 8 | bytes[idx + 1] & 255);
    }

    public static byte[] shortToRegister(short s) {
        return new byte[]{(byte) (255 & s >> 8), (byte) (255 & s)};
    }

    public static int registersToInt(byte[] bytes) {
        return (bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255;
    }

    public static byte[] intToRegisters(int v) {
        return new byte[]{(byte) (255 & v >> 24), (byte) (255 & v >> 16), (byte) (255 & v >> 8), (byte) (255 & v)};
    }

    public static long registersToLong(byte[] bytes) {
        return (long) (bytes[0] & 255) << 56 | (long) (bytes[1] & 255) << 48 | (long) (bytes[2] & 255) << 40
                | (long) (bytes[3] & 255) << 32 | (long) (bytes[4] & 255) << 24 | (long) (bytes[5] & 255) << 16
                | (long) (bytes[6] & 255) << 8 | (long) (bytes[7] & 255);
    }

    public static byte[] longToRegisters(long v) {
        return new byte[]{(byte) (255L & v >> 56), (byte) (255L & v >> 48), (byte) (255L & v >> 40),
                (byte) (255L & v >> 32), (byte) (255L & v >> 24), (byte) (255L & v >> 16), (byte) (255L & v >> 8),
                (byte) (255L & v)};
    }

    public static float registersToFloat(byte[] bytes) {
        return Float.intBitsToFloat((bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8
                | bytes[3] & 255);
    }

    public static byte[] floatToRegisters(float f) {
        return intToRegisters(Float.floatToIntBits(f));
    }

    public static double registersToDouble(byte[] bytes) {
        return Double.longBitsToDouble(registersToLong(bytes));
    }

    public static byte[] doubleToRegisters(double d) {
        return longToRegisters(Double.doubleToLongBits(d));
    }

    public static int unsignedByteToInt(byte b) {
        return b & 255;
    }

    public static byte lowByte(int wd) {
        return (byte) (255 & wd);
    }

    public static byte hiByte(int wd) {
        return (byte) (255 & wd >> 8);
    }

    public static int makeWord(int hibyte, int lowbyte) {
        int hi = 255 & hibyte;
        int low = 255 & lowbyte;
        return hi << 8 | low;
    }
}
