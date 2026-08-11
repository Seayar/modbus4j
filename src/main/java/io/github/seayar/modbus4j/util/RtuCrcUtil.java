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

public final class RtuCrcUtil {
    private RtuCrcUtil() {}

    public static int calculateCRC(byte[] buf, int len) {
        int crc = 65535;
        for (int pos = 0; pos < len; pos++) {
            crc ^= buf[pos] & 255;
            for (int i = 8; i != 0; i--) {
                if ((crc & 1) != 0) {
                    crc >>= 1;
                    crc ^= 40961;
                } else
                    crc >>= 1;
            }
        }
        return crc;
    }

    public static int calculateCRC(byte[] buf) {
        return calculateCRC(buf, buf.length);
    }
}
