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
package io.github.seayar.modbus4j.codec;

import io.github.seayar.modbus4j.msg.AbstractModbusMessage;
import io.github.seayar.modbus4j.msg.ExceptionResponse;
import io.github.seayar.modbus4j.msg.ReadHoldingRegistersRequest;
import io.github.seayar.modbus4j.msg.ReadHoldingRegistersResponse;
import io.github.seayar.modbus4j.msg.WriteRegisterRequest;
import io.github.seayar.modbus4j.msg.WriteRegisterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TcpCodecTest {

    @Test
    public void testEncode() {
        TcpCodec codec = new TcpCodec();
        ReadHoldingRegistersRequest req = new ReadHoldingRegistersRequest(1, 0, 2);
        ByteBuf buf = codec.encode(req, 5);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(0, bytes);
        assertArrayEquals(new byte[]{0x00, 0x05, 0x00, 0x00, 0x00, 0x06, 0x01, 0x03, 0x00, 0x00, 0x00, 0x02},
                bytes);
        buf.release();
    }

    @Test
    public void testDecode() {
        TcpCodec codec = new TcpCodec();
        byte[] frame = new byte[]{0x00, 0x05, 0x00, 0x00, 0x00, 0x05, 0x01, 0x03, 0x02, 0x00, 0x05};
        ByteBuf in = Unpooled.wrappedBuffer(frame);
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertEquals(5, decoded.getTransactionId());
        assertTrue(decoded.getMessage() instanceof ReadHoldingRegistersResponse);
        ReadHoldingRegistersResponse resp = (ReadHoldingRegistersResponse) decoded.getMessage();
        assertEquals(1, resp.getSlaveId());
        assertArrayEquals(new byte[]{0x00, 0x05}, resp.getData());
        assertEquals(0, in.readableBytes());
    }

    @Test
    public void testDecodeIncomplete() {
        TcpCodec codec = new TcpCodec();
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{0x00, 0x05, 0x00});
        assertNull(codec.decode(in));
        assertEquals(3, in.readableBytes());
    }

    @Test
    public void testDecodePartialPdu() {
        TcpCodec codec = new TcpCodec();
        byte[] frame = new byte[]{0x00, 0x05, 0x00, 0x00, 0x00, 0x06, 0x01, 0x03, 0x02, 0x00};
        ByteBuf in = Unpooled.wrappedBuffer(frame);
        assertNull(codec.decode(in));
        assertEquals(10, in.readableBytes());
    }

    @Test
    public void testDecodeException() {
        TcpCodec codec = new TcpCodec();
        byte[] frame = new byte[]{0x00, 0x06, 0x00, 0x00, 0x00, 0x03, 0x01, (byte) 0x83, 0x02};
        ByteBuf in = Unpooled.wrappedBuffer(frame);
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof ExceptionResponse);
        assertEquals(2, ((ExceptionResponse) decoded.getMessage()).getExceptionCode());
    }

    @Test
    public void testDecodeWriteResponse() {
        TcpCodec codec = new TcpCodec();
        byte[] frame = new byte[]{0x00, 0x07, 0x00, 0x00, 0x00, 0x06, 0x01, 0x06, 0x00, 0x0a, 0x12, 0x34};
        ByteBuf in = Unpooled.wrappedBuffer(frame);
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof WriteRegisterResponse);
        WriteRegisterResponse resp = (WriteRegisterResponse) decoded.getMessage();
        assertEquals(10, resp.getOffset());
        assertEquals(0x1234, resp.getValue());
    }

    @Test
    public void testWriteEncode() {
        TcpCodec codec = new TcpCodec();
        WriteRegisterRequest req = new WriteRegisterRequest(1, 10, 0x1234);
        ByteBuf buf = codec.encode(req, 7);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(0, bytes);
        assertArrayEquals(new byte[]{0x00, 0x07, 0x00, 0x00, 0x00, 0x06, 0x01, 0x06, 0x00, 0x0a, 0x12, 0x34},
                bytes);
        buf.release();
    }
}
