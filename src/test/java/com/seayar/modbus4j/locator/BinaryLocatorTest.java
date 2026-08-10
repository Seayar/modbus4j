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
import static org.junit.Assert.assertTrue;

public class BinaryLocatorTest {

    @Test
    public void testCoilRange() {
        BinaryLocator locator = new BinaryLocator(1, RegisterRange.COIL_STATUS, 0);
        assertEquals(DataType.BINARY, locator.getDataType());
        assertEquals(1, locator.getRegisterCount());
        assertEquals(-1, locator.getBit());
        byte[] data = new byte[]{(byte) 0x81};
        assertTrue(locator.bytesToValue(data, 0));
        assertFalse(locator.bytesToValue(data, 7));
        assertEquals(1, locator.valueToShorts(true)[0]);
        assertEquals(0, locator.valueToShorts(false)[0]);
    }

    @Test
    public void testCoilRangeInvalid() {
        try {
            new BinaryLocator(1, RegisterRange.COIL_STATUS, 0, 3);
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("expected exception");
    }

    @Test
    public void testHoldingRegisterBit() {
        BinaryLocator locator = new BinaryLocator(1, RegisterRange.HOLDING_REGISTER, 10, 3);
        assertEquals(3, locator.getBit());
        byte[] data = new byte[]{(byte) 0x00, (byte) 0x08};
        assertTrue(locator.bytesToValue(data, 10));
        byte[] data2 = new byte[]{(byte) 0x00, (byte) 0x04};
        assertFalse(locator.bytesToValue(data2, 10));
    }

    @Test
    public void testHoldingRegisterBitInvalid() {
        try {
            new BinaryLocator(1, RegisterRange.HOLDING_REGISTER, 10);
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("expected exception");
    }

    @Test
    public void testIsBinaryRange() {
        assertTrue(BinaryLocator.isBinaryRange(RegisterRange.COIL_STATUS));
        assertTrue(BinaryLocator.isBinaryRange(RegisterRange.INPUT_STATUS));
        assertFalse(BinaryLocator.isBinaryRange(RegisterRange.HOLDING_REGISTER));
    }

    @Test
    public void testToString() {
        BinaryLocator locator = new BinaryLocator(1, RegisterRange.COIL_STATUS, 5);
        assertEquals("BinaryLocator(slaveId=1, range=1, offset=5, bit=-1)", locator.toString());
    }
}
