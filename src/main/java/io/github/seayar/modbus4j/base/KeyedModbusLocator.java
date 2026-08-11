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

import io.github.seayar.modbus4j.locator.ModbusLocator;

public class KeyedModbusLocator<K> {
    private final K key;
    private final ModbusLocator<?> locator;

    public KeyedModbusLocator(K key, ModbusLocator<?> locator) {
        this.key = key;
        this.locator = locator;
    }

    public K getKey() {
        return key;
    }

    public ModbusLocator<?> getLocator() {
        return locator;
    }

    public int getOffset() {
        return locator.getOffset();
    }

    public int getEndOffset() {
        return locator.getEndOffset();
    }

    public SlaveAndRange getSlaveAndRange() {
        return locator.getSlaveAndRange();
    }

    @Override
    public String toString() {
        return "KeyedModbusLocator(key=" + key + ", locator=" + locator + ")";
    }
}
