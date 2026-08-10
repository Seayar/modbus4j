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
import static org.junit.Assert.assertTrue;

public class ReadRequestsTest {

    @Test
    public void testReadCoilsRequest() {
        ReadCoilsRequest req = new ReadCoilsRequest(1, 100, 16);
        assertEquals(1, req.getSlaveId());
        assertEquals(0x01, req.getFunctionCode());
        assertEquals(100, req.getStartOffset());
        assertEquals(16, req.getNumberOfRegisters());
        assertEquals(5, req.getPduLength());
        ByteBuf buf = Unpooled.buffer();
        req.writePdu(buf);
        assertArrayEquals(new byte[]{0x01, 0x00, 100, 0x00, 16}, toByteArray(buf));
        buf.release();
    }

    @Test
    public void testReadDiscreteInputsRequest() {
        ReadDiscreteInputsRequest req = new ReadDiscreteInputsRequest(2, 0, 8);
        assertEquals(0x02, req.getFunctionCode());
        assertEquals(0, req.getStartOffset());
        assertEquals(8, req.getNumberOfRegisters());
        assertTrue(req.createResponse(Unpooled.wrappedBuffer(new byte[]{0x02, 0x01, 0x00}))
                instanceof ReadDiscreteInputsResponse);
    }

    @Test
    public void testReadHoldingRegistersRequest() {
        ReadHoldingRegistersRequest req = new ReadHoldingRegistersRequest(1, 0, 125);
        assertEquals(0x03, req.getFunctionCode());
        assertEquals(125, req.getNumberOfRegisters());
        assertTrue(req.createResponse(Unpooled.wrappedBuffer(new byte[]{0x03, 0x02, 0x00, 0x05}))
                instanceof ReadHoldingRegistersResponse);
    }

    @Test
    public void testReadInputRegistersRequest() {
        ReadInputRegistersRequest req = new ReadInputRegistersRequest(1, 10, 2);
        assertEquals(0x04, req.getFunctionCode());
        assertEquals(10, req.getStartOffset());
        assertEquals(2, req.getNumberOfRegisters());
        assertTrue(req.createResponse(Unpooled.wrappedBuffer(new byte[]{0x04, 0x02, 0x00, 0x05}))
                instanceof ReadInputRegistersResponse);
    }

    private byte[] toByteArray(ByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(0, data);
        return data;
    }
}
