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
package com.seayar.modbus4j.poll;

import com.seayar.modbus4j.locator.ModbusLocator;

import java.util.Objects;

public class PolledLocator {
    private final String key;
    private final ModbusLocator<?> locator;
    private final long updatePeriodMillis;

    public PolledLocator(String key, ModbusLocator<?> locator, long updatePeriodMillis) {
        this.key = key;
        this.locator = locator;
        this.updatePeriodMillis = updatePeriodMillis;
    }

    public String getKey() {
        return key;
    }

    public ModbusLocator<?> getLocator() {
        return locator;
    }

    public long getUpdatePeriodMillis() {
        return updatePeriodMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PolledLocator))
            return false;
        PolledLocator that = (PolledLocator) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "PolledLocator(key=" + key + ", locator=" + locator + ", updatePeriodMillis=" + updatePeriodMillis
                + ")";
    }
}
