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
package io.github.seayar.modbus4j.base;

public class RegisterRange {
    public static final int COIL_STATUS = 1;
    public static final int INPUT_STATUS = 2;
    public static final int INPUT_REGISTER = 3;
    public static final int HOLDING_REGISTER = 4;

    public static int getFrom(int id) {
        switch (id) {
            case COIL_STATUS:
                return 0;
            case INPUT_STATUS:
                return 0x10000;
            case INPUT_REGISTER:
                return 0x30000;
            case HOLDING_REGISTER:
                return 0x40000;
        }
        return -1;
    }

    public static int getTo(int id) {
        switch (id) {
            case COIL_STATUS:
                return 0xffff;
            case INPUT_STATUS:
                return 0x1ffff;
            case INPUT_REGISTER:
                return 0x3ffff;
            case HOLDING_REGISTER:
                return 0x4ffff;
        }
        return -1;
    }

    public static int getReadFunctionCode(int id) {
        switch (id) {
            case COIL_STATUS:
                return FunctionCode.READ_COILS;
            case INPUT_STATUS:
                return FunctionCode.READ_DISCRETE_INPUTS;
            case INPUT_REGISTER:
                return FunctionCode.READ_INPUT_REGISTERS;
            case HOLDING_REGISTER:
                return FunctionCode.READ_HOLDING_REGISTERS;
        }
        return -1;
    }

    public static boolean isDiscrete(int range) {
        return range == COIL_STATUS || range == INPUT_STATUS;
    }

    public static boolean isInput(int range) {
        return range == INPUT_STATUS || range == INPUT_REGISTER;
    }
}
