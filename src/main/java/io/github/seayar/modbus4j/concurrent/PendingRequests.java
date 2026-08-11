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

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

public class PendingRequests {
    private final Map<Integer, PendingRequest> requests = new ConcurrentHashMap<>();
    private final Queue<Integer> order = new ConcurrentLinkedQueue<>();

    public void put(PendingRequest request) {
        if (requests.putIfAbsent(request.getTransactionId(), request) == null)
            order.add(request.getTransactionId());
    }

    public PendingRequest remove(int transactionId) {
        PendingRequest request = requests.remove(transactionId);
        if (request != null)
            order.remove(transactionId);
        return request;
    }

    public PendingRequest removeFirst() {
        Integer transactionId = order.poll();
        if (transactionId == null)
            return null;
        PendingRequest request = requests.remove(transactionId);
        if (request == null)
            return removeFirst();
        return request;
    }

    public PendingRequest get(int transactionId) {
        return requests.get(transactionId);
    }

    public Collection<PendingRequest> values() {
        return requests.values();
    }

    public int size() {
        return requests.size();
    }

    public boolean isEmpty() {
        return requests.isEmpty();
    }

    public void clear() {
        requests.clear();
        order.clear();
    }

    public void expire(long now) {
        for (PendingRequest request : requests.values()) {
            if (request.isExpired(now)) {
                remove(request.getTransactionId());
                request.getFuture().completeExceptionally(new TimeoutException("response timeout"));
            }
        }
    }

    public void failAll(Throwable t) {
        for (PendingRequest request : requests.values())
            request.getFuture().completeExceptionally(t);
        clear();
    }

    public void close() {
        failAll(new java.util.concurrent.TimeoutException("transport closed"));
    }

    public CompletableFuture<Object> putAndGetFuture(int transactionId, int timeoutMillis) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        put(new PendingRequest(transactionId, future, timeoutMillis));
        return future;
    }
}
