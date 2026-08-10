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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IpParametersTest {

    @Test
    public void testDefaults() {
        IpParameters p = new IpParameters();
        assertEquals("localhost", p.getHost());
        assertEquals(502, p.getPort());
        assertEquals(5000, p.getConnectTimeoutMillis());
        assertEquals(10000, p.getReadTimeoutMillis());
        assertEquals(5000, p.getWriteTimeoutMillis());
        assertTrue(p.isKeepAlive());
        assertTrue(p.isTcpNoDelay());
        assertEquals(0, p.getSoLinger());
        assertEquals("localhost:502", p.getAddress());
        assertEquals("IpParameters(host=localhost, port=502)", p.toString());
    }

    @Test
    public void testSetters() {
        IpParameters p = new IpParameters();
        p.setHost("10.0.0.1");
        p.setPort(1502);
        p.setConnectTimeoutMillis(1000);
        p.setReadTimeoutMillis(2000);
        p.setWriteTimeoutMillis(3000);
        p.setKeepAlive(false);
        p.setTcpNoDelay(false);
        p.setSoLinger(5);
        assertEquals("10.0.0.1", p.getHost());
        assertEquals(1502, p.getPort());
        assertEquals(1000, p.getConnectTimeoutMillis());
        assertEquals(2000, p.getReadTimeoutMillis());
        assertEquals(3000, p.getWriteTimeoutMillis());
        assertFalse(p.isKeepAlive());
        assertFalse(p.isTcpNoDelay());
        assertEquals(5, p.getSoLinger());
        assertEquals("10.0.0.1:1502", p.getAddress());
    }
}
