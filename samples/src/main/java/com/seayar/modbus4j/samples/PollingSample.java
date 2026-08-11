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
 * @date 2026-08-11
 */
package com.seayar.modbus4j.samples;

import com.seayar.modbus4j.ModbusFactory;
import com.seayar.modbus4j.ModbusMaster;
import com.seayar.modbus4j.base.DataType;
import com.seayar.modbus4j.ip.IpParameters;
import com.seayar.modbus4j.locator.BaseLocator;
import com.seayar.modbus4j.locator.BatchResults;
import com.seayar.modbus4j.poll.PollListener;
import com.seayar.modbus4j.poll.PollTask;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Periodic polling with {@link PollTask} and a {@link PollListener}. Each
 * poll cycle sends one merged {@code BatchRead}; results arrive on the poll
 * thread.
 * <p>
 * Run {@link EmbeddedModbusSlave} first, then this sample. It stops after a
 * handful of cycles.
 */
public final class PollingSample {

    public static void main(String[] args) throws Exception {
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(1502);
        params.setReadTimeoutMillis(3000);

        ModbusMaster master = new ModbusFactory().createTcpMaster(params, true);
        master.init();

        AtomicInteger cycles = new AtomicInteger();
        PollTask task = new PollTask(master, new PollListener() {
            @Override
            public void pollCompleted(BatchResults<String> results) {
                System.out.println("cycle " + cycles.incrementAndGet()
                        + ": temp=" + results.getValue("temp")
                        + ", on=" + results.getValue("on"));
            }

            @Override
            public void pollFailed(Throwable cause) {
                cause.printStackTrace();
            }
        });

        task.addLocator("temp", BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT));
        task.addLocator("on", BaseLocator.coilStatus(1, 0), 5000L);
        task.setPeriodMillis(1000);
        task.start();

        long deadline = System.currentTimeMillis() + 4000;
        while (cycles.get() < 3 && System.currentTimeMillis() < deadline)
            Thread.sleep(100);

        task.stop();
        master.destroy();
        System.out.println("polling sample done");
    }
}
