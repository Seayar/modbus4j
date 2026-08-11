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

public final class HexUtil {
    private static final char[] DIGITS = "0123456789ABCDEF".toCharArray();

    private HexUtil() {}

    public static String str2HexStr(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        for (byte b : bs) {
            sb.append(DIGITS[(b & 0xf0) >> 4]);
            sb.append(DIGITS[b & 0x0f]);
        }
        return sb.toString();
    }

    public static byte[] hexStringToByte(String hex) {
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (charToByte(achar[pos]) << 4 | charToByte(achar[pos + 1]));
        }
        return result;
    }

    private static int charToByte(char c) {
        return "0123456789ABCDEF".indexOf(c);
    }

    public static String bytesToHexString(byte[] bArray) {
        return bytesToHexString(bArray, "");
    }

    public static String bytesToHexString(byte[] bArray, String split) {
        return bytesToHexString(bArray, -1, split);
    }

    public static String bytesToHexString(byte[] bArray, int len, String split) {
        StringBuilder sb = new StringBuilder(bArray.length);
        if (len < 0)
            len = bArray.length;
        for (int i = 0; i < len; i++) {
            if (split != null && split.length() > 0)
                sb.append(split);
            String sTemp = Integer.toHexString(255 & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static int isOdd(int num) {
        return num & 1;
    }

    public static int hexToInt(String inHex) {
        return Integer.parseInt(inHex, 16);
    }

    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static String byte2Hex(byte inByte) {
        return String.format("%02x", inByte).toUpperCase();
    }
}
