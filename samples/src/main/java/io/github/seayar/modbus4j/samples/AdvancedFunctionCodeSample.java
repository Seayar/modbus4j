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

import io.github.seayar.modbus4j.ModbusFactory;
import io.github.seayar.modbus4j.ModbusMaster;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.msg.FileRecord;
import io.github.seayar.modbus4j.util.HexUtil;

import java.util.Collections;

/**
 * The extended function codes added in this release: read exception status
 * (FC 7), report slave id (FC 17), read/write file records (FC 20/21),
 * mask-write register (FC 22) and read/write multiple registers (FC 23).
 * <p>
 * Run {@link EmbeddedModbusSlave} first, then this sample.
 */
public final class AdvancedFunctionCodeSample {

    public static void main(String[] args) throws Exception {
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(1502);
        params.setReadTimeoutMillis(3000);

        ModbusMaster master = new ModbusFactory().createTcpMaster(params, true);
        master.init();
        try {
            System.out.println("exception status   = " + master.getExceptionStatus(1));
            System.out.println("report slave id    = "
                    + HexUtil.bytesToHexString(master.reportSlaveId(1), " "));

            byte[] record = master.readFileRecord(1, 5, 3, 4);
            System.out.println("read file record   = "
                    + HexUtil.bytesToHexString(record, " "));

            master.writeFileRecord(1, 5, 3, new byte[]{1, 2, 3, 4});
            System.out.println("write file record  = ok");

            master.writeMaskRegister(1, 0x4000, 0x00ff, 0x0010);
            System.out.println("mask-write register= ok");

            byte[] data = master.readWriteMultipleRegisters(1, 0, 4, 10, new byte[]{0x00, 0x01});
            System.out.println("read/write multiple= " + HexUtil.bytesToHexString(data, " "));

            FileRecord record2 = new FileRecord(5, 3, 2);
            java.util.List<FileRecord> records = master.readFileRecords(1, Collections.singletonList(record2));
            System.out.println("read file records  = " + records.size() + " record(s)");
        } finally {
            master.destroy();
        }
    }
}
