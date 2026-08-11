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
package io.github.seayar.modbus4j.codec;

import io.github.seayar.modbus4j.msg.ReadHoldingRegistersRequest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class ModbusCodecTypeTest {

    @Test
    public void testTypes() {
        assertSame(ModbusCodecType.TCP.getCodec().getClass(), TcpCodec.class);
        assertSame(ModbusCodecType.RTU.getCodec().getClass(), RtuCodec.class);
        assertSame(ModbusCodecType.ASCII.getCodec().getClass(), AsciiCodec.class);
    }

    @Test
    public void testCodecsWork() {
        ReadHoldingRegistersRequest req = new ReadHoldingRegistersRequest(1, 0, 2);
        for (ModbusCodecType type : ModbusCodecType.values()) {
            assertNotNull(type.getCodec().encode(req, 1));
        }
    }
}
