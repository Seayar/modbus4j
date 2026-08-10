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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegisterRangeTest {

    @Test
    public void testGetFrom() {
        assertEquals(0, RegisterRange.getFrom(RegisterRange.COIL_STATUS));
        assertEquals(0x10000, RegisterRange.getFrom(RegisterRange.INPUT_STATUS));
        assertEquals(0x30000, RegisterRange.getFrom(RegisterRange.INPUT_REGISTER));
        assertEquals(0x40000, RegisterRange.getFrom(RegisterRange.HOLDING_REGISTER));
        assertEquals(-1, RegisterRange.getFrom(99));
    }

    @Test
    public void testGetTo() {
        assertEquals(0xffff, RegisterRange.getTo(RegisterRange.COIL_STATUS));
        assertEquals(0x1ffff, RegisterRange.getTo(RegisterRange.INPUT_STATUS));
        assertEquals(0x3ffff, RegisterRange.getTo(RegisterRange.INPUT_REGISTER));
        assertEquals(0x4ffff, RegisterRange.getTo(RegisterRange.HOLDING_REGISTER));
        assertEquals(-1, RegisterRange.getTo(99));
    }

    @Test
    public void testGetReadFunctionCode() {
        assertEquals(FunctionCode.READ_COILS, RegisterRange.getReadFunctionCode(RegisterRange.COIL_STATUS));
        assertEquals(FunctionCode.READ_DISCRETE_INPUTS, RegisterRange.getReadFunctionCode(RegisterRange.INPUT_STATUS));
        assertEquals(FunctionCode.READ_INPUT_REGISTERS, RegisterRange.getReadFunctionCode(RegisterRange.INPUT_REGISTER));
        assertEquals(FunctionCode.READ_HOLDING_REGISTERS, RegisterRange.getReadFunctionCode(RegisterRange.HOLDING_REGISTER));
        assertEquals(-1, RegisterRange.getReadFunctionCode(99));
    }

    @Test
    public void testIsDiscrete() {
        assertTrue(RegisterRange.isDiscrete(RegisterRange.COIL_STATUS));
        assertTrue(RegisterRange.isDiscrete(RegisterRange.INPUT_STATUS));
        assertFalse(RegisterRange.isDiscrete(RegisterRange.HOLDING_REGISTER));
    }

    @Test
    public void testIsInput() {
        assertTrue(RegisterRange.isInput(RegisterRange.INPUT_STATUS));
        assertTrue(RegisterRange.isInput(RegisterRange.INPUT_REGISTER));
        assertFalse(RegisterRange.isInput(RegisterRange.COIL_STATUS));
    }
}
