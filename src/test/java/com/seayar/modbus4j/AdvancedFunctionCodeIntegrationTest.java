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

import com.seayar.modbus4j.ip.IpParameters;
import com.seayar.modbus4j.msg.FileRecord;
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

import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AdvancedFunctionCodeIntegrationTest {
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
                                int fc = data[7] & 0xff;
                                ByteBuf buf = Unpooled.buffer();
                                buf.writeByte(data[0]);
                                buf.writeByte(data[1]);
                                buf.writeShort(0);
                                switch (fc) {
                                    case 0x07:
                                        buf.writeShort(3);
                                        buf.writeByte(data[6]);
                                        buf.writeByte(0x07);
                                        buf.writeByte(0x05);
                                        break;
                                    case 0x11:
                                        buf.writeShort(5);
                                        buf.writeByte(data[6]);
                                        buf.writeByte(0x11);
                                        buf.writeByte(0x02);
                                        buf.writeByte(0x63);
                                        buf.writeByte((byte) 0xff);
                                        break;
                                    case 0x14: {
                                        int recordLength = ((data[14] & 0xff) << 8) | (data[15] & 0xff);
                                        int byteCount = 3 + recordLength * 2;
                                        buf.writeShort(2 + byteCount);
                                        buf.writeByte(data[6]);
                                        buf.writeByte(0x14);
                                        buf.writeByte(byteCount);
                                        buf.writeByte(0x06);
                                        buf.writeShort(recordLength);
                                        for (int i = 0; i < recordLength * 2; i++)
                                            buf.writeByte(0x12 + i);
                                        break;
                                    }
                                    case 0x15: {
                                        int byteCount = data[8] & 0xff;
                                        buf.writeShort(2 + byteCount);
                                        buf.writeByte(data[6]);
                                        buf.writeByte(0x15);
                                        buf.writeByte(byteCount);
                                        buf.writeBytes(data, 9, byteCount);
                                        break;
                                    }
                                    case 0x16:
                                        buf.writeShort(8);
                                        buf.writeByte(data[6]);
                                        buf.writeByte(0x16);
                                        buf.writeBytes(data, 8, 6);
                                        break;
                                    case 0x17: {
                                        int readQuantity = ((data[10] & 0xff) << 8) | (data[11] & 0xff);
                                        int byteCount = readQuantity * 2;
                                        buf.writeShort(2 + byteCount);
                                        buf.writeByte(data[6]);
                                        buf.writeByte(0x17);
                                        buf.writeByte(byteCount);
                                        for (int i = 0; i < byteCount; i++)
                                            buf.writeByte(i + 1);
                                        break;
                                    }
                                    default:
                                        buf.writeShort(3);
                                        buf.writeByte(data[6]);
                                        buf.writeByte(0x80 | fc);
                                        buf.writeByte(0x02);
                                        break;
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

    @Test(timeout = 15000)
    public void testGetExceptionStatus() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        assertEquals(5, master.getExceptionStatus(1));
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testReportSlaveId() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        assertArrayEquals(new byte[]{0x63, (byte) 0xff}, master.reportSlaveId(1));
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testReadFileRecord() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        byte[] data = master.readFileRecord(1, 5, 3, 2);
        assertEquals(4, data.length);
        assertEquals(0x12, data[0]);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testWriteFileRecord() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        master.writeFileRecord(1, 5, 3, new byte[]{0x12, 0x34, 0x56, 0x78});
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testWriteMaskRegister() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        master.writeMaskRegister(1, 0x10, 0x00ff, 0x000f);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testReadWriteMultipleRegisters() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        byte[] data = master.readWriteMultipleRegisters(1, 0, 4, 10, new byte[]{0x00, 0x01});
        assertEquals(8, data.length);
        assertEquals(0x01, data[0]);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testReadFileRecordsList() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        FileRecord record = new FileRecord(5, 3, 2);
        java.util.List<FileRecord> result = master.readFileRecords(1, Collections.singletonList(record));
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getData().length);
        master.destroy();
    }
}
