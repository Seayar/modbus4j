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

import io.github.seayar.modbus4j.codec.ModbusCodecType;
import io.github.seayar.modbus4j.concurrent.AdaptiveConcurrency;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.ip.TcpMaster;
import io.github.seayar.modbus4j.ip.UdpMaster;
import io.github.seayar.modbus4j.serial.AsciiMaster;
import io.github.seayar.modbus4j.serial.AsciiUdpMaster;
import io.github.seayar.modbus4j.serial.RtuMaster;
import io.github.seayar.modbus4j.serial.RtuUdpMaster;
import io.github.seayar.modbus4j.transport.ModbusTransport;
import io.github.seayar.modbus4j.transport.NettyTransport;
import io.github.seayar.modbus4j.transport.UdpTransport;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
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
    public void testCreateTcpMasterWithCustomTransport() {
        ModbusTransport transport = new NettyTransport(new IpParameters(), ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        TcpMaster master = new TcpMaster(transport, true);
        assertSame(transport, master.getTransport());
    }

    @Test
    public void testCreateRtuMasterWithCustomTransport() {
        ModbusTransport transport = new NettyTransport(new IpParameters(), ModbusCodecType.RTU, true, null);
        RtuMaster master = new RtuMaster(transport, true);
        assertSame(transport, master.getTransport());
    }

    @Test
    public void testCreateAsciiMasterWithCustomTransport() {
        ModbusTransport transport = new NettyTransport(new IpParameters(), ModbusCodecType.ASCII, true, null);
        AsciiMaster master = new AsciiMaster(transport, false);
        assertSame(transport, master.getTransport());
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
    public void testCreateUdpMaster() {
        ModbusFactory factory = new ModbusFactory();
        ModbusMaster master = factory.createUdpMaster(new IpParameters(), true);
        assertNotNull(master);
        assertTrue(master instanceof UdpMaster);
        assertNotNull(master.getTransport());
    }

    @Test
    public void testCreateRtuUdpMaster() {
        ModbusFactory factory = new ModbusFactory();
        ModbusMaster master = factory.createRtuUdpMaster(new IpParameters(), true);
        assertNotNull(master);
        assertTrue(master instanceof RtuUdpMaster);
        assertNotNull(master.getTransport());
    }

    @Test
    public void testCreateAsciiUdpMaster() {
        ModbusFactory factory = new ModbusFactory();
        ModbusMaster master = factory.createAsciiUdpMaster(new IpParameters(), false);
        assertNotNull(master);
        assertTrue(master instanceof AsciiUdpMaster);
        assertNotNull(master.getTransport());
    }

    @Test
    public void testUdpMasterWithCustomTransport() {
        ModbusTransport transport = new UdpTransport(new IpParameters(), ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        UdpMaster master = new UdpMaster(transport, true);
        assertSame(transport, master.getTransport());
    }

    @Test
    public void testModbus() {
        assertEquals(502, Modbus.DEFAULT_PORT);
    }
}
