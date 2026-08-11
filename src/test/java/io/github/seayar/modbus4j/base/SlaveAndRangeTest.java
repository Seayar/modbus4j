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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SlaveAndRangeTest {

    @Test
    public void testGetters() {
        SlaveAndRange sar = new SlaveAndRange(3, RegisterRange.HOLDING_REGISTER);
        assertEquals(3, sar.getSlaveId());
        assertEquals(RegisterRange.HOLDING_REGISTER, sar.getRange());
    }

    @Test
    public void testEqualsHashCode() {
        SlaveAndRange a = new SlaveAndRange(3, RegisterRange.HOLDING_REGISTER);
        SlaveAndRange b = new SlaveAndRange(3, RegisterRange.HOLDING_REGISTER);
        SlaveAndRange c = new SlaveAndRange(4, RegisterRange.HOLDING_REGISTER);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
    }

    @Test
    public void testToString() {
        assertEquals("SlaveAndRange(slaveId=3, range=4)", new SlaveAndRange(3, 4).toString());
    }
}
