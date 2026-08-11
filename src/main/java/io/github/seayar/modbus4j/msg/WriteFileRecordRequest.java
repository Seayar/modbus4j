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

import java.util.ArrayList;
import java.util.List;

public class WriteFileRecordRequest extends AbstractModbusRequest {
    private final List<FileRecord> records;

    public WriteFileRecordRequest(int slaveId, List<FileRecord> records) {
        super(slaveId, FunctionCode.WRITE_FILE_RECORD);
        this.records = records;
    }

    @Override
    protected int getDataLength() {
        int length = 1;
        for (FileRecord record : records)
            length += 7 + record.getData().length;
        return length;
    }

    @Override
    protected void writeData(ByteBuf buf) {
        buf.writeByte(getDataLength() - 1);
        for (FileRecord record : records) {
            buf.writeByte(0x06);
            buf.writeShort(record.getFileNumber());
            buf.writeShort(record.getRecordNumber());
            buf.writeShort(record.getData().length / 2);
            buf.writeBytes(record.getData());
        }
    }

    @Override
    public void readPdu(ByteBuf buf) {
    }

    public List<FileRecord> getRecords() {
        return new ArrayList<>(records);
    }
}
