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

import com.seayar.modbus4j.base.DataType;
import com.seayar.modbus4j.base.RegisterRange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BaseLocatorTest {

    @Test
    public void testCoilStatus() {
        BaseLocator<Boolean> locator = BaseLocator.coilStatus(1, 5);
        assertEquals(1, locator.getSlaveId());
        assertEquals(RegisterRange.COIL_STATUS, locator.getRange());
        assertEquals(5, locator.getOffset());
        assertEquals(1, locator.getRegisterCount());
        assertNotNull(locator.getSlaveAndRange());
    }

    @Test
    public void testInputStatus() {
        BaseLocator<Boolean> locator = BaseLocator.inputStatus(2, 6);
        assertEquals(RegisterRange.INPUT_STATUS, locator.getRange());
    }

    @Test
    public void testInputRegister() {
        BaseLocator<Number> locator = BaseLocator.inputRegister(3, 100, DataType.FOUR_BYTE_FLOAT);
        assertEquals(RegisterRange.INPUT_REGISTER, locator.getRange());
        assertEquals(2, locator.getRegisterCount());
    }

    @Test
    public void testInputRegisterBit() {
        BaseLocator<Boolean> locator = BaseLocator.inputRegisterBit(4, 100, 5);
        assertEquals(RegisterRange.INPUT_REGISTER, locator.getRange());
        assertEquals(1, locator.getRegisterCount());
    }

    @Test
    public void testHoldingRegister() {
        BaseLocator<Number> locator = BaseLocator.holdingRegister(5, 200, DataType.FOUR_BYTE_INT_SIGNED);
        assertEquals(RegisterRange.HOLDING_REGISTER, locator.getRange());
        assertEquals(2, locator.getRegisterCount());
    }

    @Test
    public void testHoldingRegisterBit() {
        BaseLocator<Boolean> locator = BaseLocator.holdingRegisterBit(5, 200, 15);
        assertEquals(RegisterRange.HOLDING_REGISTER, locator.getRange());
        assertEquals(15, ((BinaryLocator) locator).getBit());
    }

    @Test
    public void testHoldingRegisterString() {
        BaseLocator<String> locator = BaseLocator.holdingRegisterString(1, 10, DataType.CHAR, 4);
        assertEquals(RegisterRange.HOLDING_REGISTER, locator.getRange());
        assertEquals(4, locator.getRegisterCount());
    }

    @Test
    public void testInputRegisterString() {
        BaseLocator<String> locator = BaseLocator.inputRegisterString(1, 10, DataType.VARCHAR, 4);
        assertEquals(RegisterRange.INPUT_REGISTER, locator.getRange());
    }

    @Test
    public void testCreateLocatorByRegisterId() {
        BaseLocator<?> coil = BaseLocator.createLocator(1, 1, DataType.BINARY, -1, 1);
        assertEquals(RegisterRange.COIL_STATUS, coil.getRange());
        assertEquals(0, coil.getOffset());

        BaseLocator<?> inputStatus = BaseLocator.createLocator(1, 10001, DataType.BINARY, -1, 1);
        assertEquals(RegisterRange.INPUT_STATUS, inputStatus.getRange());
        assertEquals(0, inputStatus.getOffset());

        BaseLocator<?> inputRegister = BaseLocator.createLocator(1, 30001, DataType.TWO_BYTE_INT_UNSIGNED,
                -1, 1);
        assertEquals(RegisterRange.INPUT_REGISTER, inputRegister.getRange());
        assertEquals(0, inputRegister.getOffset());

        BaseLocator<?> holding = BaseLocator.createLocator(1, 40001, DataType.TWO_BYTE_INT_UNSIGNED, -1, 1);
        assertEquals(RegisterRange.HOLDING_REGISTER, holding.getRange());
        assertEquals(0, holding.getOffset());
    }

    @Test
    public void testCreateLocatorWithBit() {
        BaseLocator<?> bit = BaseLocator.createLocator(1, RegisterRange.HOLDING_REGISTER, 10, DataType.BINARY,
                3, 1);
        assertTrue(bit instanceof BinaryLocator);
        assertEquals(3, ((BinaryLocator) bit).getBit());
    }

    @Test
    public void testCreateLocatorString() {
        BaseLocator<?> string = BaseLocator.createLocator(1, RegisterRange.HOLDING_REGISTER, 10, DataType.CHAR,
                4, 4);
        assertTrue(string instanceof StringLocator);
    }

    @Test
    public void testValidateOffset() {
        try {
            BaseLocator.validateOffset(-1);
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("expected exception");
    }

    @Test
    public void testValidateEndOffset() {
        try {
            BaseLocator.validateEndOffset(65536);
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("expected exception");
    }
}
