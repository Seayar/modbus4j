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
package com.seayar.modbus4j;

import com.seayar.modbus4j.base.DataType;
import com.seayar.modbus4j.exception.ModbusCodeException;
import com.seayar.modbus4j.ip.IpParameters;
import com.seayar.modbus4j.locator.BaseLocator;
import com.seayar.modbus4j.locator.BatchRead;
import com.seayar.modbus4j.locator.BatchResults;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BatchSplitOnExceptionTest {
    private static final int FORBIDDEN_START = 51;
    private static final int FORBIDDEN_END = 59;

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
                                ByteBuf buf = Unpooled.buffer();
                                buf.writeByte(data[0]);
                                buf.writeByte(data[1]);
                                buf.writeShort(0);
                                int fc = data[7] & 0xff;
                                if (fc != 0x03) {
                                    buf.writeShort(3);
                                    buf.writeByte(data[6]);
                                    buf.writeByte(0x80 | fc);
                                    buf.writeByte(0x01);
                                } else {
                                    int start = ((data[8] & 0xff) << 8) | (data[9] & 0xff);
                                    int quantity = ((data[10] & 0xff) << 8) | (data[11] & 0xff);
                                    int end = start + quantity - 1;
                                    if (end >= FORBIDDEN_START && start <= FORBIDDEN_END) {
                                        buf.writeShort(3);
                                        buf.writeByte(data[6]);
                                        buf.writeByte(0x83);
                                        buf.writeByte(0x02);
                                    } else {
                                        byte[] payload = new byte[quantity * 2];
                                        for (int i = 0; i < quantity; i++) {
                                            payload[i * 2] = (byte) ((start + i + 1) >> 8);
                                            payload[i * 2 + 1] = (byte) (start + i + 1);
                                        }
                                        buf.writeShort(3 + payload.length);
                                        buf.writeByte(data[6]);
                                        buf.writeByte(0x03);
                                        buf.writeByte(payload.length);
                                        buf.writeBytes(payload);
                                    }
                                }
                                byte[] out = new byte[buf.readableBytes()];
                                buf.getBytes(0, out);
                                buf.release();
                                ctx.writeAndFlush(Unpooled.wrappedBuffer(out));
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

    private ModbusMaster createMaster() {
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(port);
        params.setReadTimeoutMillis(5000);
        return new ModbusFactory().createTcpMaster(params, true);
    }

    private BatchRead<Integer> createBatch() {
        BatchRead<Integer> batch = new BatchRead<>();
        for (int i = 0; i <= 100; i++)
            batch.addLocator(i, BaseLocator.holdingRegister(1, i, DataType.TWO_BYTE_INT_UNSIGNED));
        return batch;
    }

    @Test(timeout = 30000)
    public void testSplitOnExceptionRecoversReadablePoints() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchResults<Integer> results = master.send(createBatch());
        for (int i = 0; i <= 100; i++) {
            if (i >= FORBIDDEN_START && i <= FORBIDDEN_END) {
                assertTrue("offset " + i + " should be an error", results.isError(i));
            } else {
                assertEquals("offset " + i, i + 1, ((Number) results.getValue(i)).intValue());
            }
        }
        master.destroy();
    }

    @Test(timeout = 15000, expected = ModbusCodeException.class)
    public void testSplitDisabledThrows() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchRead<Integer> batch = createBatch();
        batch.setSplitOnException(false);
        master.send(batch);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testSingleForbiddenPointBecomesError() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchRead<String> batch = new BatchRead<>();
        batch.addLocator("bad", BaseLocator.holdingRegister(1, 55, DataType.TWO_BYTE_INT_UNSIGNED));
        BatchResults<String> results = master.send(batch);
        assertTrue(results.isError("bad"));
        assertTrue(results.getErrors().contains("bad"));
        master.destroy();
    }

    @Test(timeout = 30000)
    public void testReadablePointKeepsValueAlongsideErrors() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchResults<Integer> results = master.send(createBatch());
        assertEquals(1, ((Number) results.getValue(0)).intValue());
        assertEquals(101, ((Number) results.getValue(100)).intValue());
        assertTrue(results.isError(55));
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testSplitOnExceptionDefaultTrue() {
        assertTrue(new BatchRead<>().isSplitOnException());
    }
}
