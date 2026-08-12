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
package io.github.seayar.modbus4j.transport;

import io.github.seayar.modbus4j.codec.ModbusCodecType;
import io.github.seayar.modbus4j.concurrent.AdaptiveConcurrency;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.msg.AbstractModbusResponse;
import io.github.seayar.modbus4j.msg.ReadHoldingRegistersRequest;
import io.github.seayar.modbus4j.msg.ReadHoldingRegistersResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.junit.After;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UdpTransportTest {

    private interface UdpResponder {
        byte[] respond(byte[] request);
    }

    private static final class ServerHandle {
        final EventLoopGroup group;
        final Channel channel;

        ServerHandle(EventLoopGroup group, Channel channel) {
            this.group = group;
            this.channel = channel;
        }
    }

    private final List<ServerHandle> servers = new ArrayList<>();

    private int startServer(UdpResponder responder) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
                        byte[] req = new byte[packet.content().readableBytes()];
                        packet.content().getBytes(0, req);
                        byte[] resp = responder.respond(req);
                        ctx.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(resp), packet.sender()));
                    }
                });
        Channel server = bootstrap.bind(0).sync().channel();
        servers.add(new ServerHandle(group, server));
        return ((InetSocketAddress) server.localAddress()).getPort();
    }

    @After
    public void stopServers() {
        for (ServerHandle handle : servers) {
            handle.channel.close().syncUninterruptibly();
            handle.group.shutdownGracefully().syncUninterruptibly();
        }
        servers.clear();
    }

    private IpParameters params(int port) {
        IpParameters parameters = new IpParameters();
        parameters.setHost("127.0.0.1");
        parameters.setPort(port);
        parameters.setReadTimeoutMillis(3000);
        return parameters;
    }

    private static byte[] mbapResponse(byte[] req) {
        int tx = ((req[0] & 0xff) << 8) | (req[1] & 0xff);
        int unit = req[6] & 0xff;
        int fc = req[7] & 0xff;
        byte[] pdu = fc == 0x03
                ? new byte[]{0x03, 0x02, 0x00, 0x05}
                : new byte[]{0x06, 0x00, 0x0a, 0x12, 0x34};
        byte[] out = new byte[7 + pdu.length];
        out[0] = (byte) (tx >> 8);
        out[1] = (byte) tx;
        out[4] = (byte) ((1 + pdu.length) >> 8);
        out[5] = (byte) (1 + pdu.length);
        out[6] = (byte) unit;
        System.arraycopy(pdu, 0, out, 7, pdu.length);
        return out;
    }

    private static byte[] rtuResponse(byte[] req) {
        byte[] pdu = new byte[]{0x01, 0x03, 0x02, 0x00, 0x05};
        int crc = io.github.seayar.modbus4j.util.RtuCrcUtil.calculateCRC(pdu);
        byte[] out = new byte[]{pdu[0], pdu[1], pdu[2], pdu[3], pdu[4], (byte) (crc & 0xff),
                (byte) ((crc >> 8) & 0xff)};
        return out;
    }

    private static byte[] asciiResponse(byte[] req) {
        byte[] pdu = new byte[]{0x01, 0x03, 0x02, 0x00, 0x05};
        byte lrc = io.github.seayar.modbus4j.util.AsciiLrcUtil.calculateLRC(pdu);
        String hex = io.github.seayar.modbus4j.util.HexUtil.bytesToHexString(pdu, "");
        return (":" + hex + io.github.seayar.modbus4j.util.HexUtil.byte2Hex(lrc) + "\r\n")
                .getBytes(StandardCharsets.US_ASCII);
    }

    @Test(timeout = 15000)
    public void testUdpMasterRead() throws Exception {
        int port = startServer(UdpTransportTest::mbapResponse);
        UdpTransport transport = new UdpTransport(params(port), ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
        assertTrue(transport.isInitialized());
        AbstractModbusResponse resp = transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
        assertEquals(2, ((ReadHoldingRegistersResponse) resp).getByteCount());
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testUdpMasterSendAsync() throws Exception {
        int port = startServer(UdpTransportTest::mbapResponse);
        UdpTransport transport = new UdpTransport(params(port), ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
        Future<AbstractModbusResponse> future = transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
        AbstractModbusResponse resp = future.get(3, TimeUnit.SECONDS);
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testRtuUdpRead() throws Exception {
        int port = startServer(UdpTransportTest::rtuResponse);
        UdpTransport transport = new UdpTransport(params(port), ModbusCodecType.RTU, true, null);
        transport.init();
        AbstractModbusResponse resp = transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
        assertEquals(1, transport.getMaxInFlight());
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testAsciiUdpRead() throws Exception {
        int port = startServer(UdpTransportTest::asciiResponse);
        UdpTransport transport = new UdpTransport(params(port), ModbusCodecType.ASCII, true, null);
        transport.init();
        AbstractModbusResponse resp = transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testUdpTimeout() throws Exception {
        int port = startServer(req -> new byte[0]);
        IpParameters parameters = params(port);
        parameters.setReadTimeoutMillis(300);
        UdpTransport transport = new UdpTransport(parameters, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
        Future<AbstractModbusResponse> future = transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
        try {
            future.get(5, TimeUnit.SECONDS);
            fail("expected timeout");
        } catch (java.util.concurrent.ExecutionException expected) {
            assertTrue(expected.getCause() instanceof java.util.concurrent.TimeoutException);
        }
        assertEquals(0, transport.getInFlight());
        transport.destroy();
    }

    @Test(timeout = 15000, expected = io.github.seayar.modbus4j.exception.ModbusTransportException.class)
    public void testUdpSendNotInitialized() throws Exception {
        IpParameters parameters = new IpParameters();
        parameters.setHost("127.0.0.1");
        parameters.setPort(1);
        UdpTransport transport = new UdpTransport(parameters, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
    }

    @Test(timeout = 15000)
    public void testUdpConcurrencyThrottle() throws Exception {
        int port = startServer(UdpTransportTest::mbapResponse);
        UdpTransport transport = new UdpTransport(params(port), ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
        assertEquals(1, transport.getMaxInFlight());
        transport.setMaxInFlight(2);
        assertEquals(2, transport.getMaxInFlight());
        Future<AbstractModbusResponse> f1 = transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
        Future<AbstractModbusResponse> f2 = transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
        assertEquals(2, transport.getInFlight());
        f1.get(3, TimeUnit.SECONDS);
        f2.get(3, TimeUnit.SECONDS);
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testUdpConcurrencySlotTimeout() throws Exception {
        int port = startServer(req -> new byte[0]);
        IpParameters parameters = params(port);
        parameters.setReadTimeoutMillis(200);
        UdpTransport transport = new UdpTransport(parameters, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
        Future<AbstractModbusResponse> f1 = transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
        try {
            transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
            fail("expected concurrency slot timeout");
        } catch (io.github.seayar.modbus4j.exception.ModbusTransportException expected) {
        }
        try {
            f1.get(5, TimeUnit.SECONDS);
            fail("expected f1 timeout");
        } catch (java.util.concurrent.ExecutionException expected) {
            assertTrue(expected.getCause() instanceof java.util.concurrent.TimeoutException);
        }
        transport.destroy();
    }

    @Test
    public void testDatagramByteBufRefCount() {
        ByteBuf content = Unpooled.buffer();
        content.writeByte(1);
        DatagramPacket packet = new DatagramPacket(content,
                new InetSocketAddress("127.0.0.1", 502));
        assertEquals(1, packet.content().refCnt());
        packet.release();
    }
}
