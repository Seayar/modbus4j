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

public final class AsciiLrcUtil {
    private AsciiLrcUtil() {}

    public static byte calculateLRC(byte[] data, int offset, int length) {
        int sum = 0;
        for (int i = offset; i < offset + length; i++)
            sum += data[i] & 0xff;
        return (byte) -(sum & 0xff);
    }

    public static byte calculateLRC(byte[] data) {
        return calculateLRC(data, 0, data.length);
    }
}
