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
 * @date 2026-08-12
 */
package io.github.seayar.modbus4j.ip;

import io.github.seayar.modbus4j.ModbusMaster;
import io.github.seayar.modbus4j.codec.ModbusCodecType;
import io.github.seayar.modbus4j.concurrent.AdaptiveConcurrency;
import io.github.seayar.modbus4j.transport.ModbusTransport;
import io.github.seayar.modbus4j.transport.UdpTransport;

public class UdpMaster extends ModbusMaster {

    public UdpMaster(IpParameters parameters, boolean validateResponse) {
        super(createTransport(parameters), validateResponse);
    }

    public UdpMaster(ModbusTransport transport, boolean validateResponse) {
        super(transport, validateResponse);
    }

    private static ModbusTransport createTransport(IpParameters parameters) {
        AdaptiveConcurrency concurrency = new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1);
        return new UdpTransport(parameters, ModbusCodecType.TCP, false, concurrency);
    }
}
