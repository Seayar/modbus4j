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

public class WriteRequestsTest {

    @Test
    public void testWriteCoilRequest() {
        WriteCoilRequest req = new WriteCoilRequest(1, 5, true);
        assertEquals(0x05, req.getFunctionCode());
        assertEquals(5, req.getOffset());
        assertEquals(0xff00, req.getValue());
        assertTrue(req.getValueBoolean());
        assertTrue(req.createResponse(Unpooled.wrappedBuffer(new byte[]{0x05, 0, 5, (byte) 0xff, 0}))
                instanceof WriteCoilResponse);

        WriteCoilRequest off = new WriteCoilRequest(1, 5, 0);
        assertFalse(off.getValueBoolean());
        assertEquals(0, off.getValue());
    }

    @Test
    public void testWriteRegisterRequest() {
        WriteRegisterRequest req = new WriteRegisterRequest(1, 10, 0x1234);
        assertEquals(0x06, req.getFunctionCode());
        assertEquals(10, req.getOffset());
        assertEquals(0x1234, req.getValue());
        assertEquals(5, req.getPduLength());
        ByteBuf buf = Unpooled.buffer();
        req.writePdu(buf);
        assertArrayEquals(new byte[]{0x06, 0x00, 10, 0x12, 0x34}, toBytes(buf));
        buf.release();
    }

    @Test
    public void testWriteCoilsRequest() {
        WriteCoilsRequest req = new WriteCoilsRequest(1, 0, new byte[]{0x01});
        assertEquals(0x0f, req.getFunctionCode());
        assertEquals(0, req.getStartOffset());
        assertEquals(8, req.getNumberOfRegisters());
        assertArrayEquals(new byte[]{0x01}, req.getData());
        assertTrue(req.createResponse(Unpooled.wrappedBuffer(new byte[]{0x0f, 0x00, 0x00, 0x00, 0x08}))
                instanceof WriteCoilsResponse);

        WriteCoilsRequest req2 = new WriteCoilsRequest(1, 0, 3, new byte[]{0x05});
        assertEquals(3, req2.getNumberOfRegisters());
    }

    @Test
    public void testWriteRegistersRequest() {
        WriteRegistersRequest req = new WriteRegistersRequest(1, 10, new byte[]{0x00, 0x01, 0x00, 0x02});
        assertEquals(0x10, req.getFunctionCode());
        assertEquals(10, req.getStartOffset());
        assertEquals(2, req.getNumberOfRegisters());
        assertEquals(10, req.getPduLength());
        ByteBuf buf = Unpooled.buffer();
        req.writePdu(buf);
        assertArrayEquals(new byte[]{0x10, 0x00, 10, 0x00, 0x02, 0x04, 0x00, 0x01, 0x00, 0x02}, toBytes(buf));
        buf.release();

        WriteRegistersRequest req2 = new WriteRegistersRequest(1, 10, 4, new byte[]{0, 1, 2, 3, 4, 5, 6, 7});
        assertEquals(4, req2.getNumberOfRegisters());
    }

    @Test
    public void testReadPduNoop() {
        WriteCoilRequest coil = new WriteCoilRequest(1, 5, true);
        coil.readPdu(Unpooled.EMPTY_BUFFER);
        WriteRegisterRequest reg = new WriteRegisterRequest(1, 5, 1);
        reg.readPdu(Unpooled.EMPTY_BUFFER);
        WriteCoilsRequest coils = new WriteCoilsRequest(1, 0, new byte[]{0x01});
        coils.readPdu(Unpooled.EMPTY_BUFFER);
        WriteRegistersRequest regs = new WriteRegistersRequest(1, 10, new byte[]{0, 1});
        regs.readPdu(Unpooled.EMPTY_BUFFER);
        ReadCoilsRequest rc = new ReadCoilsRequest(1, 0, 8);
        rc.readPdu(Unpooled.EMPTY_BUFFER);
    }

    private byte[] toBytes(ByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(0, data);
        return data;
    }
}
