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
package io.github.seayar.modbus4j;

import io.github.seayar.modbus4j.ip.IpParameters;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ModbusMasterTestSupport {
    private static EventLoopGroup serverGroup;
    private static Channel serverChannel;
    private static int port;

    private ModbusMasterTestSupport() {}

    public static synchronized void ensureServer() {
        if (serverChannel != null)
            return;
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
                                ctx.writeAndFlush(Unpooled.wrappedBuffer(respond(data)));
                            }
                        });
                    }
                });
        try {
            serverChannel = bootstrap.bind(0).sync().channel();
            port = ((java.net.InetSocketAddress) serverChannel.localAddress()).getPort();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(ModbusMasterTestSupport::stopServer));
    }

    private static void stopServer() {
        if (serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
            serverChannel = null;
        }
        if (serverGroup != null) {
            serverGroup.shutdownGracefully().syncUninterruptibly();
            serverGroup = null;
        }
    }

    private static byte[] respond(byte[] data) {
        if (data[6] == (byte) 0x7f) {
            ByteBuf err = Unpooled.buffer();
            err.writeByte(data[0]);
            err.writeByte(data[1]);
            err.writeShort(0);
            err.writeShort(3);
            err.writeByte(data[6]);
            err.writeByte(0x80 | data[7]);
            err.writeByte(0x02);
            byte[] out = new byte[err.readableBytes()];
            err.getBytes(0, out);
            err.release();
            return out;
        }
        int fc = data[7] & 0xff;
        int start = ((data[8] & 0xff) << 8) | (data[9] & 0xff);
        int quantity = ((data[10] & 0xff) << 8) | (data[11] & 0xff);
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(data[0]);
        buf.writeByte(data[1]);
        buf.writeShort(0);
        if (fc == 0x03 || fc == 0x04) {
            byte[] payload = new byte[quantity * 2];
            for (int i = 0; i < quantity; i++) {
                payload[i * 2] = (byte) ((start + i + 1) >> 8);
                payload[i * 2 + 1] = (byte) (start + i + 1);
            }
            buf.writeShort(payload.length + 3);
            buf.writeByte(data[6]);
            buf.writeByte(fc);
            buf.writeByte(payload.length);
            buf.writeBytes(payload);
        } else if (fc == 0x01 || fc == 0x02) {
            int byteCount = (quantity + 7) / 8;
            buf.writeShort(byteCount + 3);
            buf.writeByte(data[6]);
            buf.writeByte(fc);
            buf.writeByte(byteCount);
            for (int i = 0; i < byteCount; i++)
                buf.writeByte(0x01);
        } else if (fc == 0x06 || fc == 0x10 || fc == 0x05 || fc == 0x0f) {
            buf.writeShort(6);
            buf.writeByte(data[6]);
            buf.writeByte(fc);
            buf.writeBytes(data, 8, 4);
        } else {
            buf.writeShort(3);
            buf.writeByte(data[6]);
            buf.writeByte(0x80 | fc);
            buf.writeByte(0x02);
        }
        byte[] out = new byte[buf.readableBytes()];
        buf.getBytes(0, out);
        buf.release();
        return out;
    }

    public static ModbusMaster createMaster() {
        ensureServer();
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(port);
        params.setReadTimeoutMillis(5000);
        return new ModbusFactory().createTcpMaster(params, true);
    }

    public static ModbusMaster createUnreachableMaster() {
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(1);
        params.setConnectTimeoutMillis(100);
        params.setReadTimeoutMillis(100);
        return new ModbusFactory().createTcpMaster(params, true);
    }
}
