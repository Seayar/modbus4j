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
package com.seayar.modbus4j.codec;

import com.seayar.modbus4j.msg.ExceptionResponse;
import com.seayar.modbus4j.msg.ReadHoldingRegistersRequest;
import com.seayar.modbus4j.msg.ReadHoldingRegistersResponse;
import com.seayar.modbus4j.msg.WriteRegisterRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RtuCodecTest {

    @Test
    public void testEncode() {
        RtuCodec codec = new RtuCodec();
        ReadHoldingRegistersRequest req = new ReadHoldingRegistersRequest(1, 0, 2);
        ByteBuf buf = codec.encode(req, -1);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(0, bytes);
        assertArrayEquals(new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x02, (byte) 0xc4, (byte) 0x0b}, bytes);
        buf.release();
    }

    @Test
    public void testEncodeWriteRegister() {
        RtuCodec codec = new RtuCodec();
        WriteRegisterRequest req = new WriteRegisterRequest(1, 10, 0x1234);
        ByteBuf buf = codec.encode(req, -1);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(0, bytes);
        byte[] expected = new byte[]{0x01, 0x06, 0x00, 0x0a, 0x12, 0x34, 0, 0};
        expected[6] = (byte) (0xffff & RtuCrcUtil.computeRtuCrc(new byte[]{0x01, 0x06, 0x00, 0x0a, 0x12, 0x34}));
        expected[7] = (byte) ((0xffff & RtuCrcUtil.computeRtuCrc(new byte[]{0x01, 0x06, 0x00, 0x0a, 0x12, 0x34})) >> 8);
        assertArrayEquals(expected, bytes);
        buf.release();
    }

    @Test
    public void testDecode() {
        RtuCodec codec = new RtuCodec();
        byte[] frame = new byte[]{0x01, 0x03, 0x02, 0x00, 0x05};
        byte[] crcData = new byte[]{0x01, 0x03, 0x02, 0x00, 0x05};
        int crc = RtuCrcUtil.computeRtuCrc(crcData);
        ByteBuf in = Unpooled.buffer();
        in.writeBytes(frame);
        in.writeByte(crc & 0xff);
        in.writeByte((crc >> 8) & 0xff);
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertEquals(-1, decoded.getTransactionId());
        assertTrue(decoded.getMessage() instanceof ReadHoldingRegistersResponse);
        ReadHoldingRegistersResponse resp = (ReadHoldingRegistersResponse) decoded.getMessage();
        assertArrayEquals(new byte[]{0x00, 0x05}, resp.getData());
        assertEquals(0, in.readableBytes());
    }

    @Test
    public void testDecodeIncomplete() {
        RtuCodec codec = new RtuCodec();
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{0x01, 0x03, 0x02});
        assertNull(codec.decode(in));
    }

    @Test
    public void testDecodeBadCrc() {
        RtuCodec codec = new RtuCodec();
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{0x01, 0x03, 0x02, 0x00, 0x05, 0x00, 0x00});
        assertNull(codec.decode(in));
    }

    @Test
    public void testDecodeException() {
        RtuCodec codec = new RtuCodec();
        byte[] payload = new byte[]{0x01, (byte) 0x83, 0x02};
        int crc = RtuCrcUtil.computeRtuCrc(payload);
        ByteBuf in = Unpooled.buffer();
        in.writeBytes(payload);
        in.writeByte(crc & 0xff);
        in.writeByte((crc >> 8) & 0xff);
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof ExceptionResponse);
        assertEquals(2, ((ExceptionResponse) decoded.getMessage()).getExceptionCode());
    }

    @Test
    public void testDecodeWriteCoil() {
        RtuCodec codec = new RtuCodec();
        byte[] payload = new byte[]{0x01, 0x05, 0x00, 0x05, (byte) 0xff, 0x00};
        int crc = RtuCrcUtil.computeRtuCrc(payload);
        ByteBuf in = Unpooled.buffer();
        in.writeBytes(payload);
        in.writeByte(crc & 0xff);
        in.writeByte((crc >> 8) & 0xff);
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof com.seayar.modbus4j.msg.WriteCoilResponse);
    }

    @Test
    public void testDecodeWriteCoils() {
        RtuCodec codec = new RtuCodec();
        byte[] payload = new byte[]{0x01, 0x0f, 0x00, 0x05, 0x00, 0x08};
        int crc = RtuCrcUtil.computeRtuCrc(payload);
        ByteBuf in = Unpooled.buffer();
        in.writeBytes(payload);
        in.writeByte(crc & 0xff);
        in.writeByte((crc >> 8) & 0xff);
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof com.seayar.modbus4j.msg.WriteCoilsResponse);
    }

    @Test
    public void testDecodeCoils() {
        RtuCodec codec = new RtuCodec();
        byte[] payload = new byte[]{0x01, 0x01, 0x01, 0x01};
        int crc = RtuCrcUtil.computeRtuCrc(payload);
        ByteBuf in = Unpooled.buffer();
        in.writeBytes(payload);
        in.writeByte(crc & 0xff);
        in.writeByte((crc >> 8) & 0xff);
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof com.seayar.modbus4j.msg.ReadCoilsResponse);
    }

    static final class RtuCrcUtil {
        private RtuCrcUtil() {}

        static int computeRtuCrc(byte[] data) {
            return com.seayar.modbus4j.util.RtuCrcUtil.calculateCRC(data);
        }
    }
}