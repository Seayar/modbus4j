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

public class WriteMaskRegisterResponse extends AbstractModbusResponse {
    private final int offset;
    private final int andMask;
    private final int orMask;

    public WriteMaskRegisterResponse(int slaveId, int offset, int andMask, int orMask) {
        super(slaveId, FunctionCode.WRITE_MASK_REGISTER);
        this.offset = offset;
        this.andMask = andMask;
        this.orMask = orMask;
    }

    public WriteMaskRegisterResponse(int slaveId, ByteBuf data) {
        this(slaveId, data.readUnsignedShort(), data.readUnsignedShort(), data.readUnsignedShort());
    }

    @Override
    protected int getDataLength() {
        return 6;
    }

    @Override
    protected void writeData(ByteBuf buf) {
        buf.writeShort(offset);
        buf.writeShort(andMask);
        buf.writeShort(orMask);
    }

    @Override
    public void readPdu(ByteBuf buf) {
    }

    public int getOffset() {
        return offset;
    }

    public int getAndMask() {
        return andMask;
    }

    public int getOrMask() {
        return orMask;
    }
}
