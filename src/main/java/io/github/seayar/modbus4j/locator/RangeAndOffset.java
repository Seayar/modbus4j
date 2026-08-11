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
package io.github.seayar.modbus4j.locator;

import io.github.seayar.modbus4j.base.RegisterRange;

public class RangeAndOffset {
    private final int range;
    private final int offset;

    public RangeAndOffset(int range, int offset) {
        this.range = range;
        this.offset = offset;
    }

    public RangeAndOffset(int registerId) {
        if (registerId < 10000) {
            range = RegisterRange.COIL_STATUS;
            offset = registerId - 1;
        } else if (registerId < 20000) {
            range = RegisterRange.INPUT_STATUS;
            offset = registerId - 10001;
        } else if (registerId < 40000) {
            range = RegisterRange.INPUT_REGISTER;
            offset = registerId - 30001;
        } else {
            range = RegisterRange.HOLDING_REGISTER;
            offset = registerId - 40001;
        }
    }

    public int getRange() {
        return range;
    }

    public int getOffset() {
        return offset;
    }
}
