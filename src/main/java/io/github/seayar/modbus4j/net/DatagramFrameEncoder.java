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
import io.github.seayar.modbus4j.codec.ModbusFrame;
import io.github.seayar.modbus4j.msg.AbstractModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.net.InetSocketAddress;
import java.util.List;

public class DatagramFrameEncoder extends MessageToMessageEncoder<ModbusFrame> {
    private final ModbusCodec codec;

    public DatagramFrameEncoder(ModbusCodec codec) {
        this.codec = codec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusFrame msg, List<Object> out) throws Exception {
        AbstractModbusRequest request = (AbstractModbusRequest) msg.getMessage();
        ByteBuf encoded = codec.encode(request, msg.getTransactionId());
        InetSocketAddress remote;
        if (ctx.channel().remoteAddress() instanceof InetSocketAddress)
            remote = (InetSocketAddress) ctx.channel().remoteAddress();
        else
            remote = new InetSocketAddress(0);
        out.add(new DatagramPacket(encoded, remote));
    }
}
