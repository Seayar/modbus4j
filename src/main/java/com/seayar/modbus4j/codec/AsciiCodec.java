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
import com.seayar.modbus4j.util.AsciiLrcUtil;
import com.seayar.modbus4j.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class AsciiCodec implements ModbusCodec {
    private static final byte START = ':';
    private static final byte CR = '\r';
    private static final byte LF = '\n';

    @Override
    public ByteBuf encode(AbstractModbusRequest request, int transactionId) {
        int pduLength = request.getPduLength();
        byte[] payload = new byte[1 + pduLength];
        payload[0] = (byte) request.getSlaveId();
        ByteBuf pdu = Unpooled.buffer(pduLength);
        request.writePdu(pdu);
        pdu.readBytes(payload, 1, pduLength);
        payload = appendLrc(payload);
        StringBuilder sb = new StringBuilder(payload.length * 2 + 3);
        sb.append((char) START);
        sb.append(HexUtil.bytesToHexString(payload, ""));
        sb.append((char) CR);
        sb.append((char) LF);
        return Unpooled.wrappedBuffer(sb.toString().getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    }

    private byte[] appendLrc(byte[] payload) {
        byte lrc = AsciiLrcUtil.calculateLRC(payload);
        byte[] result = new byte[payload.length + 1];
        System.arraycopy(payload, 0, result, 0, payload.length);
        result[payload.length] = lrc;
        return result;
    }

    @Override
    public ModbusFrame decode(ByteBuf in) {
        if (in.readableBytes() < 3)
            return null;
        in.markReaderIndex();
        if (in.readByte() != START) {
            in.resetReaderIndex();
            return null;
        }
        int hexStart = in.readerIndex();
        int crIndex = in.forEachByte(value -> value != CR);
        if (crIndex < 0) {
            in.resetReaderIndex();
            return null;
        }
        int hexLength = crIndex - hexStart;
        if (hexLength % 2 != 0) {
            in.resetReaderIndex();
            return null;
        }
        in.readerIndex(crIndex);
        if (in.readByte() != CR || in.readByte() != LF) {
            in.resetReaderIndex();
            return null;
        }
        byte[] hexChars = new byte[hexLength];
        in.getBytes(hexStart, hexChars);
        byte[] payload = HexUtil.hexStringToByte(new String(hexChars, java.nio.charset.StandardCharsets.US_ASCII));
        byte expected = AsciiLrcUtil.calculateLRC(payload, 0, payload.length - 1);
        if (payload[payload.length - 1] != expected) {
            in.resetReaderIndex();
            return null;
        }
        int slaveId = payload[0] & 0xff;
        ByteBuf data = Unpooled.wrappedBuffer(payload, 1, payload.length - 2);
        AbstractModbusMessage message = MessageUtil.createResponse(slaveId, data.readByte(), data);
        return new ModbusFrame(-1, message);
    }
}
