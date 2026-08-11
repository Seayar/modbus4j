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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FunctionCodeTest {

    @Test
    public void testConstants() {
        assertEquals(1, FunctionCode.READ_COILS);
        assertEquals(2, FunctionCode.READ_DISCRETE_INPUTS);
        assertEquals(3, FunctionCode.READ_HOLDING_REGISTERS);
        assertEquals(4, FunctionCode.READ_INPUT_REGISTERS);
        assertEquals(5, FunctionCode.WRITE_COIL);
        assertEquals(6, FunctionCode.WRITE_REGISTER);
        assertEquals(15, FunctionCode.WRITE_COILS);
        assertEquals(16, FunctionCode.WRITE_REGISTERS);
        assertEquals(20, FunctionCode.READ_FILE_RECORD);
        assertEquals(21, FunctionCode.WRITE_FILE_RECORD);
        assertEquals(22, FunctionCode.WRITE_MASK_REGISTER);
        assertEquals(23, FunctionCode.READ_WRITE_MULTIPLE_REGISTERS);
        assertEquals(17, FunctionCode.REPORT_SLAVE_ID);
        assertEquals(7, FunctionCode.READ_EXCEPTION_STATUS);
    }

    @Test
    public void testIsException() {
        assertFalse(FunctionCode.isException((byte) 3));
        assertTrue(FunctionCode.isException((byte) 0x83));
    }

    @Test
    public void testGetExceptionCode() {
        assertEquals(3, FunctionCode.getExceptionCode((byte) 0x83));
    }

    @Test
    public void testGetExceptionFunction() {
        assertEquals((byte) 0x83, FunctionCode.getExceptionFunction((byte) 3));
    }

    @Test
    public void testToString() {
        assertEquals("3", FunctionCode.toString((byte) 3));
        assertEquals("131", FunctionCode.toString((byte) 0x83));
    }
}
