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
package io.github.seayar.modbus4j.msg;

import io.netty.buffer.ByteBuf;

public class WriteCoilResponse extends WriteResponse {

    public WriteCoilResponse(int slaveId, int offset, boolean value) {
        super(slaveId, (byte) 0x05, offset, value ? 0xff00 : 0x0000);
    }

    public WriteCoilResponse(int slaveId, ByteBuf data) {
        super(slaveId, (byte) 0x05, data.readUnsignedShort(), data.readUnsignedShort());
    }

    public boolean getValueBoolean() {
        return getValue() == 0xff00;
    }
}
