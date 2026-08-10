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

import com.seayar.modbus4j.exception.ModbusCodeException;
import io.netty.buffer.ByteBuf;

public class ExceptionResponse extends AbstractModbusResponse {
    private final int exceptionCode;

    public ExceptionResponse(int slaveId, int exceptionCode) {
        super(slaveId, (byte) (0x80 | exceptionCode));
        this.exceptionCode = exceptionCode;
    }

    @Override
    protected int getDataLength() {
        return 1;
    }

    @Override
    protected void writeData(ByteBuf buf) {
        buf.writeByte(exceptionCode);
    }

    @Override
    public void readPdu(ByteBuf buf) {
        buf.readByte();
    }

    public int getExceptionCode() {
        return exceptionCode;
    }

    public ModbusCodeException toException() {
        return new ModbusCodeException((byte) exceptionCode);
    }

    @Override
    public boolean isException() {
        return true;
    }
}
