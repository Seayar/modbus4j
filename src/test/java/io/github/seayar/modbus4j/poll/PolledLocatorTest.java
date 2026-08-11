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
package io.github.seayar.modbus4j.poll;

import io.github.seayar.modbus4j.base.DataType;
import io.github.seayar.modbus4j.locator.BaseLocator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PolledLocatorTest {

    @Test
    public void testAccessors() {
        PolledLocator locator = new PolledLocator("temp",
                BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED), 1000);
        assertEquals("temp", locator.getKey());
        assertEquals(1000, locator.getUpdatePeriodMillis());
        assertEquals(1, locator.getLocator().getSlaveId());
    }

    @Test
    public void testEquals() {
        PolledLocator a = new PolledLocator("k", null, 1000);
        PolledLocator b = new PolledLocator("k", null, 2000);
        PolledLocator c = new PolledLocator("other", null, 1000);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(c));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));
    }

    @Test
    public void testToString() {
        PolledLocator locator = new PolledLocator("temp",
                BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED), 1000);
        assertTrue(locator.toString().startsWith("PolledLocator(key=temp"));
    }
}
