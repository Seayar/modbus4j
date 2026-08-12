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

import io.github.seayar.modbus4j.codec.ModbusCodec;
import io.github.seayar.modbus4j.concurrent.PendingRequests;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

public class UdpChannelInitializer extends ChannelInitializer<Channel> {
    private final ModbusCodec codec;
    private final PendingRequests pendingRequests;
    private final int readTimeoutMillis;
    private final ChannelPipelineCustomizer pipelineCustomizer;

    public UdpChannelInitializer(ModbusCodec codec, PendingRequests pendingRequests, int readTimeoutMillis) {
        this(codec, pendingRequests, readTimeoutMillis, null);
    }

    public UdpChannelInitializer(ModbusCodec codec, PendingRequests pendingRequests, int readTimeoutMillis,
            ChannelPipelineCustomizer pipelineCustomizer) {
        this.codec = codec;
        this.pendingRequests = pendingRequests;
        this.readTimeoutMillis = readTimeoutMillis;
        this.pipelineCustomizer = pipelineCustomizer;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (pipelineCustomizer != null)
            pipelineCustomizer.customize(pipeline);
        pipeline.addLast("frameDecoder", new DatagramFrameDecoder(codec));
        pipeline.addLast("idle", new IdleStateHandler(readTimeoutMillis, 0, 0));
        pipeline.addLast("responseHandler", new ModbusResponseHandler(pendingRequests, false));
        pipeline.addLast("frameEncoder", new DatagramFrameEncoder(codec));
    }
}
