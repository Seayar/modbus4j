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
package com.seayar.modbus4j.net;

import com.seayar.modbus4j.codec.ModbusFrame;
import com.seayar.modbus4j.codec.TcpCodec;
import com.seayar.modbus4j.concurrent.PendingRequest;
import com.seayar.modbus4j.concurrent.PendingRequests;
import com.seayar.modbus4j.msg.AbstractModbusMessage;
import com.seayar.modbus4j.msg.ReadHoldingRegistersRequest;
import com.seayar.modbus4j.msg.ReadHoldingRegistersResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetPackageTest {

    @Test
    public void testDecoder() {
        TcpCodec codec = new TcpCodec();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusFrameDecoder(codec));
        byte[] frame = new byte[]{0x00, 0x05, 0x00, 0x00, 0x00, 0x05, 0x01, 0x03, 0x02, 0x00, 0x05};
        channel.writeInbound(Unpooled.wrappedBuffer(frame));
        ModbusFrame decoded = channel.readInbound();
        assertEquals(5, decoded.getTransactionId());
        assertTrue(decoded.getMessage() instanceof ReadHoldingRegistersResponse);
        channel.finish();
    }

    @Test
    public void testDecoderIncomplete() {
        TcpCodec codec = new TcpCodec();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusFrameDecoder(codec));
        channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{0x00, 0x05, 0x00}));
        Object decoded = channel.readInbound();
        assertTrue(decoded == null);
        channel.finish();
    }

    @Test
    public void testEncoder() {
        TcpCodec codec = new TcpCodec();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusFrameEncoder(codec));
        ReadHoldingRegistersRequest req = new ReadHoldingRegistersRequest(1, 0, 2);
        assertTrue(channel.writeOutbound(new ModbusFrame(5, req)));
        ByteBuf out = channel.readOutbound();
        byte[] bytes = new byte[out.readableBytes()];
        out.getBytes(0, bytes);
        assertArrayEquals(new byte[]{0x00, 0x05, 0x00, 0x00, 0x00, 0x06, 0x01, 0x03, 0x00, 0x00, 0x00, 0x02},
                bytes);
        out.release();
        channel.finish();
    }

    @Test
    public void testResponseHandlerMatches() {
        PendingRequests pending = new PendingRequests();
        CompletableFuture<Object> future = pending.putAndGetFuture(5, 1000);
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending));
        ReadHoldingRegistersResponse resp = new ReadHoldingRegistersResponse(1, 2, new byte[]{0, 5});
        channel.writeInbound(new ModbusFrame(5, resp));
        assertEquals(resp, future.join());
        channel.finish();
    }

    @Test
    public void testResponseHandlerFifo() {
        PendingRequests pending = new PendingRequests();
        CompletableFuture<Object> future = pending.putAndGetFuture(-1, 1000);
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending));
        ReadHoldingRegistersResponse resp = new ReadHoldingRegistersResponse(1, 2, new byte[]{0, 5});
        channel.writeInbound(new ModbusFrame(-1, resp));
        assertEquals(resp, future.join());
        channel.finish();
    }

    @Test
    public void testResponseHandlerException() {
        PendingRequests pending = new PendingRequests();
        CompletableFuture<Object> future = pending.putAndGetFuture(5, 1000);
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending));
        channel.pipeline().fireExceptionCaught(new RuntimeException("boom"));
        assertTrue(future.isCompletedExceptionally());
        channel.finish();
    }

    @Test
    public void testResponseHandlerChannelInactive() {
        PendingRequests pending = new PendingRequests();
        CompletableFuture<Object> future = pending.putAndGetFuture(5, 1000);
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending));
        channel.close();
        assertTrue(future.isCompletedExceptionally());
        channel.finish();
    }

    @Test
    public void testChannelInitializer() {
        PendingRequests pending = new PendingRequests();
        ModbusChannelInitializer initializer = new ModbusChannelInitializer(new TcpCodec(), pending, 5000);
        EmbeddedChannel channel = new EmbeddedChannel(initializer);
        assertTrue(channel.pipeline().get("frameDecoder") != null);
        assertTrue(channel.pipeline().get("frameEncoder") != null);
        assertTrue(channel.pipeline().get("idle") != null);
        assertTrue(channel.pipeline().get("responseHandler") != null);
        channel.finish();
    }
}
