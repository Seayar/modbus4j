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

/**
 * RTU-over-TCP and ASCII-over-TCP masters for devices behind serial-to-TCP
 * gateways. Both run strictly synchronous: the next request is only sent after
 * the previous response arrives, so serial lines are never flooded.
 * <p>
 * NOTE: {@link EmbeddedModbusSlave} speaks TCP framing; to exercise the RTU /
 * ASCII transports against it, point an RTU/ASCII serial-to-TCP gateway at the
 * slave, or use one of the codec round-trip tests as a reference.
 */
public final class RtuAsciiSample {

    public static void main(String[] args) throws Exception {
        IpParameters rtuParams = new IpParameters();
        rtuParams.setHost("127.0.0.1");
        rtuParams.setPort(1502);
        rtuParams.setReadTimeoutMillis(3000);

        ModbusMaster rtu = new ModbusFactory().createRtuMaster(rtuParams, true);
        rtu.init();
        try {
            Object value = rtu.getValue(BaseLocator.holdingRegister(1, 0, DataType.TWO_BYTE_INT_UNSIGNED));
            System.out.println("RTU  holding register 0 = " + value);
        } finally {
            rtu.destroy();
        }

        ModbusMaster ascii = new ModbusFactory().createAsciiMaster(rtuParams, true);
        ascii.init();
        try {
            Object value = ascii.getValue(BaseLocator.coilStatus(1, 0));
            System.out.println("ASCII coil 0 = " + value);
        } finally {
            ascii.destroy();
        }
    }
}
