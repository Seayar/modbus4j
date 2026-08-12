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
package io.github.seayar.modbus4j.samples;

import io.github.seayar.modbus4j.ModbusFactory;
import io.github.seayar.modbus4j.ModbusMaster;
import io.github.seayar.modbus4j.base.DataType;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.locator.BaseLocator;

/**
 * The UDP modes, matching the Modbus slave simulator:
 * <ul>
 *   <li>Modbus UDP (MBAP over UDP)       -> port 1502</li>
 *   <li>RTU over UDP                     -> port 1503</li>
 *   <li>ASCII over UDP                   -> port 1504</li>
 * </ul>
 * Run {@link EmbeddedModbusSlave} first, then this sample.
 */
public final class UdpSample {

    public static void main(String[] args) throws Exception {
        int basePort = args.length > 0 ? Integer.parseInt(args[0]) : 1502;
        ModbusFactory factory = new ModbusFactory();

        IpParameters udp = new IpParameters();
        udp.setHost("127.0.0.1");
        udp.setPort(basePort);
        udp.setReadTimeoutMillis(3000);
        ModbusMaster mbap = factory.createUdpMaster(udp, true);
        mbap.init();
        try {
            System.out.println("UDP  (MBAP)   register 0 = "
                    + mbap.getValue(BaseLocator.holdingRegister(1, 0, DataType.TWO_BYTE_INT_UNSIGNED)));
        } finally {
            mbap.destroy();
        }

        IpParameters rtu = new IpParameters();
        rtu.setHost("127.0.0.1");
        rtu.setPort(basePort + 1);
        rtu.setReadTimeoutMillis(3000);
        ModbusMaster rtuUdp = factory.createRtuUdpMaster(rtu, true);
        rtuUdp.init();
        try {
            System.out.println("RTU over UDP  register 0 = "
                    + rtuUdp.getValue(BaseLocator.holdingRegister(1, 0, DataType.TWO_BYTE_INT_UNSIGNED)));
        } finally {
            rtuUdp.destroy();
        }

        IpParameters ascii = new IpParameters();
        ascii.setHost("127.0.0.1");
        ascii.setPort(basePort + 2);
        ascii.setReadTimeoutMillis(3000);
        ModbusMaster asciiUdp = factory.createAsciiUdpMaster(ascii, true);
        asciiUdp.init();
        try {
            System.out.println("ASCII over UDP register 0 = "
                    + asciiUdp.getValue(BaseLocator.holdingRegister(1, 0, DataType.TWO_BYTE_INT_UNSIGNED)));
        } finally {
            asciiUdp.destroy();
        }
    }
}
