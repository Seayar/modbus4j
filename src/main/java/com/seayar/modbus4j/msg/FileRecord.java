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
package com.seayar.modbus4j.msg;

public class FileRecord {
    private final int fileNumber;
    private final int recordNumber;
    private int recordLength;
    private byte[] data;

    public FileRecord(int fileNumber, int recordNumber, int recordLength) {
        this.fileNumber = fileNumber;
        this.recordNumber = recordNumber;
        this.recordLength = recordLength;
        this.data = new byte[recordLength * 2];
    }

    public FileRecord(int fileNumber, int recordNumber, byte[] data) {
        this.fileNumber = fileNumber;
        this.recordNumber = recordNumber;
        this.data = data;
        this.recordLength = data.length / 2;
    }

    public FileRecord(byte[] data) {
        this(-1, -1, data);
    }

    public int getFileNumber() {
        return fileNumber;
    }

    public int getRecordNumber() {
        return recordNumber;
    }

    public int getRecordLength() {
        return recordLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        this.recordLength = data.length / 2;
    }
}
