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

import com.seayar.modbus4j.ModbusMaster;
import com.seayar.modbus4j.base.DataType;
import com.seayar.modbus4j.exception.ModbusInitException;
import com.seayar.modbus4j.exception.ModbusTransportException;
import com.seayar.modbus4j.locator.BaseLocator;
import com.seayar.modbus4j.msg.AbstractModbusRequest;
import com.seayar.modbus4j.msg.AbstractModbusResponse;
import com.seayar.modbus4j.transport.ModbusTransport;

import java.util.concurrent.Future;

/**
 * Implementing the full {@link ModbusTransport} SPI and handing it to a
 * {@code ModbusMaster} subclass. All high-level behaviour (getValue, setValue,
 * BatchRead, polling) works unchanged on top of any transport.
 * <p>
 * This stub returns a fixed "not connected" error so it compiles and runs
 * without a device; replace the three TODO bodies with your own channel logic
 * (shared connection, UDP, virtual multi-drop, …).
 */
public final class CustomTransportSample {

    /** A minimal transport implementation to show the SPI contract. */
    public static final class MyTransport implements ModbusTransport {
        @Override
        public void init() throws ModbusInitException {
        }

        @Override
        public void destroy() {
        }

        @Override
        public boolean isInitialized() {
            return false;
        }

        @Override
        public AbstractModbusResponse send(AbstractModbusRequest request) throws ModbusTransportException {
            throw new ModbusTransportException("MyTransport not connected");
        }

        @Override
        public Future<AbstractModbusResponse> sendAsync(AbstractModbusRequest request)
                throws ModbusTransportException {
            throw new ModbusTransportException("MyTransport not connected");
        }

        @Override
        public int getInFlight() {
            return 0;
        }

        @Override
        public int getMaxInFlight() {
            return 1;
        }

        @Override
        public void setMaxInFlight(int maxInFlight) {
        }
    }

    /** Master subclass that accepts any transport. */
    public static final class MyModbusMaster extends ModbusMaster {
        public MyModbusMaster(ModbusTransport transport, boolean validateResponse) {
            super(transport, validateResponse);
        }
    }

    public static void main(String[] args) throws Exception {
        ModbusMaster master = new MyModbusMaster(new MyTransport(), true);
        try {
            master.getValue(BaseLocator.holdingRegister(1, 0, DataType.TWO_BYTE_INT_UNSIGNED));
        } catch (ModbusTransportException expected) {
            System.out.println("MyTransport correctly reports: " + expected.getMessage());
        }
        master.destroy();
    }
}
