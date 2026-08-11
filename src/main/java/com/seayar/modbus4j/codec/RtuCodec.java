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

import com.seayar.modbus4j.base.FunctionCode;
import com.seayar.modbus4j.msg.AbstractModbusMessage;
import com.seayar.modbus4j.msg.AbstractModbusRequest;
import com.seayar.modbus4j.msg.MessageUtil;
import com.seayar.modbus4j.util.RtuCrcUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtuCodec implements ModbusCodec {
    private static final Logger LOG = LoggerFactory.getLogger(RtuCodec.class);

    @Override
    public ByteBuf encode(AbstractModbusRequest request, int transactionId) {
        int pduLength = request.getPduLength();
        ByteBuf buf = Unpooled.buffer(pduLength + 3);
        buf.writeByte(request.getSlaveId());
        request.writePdu(buf);
        int crc = RtuCrcUtil.calculateCRC(buf.array(), pduLength + 1);
        buf.writeByte(crc & 0xff);
        buf.writeByte((crc >> 8) & 0xff);
        return buf;
    }

    @Override
    public ModbusFrame decode(ByteBuf in) {
        if (in.readableBytes() < 4)
            return null;
        in.markReaderIndex();
        int slaveId = in.readUnsignedByte();
        int functionCode = in.readUnsignedByte();
        int dataLength = getResponseDataLength(functionCode, in);
        if (dataLength < 0) {
            resync(in);
            return null;
        }
        if (in.readableBytes() < dataLength + 2) {
            in.resetReaderIndex();
            return null;
        }
        byte[] crcData = new byte[2 + dataLength];
        crcData[0] = (byte) slaveId;
        crcData[1] = (byte) functionCode;
        in.readBytes(crcData, 2, dataLength);
        int expected = RtuCrcUtil.calculateCRC(crcData, crcData.length);
        int crcLow = in.readUnsignedByte();
        int crcHigh = in.readUnsignedByte();
        int actual = (crcHigh << 8) | crcLow;
        if (expected != actual) {
            LOG.warn("Dropping RTU frame with bad CRC: slaveId={}, functionCode={}", slaveId, functionCode);
            resync(in);
            return null;
        }
        ByteBuf data = Unpooled.wrappedBuffer(crcData, 2, dataLength);
        AbstractModbusMessage message = MessageUtil.createResponse(slaveId, (byte) functionCode, data);
        return new ModbusFrame(-1, message);
    }

    private void resync(ByteBuf in) {
        in.resetReaderIndex();
        in.skipBytes(1);
    }

    private int getResponseDataLength(int functionCode, ByteBuf in) {
        if (FunctionCode.isException((byte) functionCode))
            return 1;
        switch (functionCode) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 17:
            case 20:
            case 21:
            case 23:
                if (in.readableBytes() < 1)
                    return -1;
                return 1 + in.getUnsignedByte(in.readerIndex());
            case 5:
            case 6:
            case 15:
            case 16:
                return 4;
            case 7:
                return 1;
            case 22:
                return 6;
        }
        return -1;
    }
}
