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
package io.github.seayar.modbus4j.msg;

import io.github.seayar.modbus4j.base.FunctionCode;
import io.netty.buffer.ByteBuf;

public class ReadWriteMultipleRegistersRequest extends AbstractModbusRequest {
    private final int readStartOffset;
    private final int readQuantity;
    private final int writeStartOffset;
    private final int writeQuantity;
    private final byte[] writeData;

    public ReadWriteMultipleRegistersRequest(int slaveId, int readStartOffset, int readQuantity,
            int writeStartOffset, byte[] writeData) {
        super(slaveId, FunctionCode.READ_WRITE_MULTIPLE_REGISTERS);
        this.readStartOffset = readStartOffset;
        this.readQuantity = readQuantity;
        this.writeStartOffset = writeStartOffset;
        this.writeData = writeData;
        this.writeQuantity = writeData.length / 2;
    }

    @Override
    protected int getDataLength() {
        return 9 + writeData.length;
    }

    @Override
    protected void writeData(ByteBuf buf) {
        buf.writeShort(readStartOffset);
        buf.writeShort(readQuantity);
        buf.writeShort(writeStartOffset);
        buf.writeShort(writeQuantity);
        buf.writeByte(writeData.length);
        buf.writeBytes(writeData);
    }

    @Override
    public void readPdu(ByteBuf buf) {
    }

    public int getReadStartOffset() {
        return readStartOffset;
    }

    public int getReadQuantity() {
        return readQuantity;
    }

    public int getWriteStartOffset() {
        return writeStartOffset;
    }

    public int getWriteQuantity() {
        return writeQuantity;
    }

    public byte[] getWriteData() {
        return writeData;
    }
}
