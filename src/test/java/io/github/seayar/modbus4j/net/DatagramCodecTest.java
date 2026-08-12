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
 * @date 2026-08-12
 */
package io.github.seayar.modbus4j.net;

import io.github.seayar.modbus4j.codec.ModbusFrame;
import io.github.seayar.modbus4j.codec.TcpCodec;
import io.github.seayar.modbus4j.msg.ReadHoldingRegistersRequest;
import io.github.seayar.modbus4j.msg.ReadHoldingRegistersResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DatagramCodecTest {

    @Test
    public void testEncoderWrapsRequestInDatagram() {
        EmbeddedChannel channel = new EmbeddedChannel(new DatagramFrameEncoder(new TcpCodec()));
        channel.writeOutbound(new ModbusFrame(7, new ReadHoldingRegistersRequest(1, 0, 2)));
        Object out = channel.readOutbound();
        assertTrue(out instanceof DatagramPacket);
        DatagramPacket packet = (DatagramPacket) out;
        ByteBuf content = packet.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.getBytes(0, bytes);
        assertEquals(12, bytes.length);
        assertEquals(0, bytes[0]);
        assertEquals(7, bytes[1]);
        assertEquals(0, bytes[2]);
        assertEquals(0, bytes[3]);
        assertEquals(0, bytes[4]);
        assertEquals(6, bytes[5]);
        assertEquals(1, bytes[6]);
        assertEquals(0x03, bytes[7]);
        assertEquals(0, bytes[8]);
        assertEquals(0, bytes[9]);
        assertEquals(0, bytes[10]);
        assertEquals(2, bytes[11]);
        channel.finishAndReleaseAll();
    }

    @Test
    public void testDecoderUnwrapsDatagram() {
        EmbeddedChannel channel = new EmbeddedChannel(new DatagramFrameDecoder(new TcpCodec()));
        byte[] response = new byte[]{0x00, 0x07, 0x00, 0x00, 0x00, 0x05, 0x01, 0x03, 0x02, 0x00, 0x05};
        channel.writeInbound(new DatagramPacket(Unpooled.wrappedBuffer(response),
                new InetSocketAddress("127.0.0.1", 502)));
        Object frame = channel.readInbound();
        assertTrue(frame instanceof ModbusFrame);
        assertEquals(7, ((ModbusFrame) frame).getTransactionId());
        assertTrue(((ModbusFrame) frame).getMessage() instanceof ReadHoldingRegistersResponse);
        assertNull(channel.readInbound());
        channel.finishAndReleaseAll();
    }

    @Test
    public void testDecoderDropsGarbageDatagram() {
        EmbeddedChannel channel = new EmbeddedChannel(new DatagramFrameDecoder(new TcpCodec()));
        channel.writeInbound(new DatagramPacket(Unpooled.wrappedBuffer(new byte[]{(byte) 0xff, 0x00}),
                new InetSocketAddress("127.0.0.1", 502)));
        assertNull(channel.readInbound());
        channel.finishAndReleaseAll();
    }
}
