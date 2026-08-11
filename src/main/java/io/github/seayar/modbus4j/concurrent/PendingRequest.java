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

import java.util.concurrent.CompletableFuture;

public class PendingRequest {
    private final int transactionId;
    private final CompletableFuture<Object> future;
    private final long timestamp;
    private final int timeoutMillis;

    public PendingRequest(int transactionId, CompletableFuture<Object> future, int timeoutMillis) {
        this.transactionId = transactionId;
        this.future = future;
        this.timestamp = System.currentTimeMillis();
        this.timeoutMillis = timeoutMillis;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public CompletableFuture<Object> getFuture() {
        return future;
    }

    public boolean isExpired(long now) {
        return now - timestamp > timeoutMillis;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }
}
