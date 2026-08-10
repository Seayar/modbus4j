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
package com.seayar.modbus4j.codec;

import com.seayar.modbus4j.msg.AbstractModbusMessage;
import com.seayar.modbus4j.msg.AbstractModbusRequest;
import com.seayar.modbus4j.msg.MessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TcpCodec implements ModbusCodec {
    public static final int MBAP_HEADER_LENGTH = 7;
    public static final short PROTOCOL_ID = 0;

    @Override
    public ByteBuf encode(AbstractModbusRequest request, int transactionId) {
        int pduLength = request.getPduLength();
        ByteBuf buf = Unpooled.buffer(MBAP_HEADER_LENGTH + pduLength);
        buf.writeShort(transactionId);
        buf.writeShort(PROTOCOL_ID);
        buf.writeShort(pduLength + 1);
        buf.writeByte(request.getSlaveId());
        request.writePdu(buf);
        return buf;
    }

    @Override
    public ModbusFrame decode(ByteBuf in) {
        if (in.readableBytes() < MBAP_HEADER_LENGTH)
            return null;
        in.markReaderIndex();
        int transactionId = in.readUnsignedShort();
        int protocolId = in.readUnsignedShort();
        int length = in.readUnsignedShort();
        if (in.readableBytes() < length - 1) {
            in.resetReaderIndex();
            return null;
        }
        int slaveId = in.readUnsignedByte();
        AbstractModbusMessage message = MessageUtil.createResponse(slaveId, in.readByte(), in);
        return new ModbusFrame(transactionId, message);
    }
}
