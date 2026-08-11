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
package com.seayar.modbus4j.net;

import com.seayar.modbus4j.codec.TcpCodec;
import com.seayar.modbus4j.concurrent.PendingRequests;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ChannelPipelineCustomizerTest {

    @Test
    public void testCustomizerAppliedBeforeModbusHandlers() {
        PendingRequests pending = new PendingRequests();
        ChannelPipelineCustomizer customizer = pipeline -> pipeline.addLast("marker",
                new ChannelInboundHandlerAdapter() {
                });
        EmbeddedChannel channel = new EmbeddedChannel(
                new ModbusChannelInitializer(new TcpCodec(), pending, 1000, customizer));
        assertEquals("marker", channel.pipeline().names().get(0));
        assertNotNull(channel.pipeline().get("frameDecoder"));
        assertNotNull(channel.pipeline().get("frameEncoder"));
        assertNotNull(channel.pipeline().get("responseHandler"));
        channel.finishAndReleaseAll();
    }

    @Test
    public void testDefaultInitializerAddsNoCustomHandlers() {
        PendingRequests pending = new PendingRequests();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusChannelInitializer(new TcpCodec(), pending, 1000));
        assertNull(channel.pipeline().get("marker"));
        assertNotNull(channel.pipeline().get("frameDecoder"));
        channel.finishAndReleaseAll();
    }
}
