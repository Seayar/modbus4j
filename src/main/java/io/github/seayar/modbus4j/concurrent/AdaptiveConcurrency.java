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

public class AdaptiveConcurrency {
    private final int minInFlight;
    private final int maxInFlight;
    private volatile int currentInFlight;
    private long totalResponseTimeNanos;
    private long sampleCount;
    private long errorCount;
    private final long targetResponseTimeNanos;
    private final double errorThreshold;

    public AdaptiveConcurrency(int minInFlight, int maxInFlight, long targetResponseTimeNanos,
            double errorThreshold) {
        this.minInFlight = minInFlight;
        this.maxInFlight = maxInFlight;
        this.currentInFlight = minInFlight;
        this.targetResponseTimeNanos = targetResponseTimeNanos;
        this.errorThreshold = errorThreshold;
    }

    public synchronized void record(boolean success, long responseTimeNanos) {
        totalResponseTimeNanos += responseTimeNanos;
        sampleCount++;
        if (!success)
            errorCount++;
    }

    public synchronized int adjust() {
        if (sampleCount == 0)
            return currentInFlight;
        double errorRate = (double) errorCount / sampleCount;
        double avgResponseTime = (double) totalResponseTimeNanos / sampleCount;
        if (errorRate > errorThreshold || avgResponseTime > targetResponseTimeNanos * 2)
            decrease();
        else if (errorRate < errorThreshold / 2 && avgResponseTime < targetResponseTimeNanos)
            increase();
        resetSamples();
        return currentInFlight;
    }

    private void increase() {
        if (currentInFlight < maxInFlight)
            currentInFlight++;
    }

    private void decrease() {
        if (currentInFlight > minInFlight)
            currentInFlight--;
    }

    private void resetSamples() {
        totalResponseTimeNanos = 0;
        sampleCount = 0;
        errorCount = 0;
    }

    public synchronized int getCurrentInFlight() {
        return currentInFlight;
    }

    public int getMinInFlight() {
        return minInFlight;
    }

    public int getMaxInFlight() {
        return maxInFlight;
    }
}
