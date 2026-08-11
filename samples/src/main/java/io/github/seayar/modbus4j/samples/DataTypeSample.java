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
import io.github.seayar.modbus4j.base.DataType;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.locator.BaseLocator;

import java.math.BigInteger;

/**
 * The built-in register data types: sizes, signedness and byte/word swap
 * variants. BCD and MOD10K are also fully wired in this release.
 * <p>
 * Run {@link EmbeddedModbusSlave} first, then this sample.
 */
public final class DataTypeSample {

    public static void main(String[] args) throws Exception {
        IpParameters params = new IpParameters();
        params.setHost("127.0.0.1");
        params.setPort(1502);
        params.setReadTimeoutMillis(3000);

        ModbusMaster master = new ModbusFactory().createTcpMaster(params, true);
        master.init();
        try {
            int[] types = {
                    DataType.TWO_BYTE_INT_UNSIGNED,
                    DataType.TWO_BYTE_INT_SIGNED,
                    DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED,
                    DataType.FOUR_BYTE_INT_UNSIGNED,
                    DataType.FOUR_BYTE_FLOAT,
                    DataType.FOUR_BYTE_FLOAT_SWAPPED,
                    DataType.EIGHT_BYTE_FLOAT,
                    DataType.TWO_BYTE_BCD,
                    DataType.FOUR_BYTE_BCD,
                    DataType.FOUR_BYTE_MOD_10K
            };
            for (int type : types) {
                Object value = master.getValue(BaseLocator.holdingRegister(1, 0, type));
                System.out.println(name(type) + " = " + value
                        + "  (java type " + (value == null ? "?" : value.getClass().getSimpleName()) + ")");
            }

            master.setValue(BaseLocator.holdingRegister(1, 10, DataType.FOUR_BYTE_FLOAT), 23.5f);
            System.out.println("written float read back = "
                    + master.getValue(BaseLocator.holdingRegister(1, 10, DataType.FOUR_BYTE_FLOAT)));

            master.setValue(BaseLocator.holdingRegister(1, 20, DataType.EIGHT_BYTE_MOD_10K),
                    new BigInteger("1234567890123456"));
            System.out.println("written MOD10K read back = "
                    + master.getValue(BaseLocator.holdingRegister(1, 20, DataType.EIGHT_BYTE_MOD_10K)));
        } finally {
            master.destroy();
        }
    }

    private static String name(int type) {
        switch (type) {
            case DataType.TWO_BYTE_INT_UNSIGNED:
                return "TWO_BYTE_INT_UNSIGNED";
            case DataType.TWO_BYTE_INT_SIGNED:
                return "TWO_BYTE_INT_SIGNED";
            case DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED:
                return "TWO_BYTE_INT_UNSIGNED_SWAPPED";
            case DataType.FOUR_BYTE_INT_UNSIGNED:
                return "FOUR_BYTE_INT_UNSIGNED";
            case DataType.FOUR_BYTE_FLOAT:
                return "FOUR_BYTE_FLOAT";
            case DataType.FOUR_BYTE_FLOAT_SWAPPED:
                return "FOUR_BYTE_FLOAT_SWAPPED";
            case DataType.EIGHT_BYTE_FLOAT:
                return "EIGHT_BYTE_FLOAT";
            case DataType.TWO_BYTE_BCD:
                return "TWO_BYTE_BCD";
            case DataType.FOUR_BYTE_BCD:
                return "FOUR_BYTE_BCD";
            case DataType.FOUR_BYTE_MOD_10K:
                return "FOUR_BYTE_MOD_10K";
            default:
                return "type=" + type;
        }
    }
}
