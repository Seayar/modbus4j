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
package com.seayar.modbus4j.transport;

import com.seayar.modbus4j.codec.ModbusCodec;
import com.seayar.modbus4j.codec.ModbusCodecType;
import com.seayar.modbus4j.codec.TcpCodec;
import com.seayar.modbus4j.concurrent.AdaptiveConcurrency;
import com.seayar.modbus4j.exception.ModbusInitException;
import com.seayar.modbus4j.exception.ModbusTransportException;
import com.seayar.modbus4j.ip.IpParameters;
import com.seayar.modbus4j.msg.AbstractModbusResponse;
import com.seayar.modbus4j.msg.ReadHoldingRegistersRequest;
import com.seayar.modbus4j.msg.ReadHoldingRegistersResponse;
import com.seayar.modbus4j.msg.WriteRegisterRequest;
import com.seayar.modbus4j.msg.WriteRegisterResponse;
import com.seayar.modbus4j.net.ChannelPipelineCustomizer;
import com.seayar.modbus4j.net.ModbusChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NettyTransportTest {
    private EventLoopGroup serverGroup;
    private Channel serverChannel;
    private int port;
    private volatile Channel acceptedChannel;
    private EventLoopGroup silentServerGroup;
    private Channel silentServerChannel;

    @Before
    public void startServer() throws Exception {
        serverGroup = new NioEventLoopGroup(1);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(serverGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        acceptedChannel = ch;
                        ch.pipeline().addLast(new io.netty.handler.codec.LengthFieldBasedFrameDecoder(4096, 4, 2, 0, 0));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                byte[] data = new byte[msg.readableBytes()];
                                msg.getBytes(0, data);
                                byte[] response;
                                if (data[7] == 0x03) {
                                    response = new byte[]{data[0], data[1], 0x00, 0x00, 0x00, 0x05, data[6], 0x03,
                                            0x02, 0x00, 0x05};
                                } else {
                                    response = new byte[]{data[0], data[1], 0x00, 0x00, 0x00, 0x06, data[6], 0x06,
                                            0x00, 0x0a, 0x12, 0x34};
                                }
                                ctx.writeAndFlush(io.netty.buffer.Unpooled.wrappedBuffer(response));
                            }
                        });
                    }
                });
        serverChannel = bootstrap.bind(0).sync().channel();
        port = ((java.net.InetSocketAddress) serverChannel.localAddress()).getPort();
    }

    @After
    public void stopServer() {
        if (silentServerChannel != null) {
            silentServerChannel.close().syncUninterruptibly();
            silentServerChannel = null;
        }
        if (silentServerGroup != null) {
            silentServerGroup.shutdownGracefully().syncUninterruptibly();
            silentServerGroup = null;
        }
        serverChannel.close().syncUninterruptibly();
        serverGroup.shutdownGracefully().syncUninterruptibly();
    }

    private NettyTransport createTransport() {
        IpParameters params = new IpParameters();
        params.setPort(port);
        params.setHost("127.0.0.1");
        params.setReadTimeoutMillis(5000);
        AdaptiveConcurrency ac = new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1);
        return new NettyTransport(params, ModbusCodecType.TCP, false, ac);
    }

    @Test(timeout = 15000)
    public void testSend() throws Exception {
        NettyTransport transport = createTransport();
        transport.init();
        assertTrue(transport.isInitialized());
        AbstractModbusResponse resp = transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
        ReadHoldingRegistersResponse read = (ReadHoldingRegistersResponse) resp;
        assertEquals(2, read.getByteCount());
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testSendAsync() throws Exception {
        NettyTransport transport = createTransport();
        transport.init();
        Future<AbstractModbusResponse> future = transport.sendAsync(new WriteRegisterRequest(1, 10, 0x1234));
        assertEquals(1, transport.getInFlight());
        AbstractModbusResponse resp = future.get();
        assertTrue(resp instanceof WriteRegisterResponse);
        WriteRegisterResponse write = (WriteRegisterResponse) resp;
        assertEquals(0x1234, write.getValue());
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testGetMaxInFlight() throws Exception {
        NettyTransport transport = createTransport();
        transport.init();
        assertEquals(1, transport.getMaxInFlight());
        transport.setMaxInFlight(5);
        transport.destroy();
    }

    @Test(timeout = 15000, expected = ModbusTransportException.class)
    public void testSendNotConnected() throws ModbusTransportException {
        IpParameters params = new IpParameters();
        params.setPort(port);
        params.setHost("127.0.0.1");
        NettyTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
    }

    @Test(timeout = 15000, expected = ModbusInitException.class)
    public void testInitFailure() {
        IpParameters params = new IpParameters();
        params.setPort(1);
        params.setHost("127.0.0.1");
        params.setConnectTimeoutMillis(100);
        NettyTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
    }

    @Test(timeout = 15000)
    public void testCustomCodecAndPipelineCustomizer() throws Exception {
        final AtomicBoolean customized = new AtomicBoolean();
        ChannelPipelineCustomizer customizer = pipeline -> {
            customized.set(true);
            pipeline.addLast("marker", new ChannelInboundHandlerAdapter() {
            });
        };
        ModbusCodec codec = new TcpCodec();
        IpParameters params = new IpParameters();
        params.setPort(port);
        params.setHost("127.0.0.1");
        params.setReadTimeoutMillis(5000);
        NettyTransport transport = new NettyTransport(params, codec, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1), customizer);
        transport.init();
        assertTrue(customized.get());
        AbstractModbusResponse resp = transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
        ReadHoldingRegistersResponse read = (ReadHoldingRegistersResponse) resp;
        assertEquals(2, read.getByteCount());
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testCodecTypeConstructorWithCustomizer() throws Exception {
        final AtomicBoolean customized = new AtomicBoolean();
        ChannelPipelineCustomizer customizer = pipeline -> customized.set(true);
        IpParameters params = new IpParameters();
        params.setPort(port);
        params.setHost("127.0.0.1");
        params.setReadTimeoutMillis(5000);
        NettyTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1), customizer);
        transport.init();
        assertTrue(customized.get());
        AbstractModbusResponse resp = transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
        transport.destroy();
    }

    @Test(timeout = 20000)
    public void testAutoReconnect() throws Exception {
        IpParameters params = new IpParameters();
        params.setPort(port);
        params.setHost("127.0.0.1");
        params.setReadTimeoutMillis(5000);
        params.setAutoReconnect(true);
        params.setReconnectDelayMillis(200);
        NettyTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
        assertTrue(transport.isInitialized());
        transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
        if (acceptedChannel != null)
            acceptedChannel.close().awaitUninterruptibly();
        Thread.sleep(300);
        long deadline = System.currentTimeMillis() + 6000;
        while (!transport.isInitialized() && System.currentTimeMillis() < deadline)
            Thread.sleep(50);
        assertTrue("reconnect failed", transport.isInitialized());
        AbstractModbusResponse resp = transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testMaxInFlightThrottling() throws Exception {
        NettyTransport transport = createTransport();
        transport.init();
        assertEquals(1, transport.getMaxInFlight());
        transport.setMaxInFlight(2);
        assertEquals(2, transport.getMaxInFlight());
        Future<AbstractModbusResponse> f1 = transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
        Future<AbstractModbusResponse> f2 = transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
        assertEquals(2, transport.getInFlight());
        f1.get();
        f2.get();
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testAsyncTimeout() throws Exception {
        EventLoopGroup silentGroup = new NioEventLoopGroup(1);
        ServerBootstrap silent = new ServerBootstrap();
        silent.group(silentGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        });
                    }
                });
        Channel silentChannel = silent.bind(0).sync().channel();
        int silentPort = ((java.net.InetSocketAddress) silentChannel.localAddress()).getPort();
        IpParameters params = new IpParameters();
        params.setPort(silentPort);
        params.setHost("127.0.0.1");
        params.setReadTimeoutMillis(300);
        NettyTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
        Future<AbstractModbusResponse> future = transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
        try {
            future.get(5, TimeUnit.SECONDS);
            throw new AssertionError("expected timeout");
        } catch (java.util.concurrent.ExecutionException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
        }
        assertEquals(0, transport.getInFlight());
        transport.destroy();
        silentChannel.close().syncUninterruptibly();
        silentGroup.shutdownGracefully().syncUninterruptibly();
    }

    @Test(timeout = 15000)
    public void testSendTimeout() throws Exception {
        int silentPort = startSilentServer();
        IpParameters params = new IpParameters();
        params.setPort(silentPort);
        params.setHost("127.0.0.1");
        params.setReadTimeoutMillis(200);
        NettyTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
        try {
            transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
            throw new AssertionError("expected timeout");
        } catch (ModbusTransportException expected) {
        }
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testConcurrencySlotTimeout() throws Exception {
        int silentPort = startSilentServer();
        IpParameters params = new IpParameters();
        params.setPort(silentPort);
        params.setHost("127.0.0.1");
        params.setReadTimeoutMillis(200);
        NettyTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 4, 100_000_000L, 0.1));
        transport.init();
        Future<AbstractModbusResponse> f1 = transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
        try {
            transport.sendAsync(new ReadHoldingRegistersRequest(1, 0, 2));
            throw new AssertionError("expected concurrency slot timeout");
        } catch (ModbusTransportException expected) {
        }
        try {
            f1.get(5, TimeUnit.SECONDS);
            throw new AssertionError("expected f1 timeout");
        } catch (java.util.concurrent.ExecutionException expected) {
            assertTrue(expected.getCause() instanceof TimeoutException);
        }
        transport.destroy();
    }

    @Test(timeout = 15000)
    public void testRtuSynchronousTransport() throws Exception {
        EventLoopGroup rtuGroup = new NioEventLoopGroup(1);
        ServerBootstrap rtuServer = new ServerBootstrap();
        rtuServer.group(rtuGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                byte[] response = new byte[]{0x01, 0x03, 0x02, 0x00, 0x05};
                                int crc = com.seayar.modbus4j.util.RtuCrcUtil.calculateCRC(response);
                                ctx.writeAndFlush(io.netty.buffer.Unpooled.wrappedBuffer(new byte[]{
                                        response[0], response[1], response[2], response[3], response[4],
                                        (byte) (crc & 0xff), (byte) ((crc >> 8) & 0xff)}));
                            }
                        });
                    }
                });
        Channel rtuChannel = rtuServer.bind(0).sync().channel();
        int rtuPort = ((java.net.InetSocketAddress) rtuChannel.localAddress()).getPort();
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(rtuPort);
        params.setReadTimeoutMillis(5000);
        NettyTransport transport = new NettyTransport(params, ModbusCodecType.RTU, true, null);
        transport.init();
        assertTrue(transport.isInitialized());
        AbstractModbusResponse resp = transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
        assertEquals(1, transport.getMaxInFlight());
        transport.destroy();
        rtuChannel.close().syncUninterruptibly();
        rtuGroup.shutdownGracefully().syncUninterruptibly();
    }

    @Test(timeout = 15000)
    public void testRtuRecoversAfterBadFrame() throws Exception {
        EventLoopGroup rtuGroup = new NioEventLoopGroup(1);
        ServerBootstrap rtuServer = new ServerBootstrap();
        rtuServer.group(rtuGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            private boolean first = true;

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                byte[] response = new byte[]{0x01, 0x03, 0x02, 0x00, 0x05};
                                int crc = com.seayar.modbus4j.util.RtuCrcUtil.calculateCRC(response);
                                ByteBuf out = io.netty.buffer.Unpooled.buffer();
                                out.writeBytes(response);
                                out.writeByte(crc & 0xff);
                                out.writeByte((crc >> 8) & 0xff);
                                if (first) {
                                    first = false;
                                    ByteBuf bad = out.copy();
                                    bad.setByte(bad.writerIndex() - 1, bad.getByte(bad.writerIndex() - 1) ^ 0xff);
                                    ctx.writeAndFlush(bad);
                                }
                                ctx.writeAndFlush(out);
                            }
                        });
                    }
                });
        Channel rtuChannel = rtuServer.bind(0).sync().channel();
        int rtuPort = ((java.net.InetSocketAddress) rtuChannel.localAddress()).getPort();
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(rtuPort);
        params.setReadTimeoutMillis(5000);
        NettyTransport transport = new NettyTransport(params, ModbusCodecType.RTU, true, null);
        transport.init();
        for (int i = 0; i < 5; i++) {
            AbstractModbusResponse resp = transport.send(new ReadHoldingRegistersRequest(1, 0, 2));
            assertTrue("iteration " + i, resp instanceof ReadHoldingRegistersResponse);
        }
        transport.destroy();
        rtuChannel.close().syncUninterruptibly();
        rtuGroup.shutdownGracefully().syncUninterruptibly();
    }

    private int startSilentServer() throws Exception {
        EventLoopGroup silentGroup = new NioEventLoopGroup(1);
        silentServerGroup = silentGroup;
        ServerBootstrap silent = new ServerBootstrap();
        silent.group(silentGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        });
                    }
                });
        silentServerChannel = silent.bind(0).sync().channel();
        return ((java.net.InetSocketAddress) silentServerChannel.localAddress()).getPort();
    }
}
