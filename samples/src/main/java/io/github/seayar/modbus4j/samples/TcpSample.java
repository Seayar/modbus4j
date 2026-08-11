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
package io.github.seayar.modbus4j.samples;

import io.github.seayar.modbus4j.ModbusFactory;
import io.github.seayar.modbus4j.ModbusMaster;
import io.github.seayar.modbus4j.base.DataType;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.locator.BaseLocator;
import io.github.seayar.modbus4j.locator.BatchRead;
import io.github.seayar.modbus4j.locator.BatchResults;
import io.github.seayar.modbus4j.msg.AbstractModbusResponse;
import io.github.seayar.modbus4j.msg.ReadHoldingRegistersRequest;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Modbus TCP master: single-point reads, batch reads, writes, bit access,
 * strings and a direct asynchronous read through the transport.
 * <p>
 * Run {@link EmbeddedModbusSlave} first, then:
 * {@code mvn -f samples/pom.xml exec:java -Dexec.mainClass=io.github.seayar.modbus4j.samples.TcpSample}
 */
public final class TcpSample {

    public static void main(String[] args) throws Exception {
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(1502);
        params.setReadTimeoutMillis(3000);

        ModbusMaster master = new ModbusFactory().createTcpMaster(params, true);
        master.init();
        try {
            BaseLocator<Number> temp = BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT);
            System.out.println("holding register 0 (float) = " + master.getValue(temp));

            master.setValue(BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED), 42);
            System.out.println("write+read register 100 = "
                    + master.getValue(BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED)));

            BaseLocator<Boolean> bit = BaseLocator.holdingRegisterBit(1, 100, 3);
            System.out.println("register 100 bit 3 = " + master.getValue(bit));
            master.setValue(bit, true);

            BatchRead<String> batch = new BatchRead<>();
            batch.addLocator("coil", BaseLocator.coilStatus(1, 0));
            batch.addLocator("hr", BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT));
            batch.addLocator("str", BaseLocator.holdingRegisterString(1, 200, DataType.VARCHAR, 4));
            BatchResults<String> results = master.send(batch);
            System.out.println("batch: coil=" + results.getValue("coil")
                    + ", hr=" + results.getValue("hr")
                    + ", str=" + results.getValue("str"));

            Future<AbstractModbusResponse> future = master.getTransport()
                    .sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
            AbstractModbusResponse response = future.get(3, TimeUnit.SECONDS);
            System.out.println("async read -> " + response);
        } finally {
            master.destroy();
        }
    }
}
