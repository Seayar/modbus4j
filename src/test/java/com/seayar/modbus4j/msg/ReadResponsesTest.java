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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReadResponsesTest {

    @Test
    public void testReadCoilsResponse() {
        ReadCoilsResponse resp = new ReadCoilsResponse(1, 2, new byte[]{0x01, 0x02});
        assertEquals(0x01, resp.getFunctionCode());
        assertEquals(2, resp.getByteCount());
        assertArrayEquals(new byte[]{0x01, 0x02}, resp.getData());
        ByteBuf buf = Unpooled.buffer();
        resp.writePdu(buf);
        assertArrayEquals(new byte[]{0x01, 0x02, 0x01, 0x02}, toBytes(buf));
        buf.release();
        assertFalse(resp.isException());
    }

    @Test
    public void testReadCoilsResponseParse() {
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x03, 0x01, 0x02, 0x04});
        ReadCoilsResponse resp = new ReadCoilsResponse(1, data);
        assertEquals(3, resp.getByteCount());
        assertArrayEquals(new byte[]{0x01, 0x02, 0x04}, resp.getData());
    }

    @Test
    public void testReadDiscreteInputsResponse() {
        ReadDiscreteInputsResponse resp = new ReadDiscreteInputsResponse(1, 1, new byte[]{0x01});
        assertEquals(0x02, resp.getFunctionCode());
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x01, 0x05});
        ReadDiscreteInputsResponse parsed = new ReadDiscreteInputsResponse(1, data);
        assertArrayEquals(new byte[]{0x05}, parsed.getData());
    }

    @Test
    public void testReadHoldingRegistersResponse() {
        ReadHoldingRegistersResponse resp = new ReadHoldingRegistersResponse(1, 4,
                new byte[]{0x00, 0x01, 0x00, 0x02});
        assertEquals(0x03, resp.getFunctionCode());
        assertEquals(4, resp.getByteCount());
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x02, 0x00, 0x05});
        ReadHoldingRegistersResponse parsed = new ReadHoldingRegistersResponse(1, data);
        assertArrayEquals(new byte[]{0x00, 0x05}, parsed.getData());
    }

    @Test
    public void testReadInputRegistersResponse() {
        ReadInputRegistersResponse resp = new ReadInputRegistersResponse(1, 2, new byte[]{0x00, 0x07});
        assertEquals(0x04, resp.getFunctionCode());
        assertEquals(2, resp.getByteCount());
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x02, 0x00, 0x08});
        ReadInputRegistersResponse parsed = new ReadInputRegistersResponse(1, data);
        assertArrayEquals(new byte[]{0x00, 0x08}, parsed.getData());
    }

    private byte[] toBytes(ByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(0, data);
        return data;
    }
}
