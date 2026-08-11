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
 * @date 2026-08-11
 */
package com.seayar.modbus4j.samples;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Arrays;

/**
 * A tiny in-memory Modbus TCP slave used by the sample programs so that no real
 * device is needed. Start it first, then run any sample against localhost:1502.
 * <p>
 * The register map is synthetic: holding/input register N holds value N+1, coils
 * are all on, and the extended function codes return fixed demo payloads.
 */
public final class EmbeddedModbusSlave {

    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    public EmbeddedModbusSlave(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(2);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new ModbusSlaveHandler());
                    }
                });
        ChannelFuture future = bootstrap.bind(port).sync();
        serverChannel = future.channel();
        System.out.println("[slave] listening on 0.0.0.0:" + port);
    }

    public void stop() {
        if (serverChannel != null)
            serverChannel.close().awaitUninterruptibly();
        if (bossGroup != null)
            bossGroup.shutdownGracefully().syncUninterruptibly();
        if (workerGroup != null)
            workerGroup.shutdownGracefully().syncUninterruptibly();
    }

    public static void main(String[] args) throws InterruptedException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 1502;
        final EmbeddedModbusSlave slave = new EmbeddedModbusSlave(port);
        slave.start();
        Runtime.getRuntime().addShutdownHook(new Thread(slave::stop));
        Thread.currentThread().join();
    }

    private static final class ModbusSlaveHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
            byte[] req = new byte[msg.readableBytes()];
            msg.getBytes(0, req);
            if (req.length < 8)
                return;
            int fc = req[7] & 0xff;
            ByteBuf out = Unpooled.buffer();
            out.writeByte(req[0]);
            out.writeByte(req[1]);
            out.writeShort(0);
            switch (fc) {
                case 0x03:
                case 0x04: {
                    int quantity = ((req[10] & 0xff) << 8) | (req[11] & 0xff);
                    byte[] payload = new byte[quantity * 2];
                    for (int i = 0; i < quantity; i++) {
                        payload[i * 2] = (byte) ((i + 1) >> 8);
                        payload[i * 2 + 1] = (byte) (i + 1);
                    }
                    out.writeShort(3 + payload.length);
                    out.writeByte(req[6]);
                    out.writeByte(fc);
                    out.writeByte(payload.length);
                    out.writeBytes(payload);
                    break;
                }
                case 0x01:
                case 0x02: {
                    int quantity = ((req[10] & 0xff) << 8) | (req[11] & 0xff);
                    int byteCount = (quantity + 7) / 8;
                    out.writeShort(3 + byteCount);
                    out.writeByte(req[6]);
                    out.writeByte(fc);
                    out.writeByte(byteCount);
                    for (int i = 0; i < byteCount; i++)
                        out.writeByte(0xff);
                    break;
                }
                case 0x05:
                case 0x06:
                case 0x0f:
                case 0x10: {
                    out.writeShort(6);
                    out.writeByte(req[6]);
                    out.writeByte(fc);
                    out.writeBytes(req, 8, 4);
                    break;
                }
                case 0x07:
                    out.writeShort(3);
                    out.writeByte(req[6]);
                    out.writeByte(0x07);
                    out.writeByte(0x05);
                    break;
                case 0x11:
                    out.writeShort(5);
                    out.writeByte(req[6]);
                    out.writeByte(0x11);
                    out.writeByte(0x02);
                    out.writeByte(0x63);
                    out.writeByte((byte) 0xff);
                    break;
                case 0x16:
                    out.writeShort(8);
                    out.writeByte(req[6]);
                    out.writeByte(0x16);
                    out.writeBytes(req, 8, 6);
                    break;
                case 0x17: {
                    int quantity = ((req[10] & 0xff) << 8) | (req[11] & 0xff);
                    out.writeShort(2 + quantity * 2);
                    out.writeByte(req[6]);
                    out.writeByte(0x17);
                    out.writeByte(quantity * 2);
                    for (int i = 0; i < quantity * 2; i++)
                        out.writeByte(i + 1);
                    break;
                }
                case 0x14: {
                    int recordLength = ((req[14] & 0xff) << 8) | (req[15] & 0xff);
                    out.writeShort(2 + 3 + recordLength * 2);
                    out.writeByte(req[6]);
                    out.writeByte(0x14);
                    out.writeByte(3 + recordLength * 2);
                    out.writeByte(0x06);
                    out.writeShort(recordLength);
                    byte[] data = new byte[recordLength * 2];
                    Arrays.fill(data, (byte) 0x42);
                    out.writeBytes(data);
                    break;
                }
                case 0x15: {
                    int byteCount = req[8] & 0xff;
                    out.writeShort(2 + byteCount);
                    out.writeByte(req[6]);
                    out.writeByte(0x15);
                    out.writeByte(byteCount);
                    out.writeBytes(req, 9, byteCount);
                    break;
                }
                default:
                    out.writeShort(3);
                    out.writeByte(req[6]);
                    out.writeByte(0x80 | fc);
                    out.writeByte(0x01);
                    break;
            }
            byte[] resp = new byte[out.readableBytes()];
            out.getBytes(0, resp);
            out.release();
            ctx.writeAndFlush(Unpooled.wrappedBuffer(resp));
        }
    }
}
