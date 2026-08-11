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
package io.github.seayar.modbus4j.poll;

import io.github.seayar.modbus4j.ModbusMaster;
import io.github.seayar.modbus4j.ModbusMasterTestSupport;
import io.github.seayar.modbus4j.base.DataType;
import io.github.seayar.modbus4j.locator.BatchResults;
import io.github.seayar.modbus4j.locator.BaseLocator;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PollTaskTest {

    @Test(timeout = 10000)
    public void testAddLocatorsAndStart() throws Exception {
        ModbusMaster master = ModbusMasterTestSupport.createMaster();
        master.init();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<BatchResults<String>> result = new AtomicReference<>();
        PollTask task = new PollTask(master, new PollListener() {
            @Override
            public void pollCompleted(BatchResults<String> results) {
                result.set(results);
                latch.countDown();
            }

            @Override
            public void pollFailed(Throwable cause) {
            }
        });
        task.addLocator("a", BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED), 500);
        task.setPeriodMillis(50);
        assertEquals(50, task.getPeriodMillis());
        assertEquals(1, task.getLocators().size());
        task.start();
        assertTrue(task.isRunning());
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertNotNull(result.get());
        assertEquals(101, ((Number) result.get().getValue("a")).intValue());
        task.stop();
        assertTrue(!task.isRunning());
        master.destroy();
    }

    @Test(timeout = 10000)
    public void testPollFailure() throws Exception {
        ModbusMaster master = ModbusMasterTestSupport.createUnreachableMaster();
        CountDownLatch latch = new CountDownLatch(1);
        PollTask task = new PollTask(master, new PollListener() {
            @Override
            public void pollCompleted(BatchResults<String> results) {
            }

            @Override
            public void pollFailed(Throwable cause) {
                latch.countDown();
            }
        });
        task.addLocator("a", BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED));
        task.setPeriodMillis(20);
        task.start();
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        task.stop();
    }
}
