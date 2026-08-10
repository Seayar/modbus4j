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

public class WriteCoilsRequest extends WriteMultipleRequest {

    public WriteCoilsRequest(int slaveId, int startOffset, byte[] data) {
        super(slaveId, (byte) 0x0f, startOffset, data.length * 8, data);
    }

    public WriteCoilsRequest(int slaveId, int startOffset, int numberOfCoils, byte[] data) {
        super(slaveId, (byte) 0x0f, startOffset, numberOfCoils, data);
    }

}
