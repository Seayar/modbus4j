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
import io.github.seayar.modbus4j.locator.BaseLocator;
import io.github.seayar.modbus4j.locator.BatchRead;
import io.github.seayar.modbus4j.locator.BatchResults;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PollTask {
    private final ModbusMaster master;
    private final List<PolledLocator> locators = new ArrayList<>();
    private final PollListener listener;
    private ScheduledExecutorService scheduler;
    private long periodMillis = 1000;

    public PollTask(ModbusMaster master, PollListener listener) {
        this.master = master;
        this.listener = listener;
    }

    public void addLocator(String key, BaseLocator<?> locator) {
        addLocator(key, locator, periodMillis);
    }

    public void addLocator(String key, BaseLocator<?> locator, long updatePeriodMillis) {
        locators.add(new PolledLocator(key, locator, updatePeriodMillis));
    }

    public void setPeriodMillis(long periodMillis) {
        this.periodMillis = periodMillis;
    }

    public long getPeriodMillis() {
        return periodMillis;
    }

    public void start() {
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "modbus-poll");
                t.setDaemon(true);
                return t;
            });
            scheduler.scheduleWithFixedDelay(this::poll, 0, periodMillis, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    public boolean isRunning() {
        return scheduler != null && !scheduler.isShutdown();
    }

    void poll() {
        try {
            BatchResults<String> results = master.send(buildBatch());
            listener.pollCompleted(results);
        } catch (Throwable t) {
            listener.pollFailed(t);
        }
    }

    private BatchRead<String> buildBatch() {
        BatchRead<String> batch = new BatchRead<>();
        for (PolledLocator polled : locators)
            batch.addLocator(polled.getKey(), polled.getLocator());
        return batch;
    }

    public List<PolledLocator> getLocators() {
        return locators;
    }
}
