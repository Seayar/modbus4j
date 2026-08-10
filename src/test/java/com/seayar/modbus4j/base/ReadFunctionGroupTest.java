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

import com.seayar.modbus4j.locator.BaseLocator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReadFunctionGroupTest {

    @Test
    public void testGetters() {
        BaseLocator<Number> l1 = BaseLocator.holdingRegister(1, 100, 8);
        BaseLocator<Number> l2 = BaseLocator.holdingRegister(1, 105, 8);
        ReadFunctionGroup<String> group = new ReadFunctionGroup<>(new KeyedModbusLocator<>("a", l1));
        group.add(new KeyedModbusLocator<>("b", l2));

        assertEquals(1, group.getSlaveId());
        assertEquals(RegisterRange.HOLDING_REGISTER, group.getRange());
        assertEquals(100, group.getStartOffset());
        assertEquals(106, group.getEndOffset());
        assertEquals(FunctionCode.READ_HOLDING_REGISTERS, group.getFunctionCode());
        assertEquals(2, group.getLocators().size());
        assertEquals(2, ReadFunctionGroup.getRegisterCount(l2));
    }

    @Test
    public void testEndOffsetExtends() {
        BaseLocator<Number> l1 = BaseLocator.holdingRegister(1, 100, 8);
        BaseLocator<Number> l2 = BaseLocator.holdingRegister(1, 200, 8);
        ReadFunctionGroup<String> group = new ReadFunctionGroup<>(new KeyedModbusLocator<>("a", l1));
        group.add(new KeyedModbusLocator<>("b", l2));
        assertEquals(201, group.getEndOffset());
    }

    @Test
    public void testToString() {
        ReadFunctionGroup<String> group = new ReadFunctionGroup<>(
                new KeyedModbusLocator<>("a", BaseLocator.holdingRegister(1, 100, 8)));
        assertEquals("ReadFunctionGroup(slaveId=1, range=4, startOffset=100, endOffset=101, locators=1)",
                group.toString());
    }
}
