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

public class WriteResponsesTest {

    @Test
    public void testWriteCoilResponse() {
        WriteCoilResponse resp = new WriteCoilResponse(1, 5, true);
        assertEquals(0x05, resp.getFunctionCode());
        assertEquals(5, resp.getOffset());
        assertTrue(resp.getValueBoolean());
        ByteBuf buf = Unpooled.buffer();
        resp.writePdu(buf);
        assertArrayEquals(new byte[]{0x05, 0x00, 5, (byte) 0xff, 0x00}, toBytes(buf));
        buf.release();

        WriteCoilResponse parsed = new WriteCoilResponse(1,
                Unpooled.wrappedBuffer(new byte[]{0x00, 0x05, 0x00, 0x00}));
        assertEquals(5, parsed.getOffset());
        assertFalse(parsed.getValueBoolean());
    }

    @Test
    public void testWriteRegisterResponse() {
        WriteRegisterResponse resp = new WriteRegisterResponse(1, 10, 0x1234);
        assertEquals(0x06, resp.getFunctionCode());
        assertEquals(10, resp.getOffset());
        assertEquals(0x1234, resp.getValue());
        WriteRegisterResponse parsed = new WriteRegisterResponse(1,
                Unpooled.wrappedBuffer(new byte[]{0x00, 0x0a, 0x12, 0x34}));
        assertEquals(0x1234, parsed.getValue());
    }

    @Test
    public void testWriteCoilsResponse() {
        WriteCoilsResponse resp = new WriteCoilsResponse(1, 0, 8);
        assertEquals(0x0f, resp.getFunctionCode());
        assertEquals(0, resp.getStartOffset());
        assertEquals(8, resp.getNumberOfRegisters());
        WriteCoilsResponse parsed = new WriteCoilsResponse(1,
                Unpooled.wrappedBuffer(new byte[]{0x00, 0x05, 0x00, 0x10}));
        assertEquals(5, parsed.getStartOffset());
        assertEquals(16, parsed.getNumberOfRegisters());
    }

    @Test
    public void testWriteRegistersResponse() {
        WriteRegistersResponse resp = new WriteRegistersResponse(1, 10, 2);
        assertEquals(0x10, resp.getFunctionCode());
        assertEquals(10, resp.getStartOffset());
        assertEquals(2, resp.getNumberOfRegisters());
        WriteRegistersResponse parsed = new WriteRegistersResponse(1,
                Unpooled.wrappedBuffer(new byte[]{0x00, 0x0a, 0x00, 0x02}));
        assertEquals(2, parsed.getNumberOfRegisters());
    }

    @Test
    public void testWriteCoilsResponsePdu() {
        WriteCoilsResponse resp = new WriteCoilsResponse(1, 5, 16);
        ByteBuf buf = Unpooled.buffer();
        resp.writePdu(buf);
        assertArrayEquals(new byte[]{0x0f, 0x00, 5, 0x00, 16}, toBytes(buf));
        buf.release();
        ByteBuf read = Unpooled.wrappedBuffer(new byte[]{0x00, 5, 0x00, 16});
        resp.readPdu(read);
    }

    @Test
    public void testWriteRegistersResponsePdu() {
        WriteRegistersResponse resp = new WriteRegistersResponse(1, 10, 2);
        ByteBuf buf = Unpooled.buffer();
        resp.writePdu(buf);
        assertArrayEquals(new byte[]{0x10, 0x00, 10, 0x00, 2}, toBytes(buf));
        buf.release();
        ByteBuf read = Unpooled.wrappedBuffer(new byte[]{0x00, 10, 0x00, 2});
        resp.readPdu(read);
    }

    @Test
    public void testWriteCoilResponsePdu() {
        WriteCoilResponse resp = new WriteCoilResponse(1, 5, false);
        ByteBuf buf = Unpooled.buffer();
        resp.writePdu(buf);
        assertArrayEquals(new byte[]{0x05, 0x00, 5, 0x00, 0x00}, toBytes(buf));
        buf.release();
        ByteBuf read = Unpooled.wrappedBuffer(new byte[]{0x00, 5, 0x00, 0x00});
        resp.readPdu(read);
    }

    @Test
    public void testWriteRegisterResponsePdu() {
        WriteRegisterResponse resp = new WriteRegisterResponse(1, 10, 0x1234);
        ByteBuf buf = Unpooled.buffer();
        resp.writePdu(buf);
        assertArrayEquals(new byte[]{0x06, 0x00, 10, 0x12, 0x34}, toBytes(buf));
        buf.release();
        ByteBuf read = Unpooled.wrappedBuffer(new byte[]{0x00, 10, 0x12, 0x34});
        resp.readPdu(read);
    }

    private byte[] toBytes(ByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(0, data);
        return data;
    }
}
