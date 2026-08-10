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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExceptionResponseTest {

    @Test
    public void testBasic() {
        ExceptionResponse resp = new ExceptionResponse(1, 2);
        assertEquals(1, resp.getSlaveId());
        assertEquals((byte) 0x82, resp.getFunctionCode());
        assertEquals(2, resp.getExceptionCode());
        assertTrue(resp.isException());
        assertEquals(2, resp.getPduLength());
        ByteBuf buf = Unpooled.buffer();
        resp.writePdu(buf);
        assertArrayEquals(new byte[]{(byte) 0x82, 0x02}, toBytes(buf));
        buf.release();
    }

    @Test
    public void testReadPdu() {
        ExceptionResponse resp = new ExceptionResponse(1, 3);
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x03});
        resp.readPdu(data);
        assertEquals(0, data.readableBytes());
    }

    @Test
    public void testToException() {
        ExceptionResponse resp = new ExceptionResponse(1, 4);
        ModbusCodeException ex = resp.toException();
        assertEquals(4, ex.getExceptionCode());
        assertEquals("Modbus exception code 4", ex.getMessage());
    }

    @Test
    public void testToString() {
        ExceptionResponse resp = new ExceptionResponse(1, 4);
        assertEquals("ExceptionResponse(slaveId=1, functionCode=132)", resp.toString());
    }

    private byte[] toBytes(ByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(0, data);
        return data;
    }
}
