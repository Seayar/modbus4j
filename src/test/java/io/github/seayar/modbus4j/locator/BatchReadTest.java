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
package io.github.seayar.modbus4j.locator;

import io.github.seayar.modbus4j.base.DataType;
import io.github.seayar.modbus4j.base.RegisterRange;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BatchReadTest {

    @Test
    public void testPartitionBySlaveAndRange() {
        BatchRead<String> batch = new BatchRead<>();
        batch.addLocator("a", BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator("b", BaseLocator.holdingRegister(2, 100, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator("c", BaseLocator.coilStatus(1, 0));
        assertEquals(3, batch.getReadFunctionGroups().size());
    }

    @Test
    public void testPartitionGaps() {
        BatchRead<String> batch = new BatchRead<>();
        for (int i = 0; i < 100; i += 2)
            batch.addLocator("k" + i, BaseLocator.holdingRegister(1, i, DataType.TWO_BYTE_INT_UNSIGNED));
        assertEquals(1, batch.getReadFunctionGroups().size());
    }

    @Test
    public void testPartitionByMaxCount() {
        BatchRead<String> batch = new BatchRead<>();
        for (int i = 0; i < 300; i++)
            batch.addLocator("k" + i, BaseLocator.holdingRegister(1, i, DataType.TWO_BYTE_INT_UNSIGNED));
        assertEquals(3, batch.getReadFunctionGroups().size());
    }

    @Test
    public void testContiguousRequests() {
        BatchRead<String> batch = new BatchRead<>();
        batch.setContiguousRequests(true);
        batch.addLocator("a", BaseLocator.holdingRegister(1, 0, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator("b", BaseLocator.holdingRegister(1, 5, DataType.TWO_BYTE_INT_UNSIGNED));
        List<io.github.seayar.modbus4j.base.ReadFunctionGroup<String>> groups = batch.getReadFunctionGroups();
        assertEquals(2, groups.size());
    }

    @Test
    public void testContiguousRequestsAbutting() {
        BatchRead<String> batch = new BatchRead<>();
        batch.setContiguousRequests(true);
        batch.addLocator("a", BaseLocator.holdingRegister(1, 0, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator("b", BaseLocator.holdingRegister(1, 1, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator("c", BaseLocator.holdingRegister(1, 3, DataType.TWO_BYTE_INT_UNSIGNED));
        assertEquals(2, batch.getReadFunctionGroups().size());
    }

    @Test
    public void testConfigFlags() {
        BatchRead<String> batch = new BatchRead<>();
        assertTrue(batch.getReadFunctionGroups().isEmpty());
        batch.setErrorsInResults(true);
        batch.setExceptionsInResults(true);
        batch.setMaxReadBitCount(100);
        batch.setMaxReadRegisterCount(50);
        assertTrue(batch.isErrorsInResults());
        assertTrue(batch.isExceptionsInResults());
        assertEquals(100, batch.getMaxReadBitCount());
        assertEquals(50, batch.getMaxReadRegisterCount());
    }

    @Test
    public void testCancel() {
        BatchRead<String> batch = new BatchRead<>();
        assertTrue(!batch.isCancel());
        batch.setCancel(true);
        assertTrue(batch.isCancel());
        batch.setCancel(false);
    }
}
