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

import io.github.seayar.modbus4j.locator.BaseLocator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KeyedModbusLocatorTest {

    @Test
    public void testGetters() {
        BaseLocator<Number> locator = BaseLocator.holdingRegister(1, 100, 8);
        KeyedModbusLocator<String> keyed = new KeyedModbusLocator<>("temp", locator);
        assertEquals("temp", keyed.getKey());
        assertEquals(locator, keyed.getLocator());
        assertEquals(100, keyed.getOffset());
        assertEquals(101, keyed.getEndOffset());
        assertEquals(new SlaveAndRange(1, RegisterRange.HOLDING_REGISTER), keyed.getSlaveAndRange());
    }

    @Test
    public void testToString() {
        KeyedModbusLocator<String> keyed = new KeyedModbusLocator<>("k",
                BaseLocator.coilStatus(1, 0));
        assertEquals("KeyedModbusLocator(key=k, locator=" + keyed.getLocator() + ")", keyed.toString());
    }
}
