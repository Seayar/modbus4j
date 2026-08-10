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
package com.seayar.modbus4j.net;

import com.seayar.modbus4j.codec.ModbusCodec;
import com.seayar.modbus4j.codec.ModbusFrame;
import com.seayar.modbus4j.msg.AbstractModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ModbusFrameEncoder extends MessageToByteEncoder<ModbusFrame> {
    private final ModbusCodec codec;

    public ModbusFrameEncoder(ModbusCodec codec) {
        this.codec = codec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusFrame msg, ByteBuf out) throws Exception {
        AbstractModbusRequest request = (AbstractModbusRequest) msg.getMessage();
        ByteBuf encoded = codec.encode(request, msg.getTransactionId());
        out.writeBytes(encoded);
        encoded.release();
    }
}
