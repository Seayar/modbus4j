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

import com.seayar.modbus4j.base.DataType;
import com.seayar.modbus4j.codec.ModbusCodecType;
import com.seayar.modbus4j.concurrent.AdaptiveConcurrency;
import com.seayar.modbus4j.ip.IpParameters;
import com.seayar.modbus4j.locator.BaseLocator;
import com.seayar.modbus4j.net.ChannelPipelineCustomizer;
import com.seayar.modbus4j.transport.ModbusTransport;
import com.seayar.modbus4j.transport.NettyTransport;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Netty pipeline customization via {@link ChannelPipelineCustomizer}: TLS
 * termination first, then a pre-connection identity handshake, then the Modbus
 * frame handlers.
 * <p>
 * The customizer runs before the Modbus frame decoder/encoder, so the "auth"
 * handler works on raw bytes. On success it removes itself and Modbus frames
 * flow; on failure it closes the channel.
 * <p>
 * NOTE: {@link EmbeddedModbusSlave} is plain TCP, so this sample will fail to
 * handshake against it. Use it as a template against a real TLS + auth
 * gateway, or point it at any SSL-terminating endpoint.
 */
public final class SslPipelineSample {

    /** Application-level auth handshake handler added before the Modbus handlers. */
    public static final class AuthHandshakeHandler extends ChannelInboundHandlerAdapter {
        private boolean authenticated;

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            byte[] greeting = "MODBUS4J-HELLO".getBytes();
            ctx.writeAndFlush(io.netty.buffer.Unpooled.wrappedBuffer(greeting));
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof io.netty.buffer.ByteBuf) {
                io.netty.buffer.ByteBuf buf = (io.netty.buffer.ByteBuf) msg;
                String text = buf.toString(java.nio.charset.StandardCharsets.UTF_8).trim();
                buf.release();
                if ("MODBUS4J-WELCOME".equals(text)) {
                    authenticated = true;
                    System.out.println("[auth] handshake ok, removing auth handler");
                    ctx.pipeline().remove(this);
                    return;
                }
            }
            System.out.println("[auth] handshake rejected");
            ctx.close();
        }

        public boolean isAuthenticated() {
            return authenticated;
        }
    }

    public static void main(String[] args) throws Exception {
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(1502);
        params.setReadTimeoutMillis(3000);

        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        ChannelPipelineCustomizer customizer = pipeline -> {
            pipeline.addLast("ssl", sslContext.newHandler(pipeline.channel().alloc()));
            pipeline.addLast("auth", new AuthHandshakeHandler());
        };

        ModbusTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
                new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1), customizer);
        com.seayar.modbus4j.ModbusMaster master = new com.seayar.modbus4j.ip.TcpMaster(transport, true);
        master.init();
        try {
            Object value = master.getValue(BaseLocator.holdingRegister(1, 0, DataType.TWO_BYTE_INT_UNSIGNED));
            System.out.println("read through TLS pipeline = " + value);
        } finally {
            master.destroy();
        }
    }
}
