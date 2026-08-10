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
package com.seayar.modbus4j.msg;

import io.netty.buffer.ByteBuf;

public abstract class ReadResponse extends AbstractModbusResponse {
    private int byteCount;
    private byte[] data;

    public ReadResponse(int slaveId, byte functionCode, int byteCount, byte[] data) {
        super(slaveId, functionCode);
        this.byteCount = byteCount;
        this.data = data;
    }

    @Override
    protected int getDataLength() {
        return 1 + byteCount;
    }

    @Override
    protected void writeData(ByteBuf buf) {
        buf.writeByte(byteCount);
        buf.writeBytes(data);
    }

    @Override
    public void readPdu(ByteBuf buf) {
        byteCount = buf.readUnsignedByte();
        data = new byte[byteCount];
        buf.readBytes(data);
    }

    public int getByteCount() {
        return byteCount;
    }

    public byte[] getData() {
        return data;
    }
}
