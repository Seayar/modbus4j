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

import com.seayar.modbus4j.base.KeyedModbusLocator;
import com.seayar.modbus4j.base.ReadFunctionGroup;
import com.seayar.modbus4j.base.RegisterRange;
import com.seayar.modbus4j.base.SlaveAndRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchRead<K> {
    public static final int DEFAULT_MAX_READ_BIT_COUNT = 2000;
    public static final int DEFAULT_MAX_READ_REGISTER_COUNT = 125;

    private int maxReadBitCount = DEFAULT_MAX_READ_BIT_COUNT;
    private int maxReadRegisterCount = DEFAULT_MAX_READ_REGISTER_COUNT;
    private final List<KeyedModbusLocator<K>> requestValues = new ArrayList<>();
    private boolean contiguousRequests = false;
    private boolean errorsInResults = false;
    private boolean exceptionsInResults = false;
    private boolean splitOnException = true;
    private boolean cancel;
    private List<ReadFunctionGroup<K>> functionGroups;

    public boolean isContiguousRequests() {
        return contiguousRequests;
    }

    public void setContiguousRequests(boolean contiguousRequests) {
        this.contiguousRequests = contiguousRequests;
        functionGroups = null;
    }

    public boolean isErrorsInResults() {
        return errorsInResults;
    }

    public void setErrorsInResults(boolean errorsInResults) {
        this.errorsInResults = errorsInResults;
    }

    public boolean isExceptionsInResults() {
        return exceptionsInResults;
    }

    public void setExceptionsInResults(boolean exceptionsInResults) {
        this.exceptionsInResults = exceptionsInResults;
    }

    public boolean isSplitOnException() {
        return splitOnException;
    }

    public void setSplitOnException(boolean splitOnException) {
        this.splitOnException = splitOnException;
    }

    public int getMaxReadBitCount() {
        return maxReadBitCount;
    }

    public void setMaxReadBitCount(int maxReadBitCount) {
        this.maxReadBitCount = maxReadBitCount;
        functionGroups = null;
    }

    public int getMaxReadRegisterCount() {
        return maxReadRegisterCount;
    }

    public void setMaxReadRegisterCount(int maxReadRegisterCount) {
        this.maxReadRegisterCount = maxReadRegisterCount;
        functionGroups = null;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public List<ReadFunctionGroup<K>> getReadFunctionGroups() {
        if (functionGroups == null)
            doPartition();
        return functionGroups;
    }

    public void addLocator(K id, ModbusLocator<?> locator) {
        addLocator(new KeyedModbusLocator<>(id, locator));
    }

    private void addLocator(KeyedModbusLocator<K> locator) {
        requestValues.add(locator);
        functionGroups = null;
    }

    private void doPartition() {
        Map<SlaveAndRange, List<KeyedModbusLocator<K>>> slaveRangeBatch = new HashMap<>();
        for (KeyedModbusLocator<K> locator : requestValues)
            slaveRangeBatch.computeIfAbsent(locator.getSlaveAndRange(), k -> new ArrayList<>()).add(locator);

        functionGroups = new ArrayList<>();
        for (List<KeyedModbusLocator<K>> functionLocatorList : slaveRangeBatch.values()) {
            functionLocatorList.sort(new FunctionLocatorComparator());
            int maxReadCount = getMaxReadCount(functionLocatorList.get(0).getSlaveAndRange().getRange());
            createRequestGroups(functionGroups, functionLocatorList, maxReadCount);
        }
    }

    private void createRequestGroups(List<ReadFunctionGroup<K>> functionGroups,
            List<KeyedModbusLocator<K>> locators, int maxCount) {
        while (locators.size() > 0) {
            ReadFunctionGroup<K> functionGroup = new ReadFunctionGroup<>(locators.remove(0));
            functionGroups.add(functionGroup);
            int endOffset = functionGroup.getStartOffset() + maxCount - 1;

            int index = 0;
            while (locators.size() > index) {
                KeyedModbusLocator<K> locator = locators.get(index);
                boolean added = false;
                if (locator.getEndOffset() <= endOffset) {
                    if (contiguousRequests) {
                        if (locator.getOffset() <= functionGroup.getEndOffset() + 1) {
                            functionGroup.add(locators.remove(index));
                            added = true;
                        }
                    } else {
                        functionGroup.add(locators.remove(index));
                        added = true;
                    }
                }
                if (!added) {
                    if (locator.getOffset() > endOffset)
                        break;
                    index++;
                }
            }
        }
    }

    private int getMaxReadCount(int registerRange) {
        switch (registerRange) {
            case RegisterRange.COIL_STATUS:
            case RegisterRange.INPUT_STATUS:
                return maxReadBitCount;
            case RegisterRange.HOLDING_REGISTER:
            case RegisterRange.INPUT_REGISTER:
                return maxReadRegisterCount;
        }
        return -1;
    }

    class FunctionLocatorComparator implements Comparator<KeyedModbusLocator<K>> {
        @Override
        public int compare(KeyedModbusLocator<K> ml1, KeyedModbusLocator<K> ml2) {
            return ml1.getOffset() - ml2.getOffset();
        }
    }
}
