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
package com.seayar.modbus4j.locator;

import com.seayar.modbus4j.base.SlaveAndRange;

public interface ModbusLocator<T> {

    int getSlaveId();

    int getRange();

    int getOffset();

    int getEndOffset();

    SlaveAndRange getSlaveAndRange();

    int getDataType();

    int getRegisterCount();

    T bytesToValue(byte[] data, int requestOffset);

    T bytesToValueRealOffset(byte[] data, int offset);

    short[] valueToShorts(T value);
}
