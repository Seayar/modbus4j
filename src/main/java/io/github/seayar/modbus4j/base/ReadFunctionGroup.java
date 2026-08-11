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

import io.github.seayar.modbus4j.locator.ModbusLocator;

import java.util.ArrayList;
import java.util.List;

public class ReadFunctionGroup<K> {
    private final int slaveId;
    private final int range;
    private final int startOffset;
    private int endOffset;
    private final List<KeyedModbusLocator<K>> locators = new ArrayList<>();

    public ReadFunctionGroup(KeyedModbusLocator<K> locator) {
        slaveId = locator.getSlaveAndRange().getSlaveId();
        range = locator.getSlaveAndRange().getRange();
        startOffset = locator.getOffset();
        endOffset = locator.getEndOffset();
        add(locator);
    }

    public void add(KeyedModbusLocator<K> locator) {
        locators.add(locator);
        if (locator.getEndOffset() > endOffset)
            endOffset = locator.getEndOffset();
    }

    public int getSlaveId() {
        return slaveId;
    }

    public int getRange() {
        return range;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public int getFunctionCode() {
        return RegisterRange.getReadFunctionCode(range);
    }

    public List<KeyedModbusLocator<K>> getLocators() {
        return locators;
    }

    public static int getRegisterCount(ModbusLocator<?> locator) {
        return locator.getRegisterCount();
    }

    @Override
    public String toString() {
        return "ReadFunctionGroup(slaveId=" + slaveId + ", range=" + range + ", startOffset=" + startOffset
                + ", endOffset=" + endOffset + ", locators=" + locators.size() + ")";
    }
}
