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
package io.github.seayar.modbus4j.concurrent;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PendingRequestsTest {

    @Test
    public void testPutRemove() {
        PendingRequests pending = new PendingRequests();
        CompletableFuture<Object> future = pending.putAndGetFuture(1, 1000);
        assertTrue(pending.size() == 1);
        assertFalse(pending.isEmpty());
        PendingRequest req = pending.get(1);
        assertEquals(1, req.getTransactionId());
        PendingRequest removed = pending.remove(1);
        assertEquals(1, removed.getTransactionId());
        assertNull(pending.remove(1));
        assertTrue(pending.isEmpty());
    }

    @Test
    public void testRemoveFirst() {
        PendingRequests pending = new PendingRequests();
        pending.putAndGetFuture(1, 1000);
        pending.putAndGetFuture(2, 1000);
        PendingRequest first = pending.removeFirst();
        assertEquals(1, first.getTransactionId());
        assertEquals(1, pending.size());
    }

    @Test
    public void testRemoveFirstEmpty() {
        PendingRequests pending = new PendingRequests();
        assertNull(pending.removeFirst());
    }

    @Test
    public void testComplete() {
        PendingRequests pending = new PendingRequests();
        CompletableFuture<Object> future = pending.putAndGetFuture(1, 1000);
        PendingRequest req = pending.get(1);
        req.getFuture().complete("done");
        assertEquals("done", future.join());
    }

    @Test
    public void testFailAll() {
        PendingRequests pending = new PendingRequests();
        CompletableFuture<Object> f1 = pending.putAndGetFuture(1, 1000);
        CompletableFuture<Object> f2 = pending.putAndGetFuture(2, 1000);
        pending.failAll(new RuntimeException("boom"));
        assertTrue(f1.isCompletedExceptionally());
        assertTrue(f2.isCompletedExceptionally());
        assertTrue(pending.isEmpty());
    }

    @Test
    public void testClose() {
        PendingRequests pending = new PendingRequests();
        CompletableFuture<Object> f1 = pending.putAndGetFuture(1, 1000);
        pending.close();
        assertTrue(f1.isCompletedExceptionally());
    }

    @Test
    public void testExpire() throws Exception {
        PendingRequests pending = new PendingRequests();
        CompletableFuture<Object> future = pending.putAndGetFuture(1, 1000);
        long now = System.currentTimeMillis() + 5000;
        pending.expire(now);
        assertTrue(pending.isEmpty());
        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void testExpireNotStale() {
        PendingRequests pending = new PendingRequests();
        pending.putAndGetFuture(1, 10000);
        long now = System.currentTimeMillis() + 100;
        pending.expire(now);
        assertEquals(1, pending.size());
    }

    @Test
    public void testClear() {
        PendingRequests pending = new PendingRequests();
        pending.putAndGetFuture(1, 1000);
        pending.putAndGetFuture(2, 1000);
        pending.clear();
        assertTrue(pending.isEmpty());
    }

    @Test
    public void testValues() {
        PendingRequests pending = new PendingRequests();
        pending.putAndGetFuture(1, 1000);
        pending.putAndGetFuture(2, 1000);
        assertEquals(2, pending.values().size());
    }
}
