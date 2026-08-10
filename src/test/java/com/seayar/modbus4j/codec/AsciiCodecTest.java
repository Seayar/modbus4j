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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AsciiCodecTest {

    @Test
    public void testEncode() {
        AsciiCodec codec = new AsciiCodec();
        ReadHoldingRegistersRequest req = new ReadHoldingRegistersRequest(1, 0, 2);
        ByteBuf buf = codec.encode(req, -1);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(0, bytes);
        String frame = new String(bytes);
        assertTrue(frame.startsWith(":"));
        assertTrue(frame.endsWith("\r\n"));
        String hex = frame.substring(1, frame.length() - 2);
        byte[] payload = com.seayar.modbus4j.util.HexUtil.hexStringToByte(hex);
        assertPrefix(new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x02, (byte) 0xfa}, payload);
        buf.release();
    }

    private void assertPrefix(byte[] expected, byte[] actual) {
        for (int i = 0; i < expected.length; i++)
            assertEquals(expected[i], actual[i]);
    }

    @Test
    public void testDecode() {
        AsciiCodec codec = new AsciiCodec();
        String frame = ":" + "0103020005" + "F5" + "\r\n";
        ByteBuf in = Unpooled.wrappedBuffer(frame.getBytes());
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof ReadHoldingRegistersResponse);
        ReadHoldingRegistersResponse resp = (ReadHoldingRegistersResponse) decoded.getMessage();
        assertEquals(1, resp.getSlaveId());
        assertArrayEquals(new byte[]{0x00, 0x05}, resp.getData());
        assertEquals(0, in.readableBytes());
    }

    @Test
    public void testDecodeIncomplete() {
        AsciiCodec codec = new AsciiCodec();
        ByteBuf in = Unpooled.wrappedBuffer(":0103".getBytes());
        assertNull(codec.decode(in));
    }

    @Test
    public void testDecodeBadStart() {
        AsciiCodec codec = new AsciiCodec();
        ByteBuf in = Unpooled.wrappedBuffer("0103020005F8\r\n".getBytes());
        assertNull(codec.decode(in));
    }

    @Test
    public void testDecodeBadLrc() {
        AsciiCodec codec = new AsciiCodec();
        String frame = ":" + "0103020005" + "00" + "\r\n";
        ByteBuf in = Unpooled.wrappedBuffer(frame.getBytes());
        assertNull(codec.decode(in));
    }

    @Test
    public void testDecodeException() {
        AsciiCodec codec = new AsciiCodec();
        byte[] payload = new byte[]{0x01, (byte) 0x83, 0x02};
        byte lrc = com.seayar.modbus4j.util.AsciiLrcUtil.calculateLRC(payload);
        String hex = com.seayar.modbus4j.util.HexUtil.bytesToHexString(payload, "");
        String frame = ":" + hex + com.seayar.modbus4j.util.HexUtil.byte2Hex(lrc) + "\r\n";
        ByteBuf in = Unpooled.wrappedBuffer(frame.getBytes());
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof ExceptionResponse);
        assertEquals(2, ((ExceptionResponse) decoded.getMessage()).getExceptionCode());
    }

    @Test
    public void testDecodeWriteResponse() {
        AsciiCodec codec = new AsciiCodec();
        byte[] payload = new byte[]{0x01, 0x06, 0x00, 0x0a, 0x12, 0x34};
        byte lrc = com.seayar.modbus4j.util.AsciiLrcUtil.calculateLRC(payload);
        String hex = com.seayar.modbus4j.util.HexUtil.bytesToHexString(payload, "");
        String frame = ":" + hex + com.seayar.modbus4j.util.HexUtil.byte2Hex(lrc) + "\r\n";
        ByteBuf in = Unpooled.wrappedBuffer(frame.getBytes());
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof com.seayar.modbus4j.msg.WriteRegisterResponse);
    }

    @Test
    public void testDecodeMultipleWriteResponse() {
        AsciiCodec codec = new AsciiCodec();
        byte[] payload = new byte[]{0x01, 0x10, 0x00, 0x0a, 0x00, 0x02};
        byte lrc = com.seayar.modbus4j.util.AsciiLrcUtil.calculateLRC(payload);
        String hex = com.seayar.modbus4j.util.HexUtil.bytesToHexString(payload, "");
        String frame = ":" + hex + com.seayar.modbus4j.util.HexUtil.byte2Hex(lrc) + "\r\n";
        ByteBuf in = Unpooled.wrappedBuffer(frame.getBytes());
        ModbusFrame decoded = codec.decode(in);
        assertNotNull(decoded);
        assertTrue(decoded.getMessage() instanceof com.seayar.modbus4j.msg.WriteRegistersResponse);
    }
}
