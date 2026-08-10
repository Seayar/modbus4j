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
package com.seayar.modbus4j;

import com.seayar.modbus4j.ip.IpParameters;
import com.seayar.modbus4j.ip.TcpMaster;
import com.seayar.modbus4j.serial.AsciiMaster;
import com.seayar.modbus4j.serial.RtuMaster;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ModbusFactoryTest {

    @Test
    public void testCreateTcpMaster() {
        ModbusFactory factory = new ModbusFactory();
        ModbusMaster master = factory.createTcpMaster(new IpParameters(), true);
        assertNotNull(master);
        assertTrue(master instanceof TcpMaster);
        assertFalse(master.isInitialized());
    }

    @Test
    public void testCreateRtuMaster() {
        ModbusFactory factory = new ModbusFactory();
        ModbusMaster master = factory.createRtuMaster(new IpParameters(), true);
        assertNotNull(master);
        assertTrue(master instanceof RtuMaster);
        assertNotNull(master.getTransport());
    }

    @Test
    public void testCreateAsciiMaster() {
        ModbusFactory factory = new ModbusFactory();
        ModbusMaster master = factory.createAsciiMaster(new IpParameters(), false);
        assertNotNull(master);
        assertTrue(master instanceof AsciiMaster);
        assertNotNull(master.getTransport());
    }

    @Test
    public void testModbus() {
        assertEquals(502, Modbus.DEFAULT_PORT);
    }
}
