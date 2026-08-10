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

import com.seayar.modbus4j.base.RegisterRange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RangeAndOffsetTest {

    @Test
    public void testExplicitConstructor() {
        RangeAndOffset rao = new RangeAndOffset(RegisterRange.HOLDING_REGISTER, 100);
        assertEquals(RegisterRange.HOLDING_REGISTER, rao.getRange());
        assertEquals(100, rao.getOffset());
    }

    @Test
    public void testRegisterIdCoil() {
        assertEquals(RegisterRange.COIL_STATUS, new RangeAndOffset(1).getRange());
        assertEquals(0, new RangeAndOffset(1).getOffset());
        assertEquals(5, new RangeAndOffset(6).getOffset());
    }

    @Test
    public void testRegisterIdInputStatus() {
        assertEquals(RegisterRange.INPUT_STATUS, new RangeAndOffset(10001).getRange());
        assertEquals(0, new RangeAndOffset(10001).getOffset());
    }

    @Test
    public void testRegisterIdInputRegister() {
        assertEquals(RegisterRange.INPUT_REGISTER, new RangeAndOffset(30001).getRange());
        assertEquals(0, new RangeAndOffset(30001).getOffset());
    }

    @Test
    public void testRegisterIdHolding() {
        assertEquals(RegisterRange.HOLDING_REGISTER, new RangeAndOffset(40001).getRange());
        assertEquals(0, new RangeAndOffset(40001).getOffset());
    }
}
