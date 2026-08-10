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

import com.seayar.modbus4j.codec.ModbusCodecType;
import com.seayar.modbus4j.concurrent.AdaptiveConcurrency;
import com.seayar.modbus4j.exception.ModbusInitException;
import com.seayar.modbus4j.exception.ModbusTransportException;
import com.seayar.modbus4j.ip.IpParameters;
import com.seayar.modbus4j.msg.AbstractModbusResponse;
import com.seayar.modbus4j.msg.ReadHoldingRegistersRequest;
import com.seayar.modbus4j.msg.ReadHoldingRegistersResponse;
import com.seayar.modbus4j.msg.WriteRegisterRequest;
import com.seayar.modbus4j.msg.WriteRegisterResponse;
import com.seayar.modbus4j.net.ModbusChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NettyTransportTest {
    private EventLoopGroup serverGroup;
    private Channel serverChannel;
    private int port;

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
}
