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
package com.seayar.modbus4j.msg;

import com.seayar.modbus4j.exception.ModbusCodeException;
import com.seayar.modbus4j.exception.ModbusException;
import com.seayar.modbus4j.exception.ModbusIdException;
import com.seayar.modbus4j.exception.ModbusInitException;
import com.seayar.modbus4j.exception.ModbusTransportException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ExceptionTest {

    @Test
    public void testModbusException() {
        assertNull(new ModbusException().getMessage());
        assertEquals("msg", new ModbusException("msg").getMessage());
        Throwable cause = new RuntimeException("cause");
        assertEquals("msg", new ModbusException("msg", cause).getMessage());
        assertSame(cause, new ModbusException(cause).getCause());
    }

    @Test
    public void testModbusCodeException() {
        ModbusCodeException ex = new ModbusCodeException((byte) 2);
        assertEquals(2, ex.getExceptionCode());
    }

    @Test
    public void testModbusTransportException() {
        assertNull(new ModbusTransportException().getMessage());
        assertEquals("msg", new ModbusTransportException("msg").getMessage());
        Throwable cause = new RuntimeException("cause");
        assertSame(cause, new ModbusTransportException(cause).getCause());
    }

    @Test
    public void testModbusInitException() {
        assertNull(new ModbusInitException().getMessage());
        assertEquals("msg", new ModbusInitException("msg").getMessage());
        Throwable cause = new RuntimeException("cause");
        assertEquals("msg", new ModbusInitException("msg", cause).getMessage());
        assertSame(cause, new ModbusInitException(cause).getCause());
    }

    @Test
    public void testModbusIdException() {
        assertNull(new ModbusIdException().getMessage());
        assertEquals("msg", new ModbusIdException("msg").getMessage());
        Throwable cause = new RuntimeException("cause");
        assertEquals("msg", new ModbusIdException("msg", cause).getMessage());
        assertSame(cause, new ModbusIdException(cause).getCause());
    }
}
