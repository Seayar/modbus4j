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
package com.seayar.modbus4j.base;

public class FunctionCode {
    public static final byte READ_COILS = 1;
    public static final byte READ_DISCRETE_INPUTS = 2;
    public static final byte READ_HOLDING_REGISTERS = 3;
    public static final byte READ_INPUT_REGISTERS = 4;
    public static final byte WRITE_COIL = 5;
    public static final byte WRITE_REGISTER = 6;
    public static final byte READ_EXCEPTION_STATUS = 7;
    public static final byte READ_FILE_RECORD = 20;
    public static final byte WRITE_FILE_RECORD = 21;
    public static final byte WRITE_MASK_REGISTER = 22;
    public static final byte READ_WRITE_MULTIPLE_REGISTERS = 23;
    public static final byte WRITE_COILS = 15;
    public static final byte WRITE_REGISTERS = 16;
    public static final byte REPORT_SLAVE_ID = 17;

    public static boolean isException(byte functionCode) {
        return (functionCode & 0x80) != 0;
    }

    public static byte getExceptionCode(byte functionCode) {
        return (byte) (functionCode & 0x7f);
    }

    public static byte getExceptionFunction(byte functionCode) {
        return (byte) (functionCode | 0x80);
    }

    public static String toString(byte code) {
        return Integer.toString(code & 0xff);
    }
}
