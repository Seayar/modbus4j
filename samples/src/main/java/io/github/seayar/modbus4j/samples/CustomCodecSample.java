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
package io.github.seayar.modbus4j.samples;

import io.github.seayar.modbus4j.base.FunctionCode;
import io.github.seayar.modbus4j.codec.ModbusCodec;
import io.github.seayar.modbus4j.codec.ModbusFrame;
import io.github.seayar.modbus4j.codec.TcpCodec;
import io.github.seayar.modbus4j.concurrent.AdaptiveConcurrency;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.msg.AbstractModbusRequest;
import io.github.seayar.modbus4j.msg.AbstractModbusResponse;
import io.github.seayar.modbus4j.msg.ReadExceptionStatusRequest;
import io.github.seayar.modbus4j.msg.ReadExceptionStatusResponse;
import io.github.seayar.modbus4j.transport.ModbusTransport;
import io.github.seayar.modbus4j.transport.NettyTransport;
import io.netty.buffer.ByteBuf;

/**
 * The "reserved extension capability" in practice: a vendor function code
 * (mask-write register, FC 22) implemented with a custom request class and a
 * custom {@link ModbusCodec} that adds the new response type to the standard
 * TCP framing.
 * <p>
 * Run {@link EmbeddedModbusSlave} first, then this sample.
 */
public final class CustomCodecSample {

    /** A vendor request for Modbus function code 22 (mask-write register). */
    public static final class MaskWriteRegisterRequest extends AbstractModbusRequest {
        private final int offset;
        private final int andMask;
        private final int orMask;

        public MaskWriteRegisterRequest(int slaveId, int offset, int andMask, int orMask) {
            super(slaveId, FunctionCode.WRITE_MASK_REGISTER);
            this.offset = offset;
            this.andMask = andMask;
            this.orMask = orMask;
        }

        @Override
        protected int getDataLength() {
            return 6;
        }

        @Override
        protected void writeData(ByteBuf buf) {
            buf.writeShort(offset).writeShort(andMask).writeShort(orMask);
        }

        @Override
        public void readPdu(ByteBuf buf) {
        }
    }

    /** Wire codec: standard TCP for built-ins, plus FC 22 responses. */
    public static final class VendorCodec implements ModbusCodec {
        private final TcpCodec delegate = new TcpCodec();

        @Override
        public ByteBuf encode(AbstractModbusRequest request, int transactionId) {
            return delegate.encode(request, transactionId);
        }

        @Override
        public ModbusFrame decode(ByteBuf in) {
            ModbusFrame frame = decodeMaskWrite(in);
            if (frame != null)
                return frame;
            return delegate.decode(in);
        }

        private ModbusFrame decodeMaskWrite(ByteBuf in) {
            if (in.readableBytes() < 7)
                return null;
            in.markReaderIndex();
            int transactionId = in.readUnsignedShort();
            in.readUnsignedShort();
            int length = in.readUnsignedShort();
            if (in.readableBytes() < length - 1) {
                in.resetReaderIndex();
                return null;
            }
            int slaveId = in.readUnsignedByte();
            int functionCode = in.readUnsignedByte();
            if (functionCode != FunctionCode.WRITE_MASK_REGISTER) {
                in.resetReaderIndex();
                return null;
            }
            int offset = in.readUnsignedShort();
            int andMask = in.readUnsignedShort();
            int orMask = in.readUnsignedShort();
            return new ModbusFrame(transactionId,
                    new io.github.seayar.modbus4j.msg.WriteMaskRegisterResponse(slaveId, offset, andMask, orMask));
        }
    }

    public static void main(String[] args) throws Exception {
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(1502);
        params.setReadTimeoutMillis(3000);

        ModbusTransport transport = new NettyTransport(params, new VendorCodec(), false,
                new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1), null);
        io.github.seayar.modbus4j.ModbusMaster master = new io.github.seayar.modbus4j.ip.TcpMaster(transport, true);
        master.init();
        try {
            master.getTransport().send(new MaskWriteRegisterRequest(1, 0x4000, 0x00ff, 0x0010));
            System.out.println("custom FC22 mask-write sent via custom codec");

            AbstractModbusResponse status = master.getTransport()
                    .send(new ReadExceptionStatusRequest(1));
            System.out.println("standard FC7 still works through the same codec: "
                    + ((ReadExceptionStatusResponse) status).getExceptionStatus());
        } finally {
            master.destroy();
        }
    }
}
