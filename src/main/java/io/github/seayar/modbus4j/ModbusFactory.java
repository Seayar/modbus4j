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
package io.github.seayar.modbus4j;

import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.ip.TcpMaster;
import io.github.seayar.modbus4j.ip.UdpMaster;
import io.github.seayar.modbus4j.serial.AsciiMaster;
import io.github.seayar.modbus4j.serial.AsciiUdpMaster;
import io.github.seayar.modbus4j.serial.RtuMaster;
import io.github.seayar.modbus4j.serial.RtuUdpMaster;

public class ModbusFactory {

    public ModbusMaster createTcpMaster(IpParameters parameters, boolean validateResponse) {
        return new TcpMaster(parameters, validateResponse);
    }

    public ModbusMaster createRtuMaster(IpParameters parameters, boolean validateResponse) {
        return new RtuMaster(parameters, validateResponse);
    }

    public ModbusMaster createAsciiMaster(IpParameters parameters, boolean validateResponse) {
        return new AsciiMaster(parameters, validateResponse);
    }

    public ModbusMaster createUdpMaster(IpParameters parameters, boolean validateResponse) {
        return new UdpMaster(parameters, validateResponse);
    }

    public ModbusMaster createRtuUdpMaster(IpParameters parameters, boolean validateResponse) {
        return new RtuUdpMaster(parameters, validateResponse);
    }

    public ModbusMaster createAsciiUdpMaster(IpParameters parameters, boolean validateResponse) {
        return new AsciiUdpMaster(parameters, validateResponse);
    }
}
