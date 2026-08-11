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
package com.seayar.modbus4j.ip;

import com.seayar.modbus4j.ModbusMaster;
import com.seayar.modbus4j.codec.ModbusCodecType;
import com.seayar.modbus4j.concurrent.AdaptiveConcurrency;
import com.seayar.modbus4j.transport.ModbusTransport;
import com.seayar.modbus4j.transport.NettyTransport;

public class TcpMaster extends ModbusMaster {

    public TcpMaster(IpParameters parameters, boolean validateResponse) {
        super(createTransport(parameters), validateResponse);
    }

    public TcpMaster(ModbusTransport transport, boolean validateResponse) {
        super(transport, validateResponse);
    }

    private static ModbusTransport createTransport(IpParameters parameters) {
        AdaptiveConcurrency concurrency = new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1);
        return new NettyTransport(parameters, ModbusCodecType.TCP, false, concurrency);
    }
}
