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

import io.github.seayar.modbus4j.exception.ModbusCodeException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageUtilTest {

    @Test
    public void testCreateResponseReadCoils() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x01,
                Unpooled.wrappedBuffer(new byte[]{0x02, 0x00, 0x01}));
        assertTrue(resp instanceof ReadCoilsResponse);
    }

    @Test
    public void testCreateResponseReadDiscrete() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x02,
                Unpooled.wrappedBuffer(new byte[]{0x01, 0x00}));
        assertTrue(resp instanceof ReadDiscreteInputsResponse);
    }

    @Test
    public void testCreateResponseReadHolding() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x03,
                Unpooled.wrappedBuffer(new byte[]{0x02, 0x00, 0x01}));
        assertTrue(resp instanceof ReadHoldingRegistersResponse);
    }

    @Test
    public void testCreateResponseReadInput() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x04,
                Unpooled.wrappedBuffer(new byte[]{0x02, 0x00, 0x01}));
        assertTrue(resp instanceof ReadInputRegistersResponse);
    }

    @Test
    public void testCreateResponseWriteCoil() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x05,
                Unpooled.wrappedBuffer(new byte[]{0x00, 0x05, (byte) 0xff, 0x00}));
        assertTrue(resp instanceof WriteCoilResponse);
    }

    @Test
    public void testCreateResponseWriteRegister() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x06,
                Unpooled.wrappedBuffer(new byte[]{0x00, 0x05, 0x12, 0x34}));
        assertTrue(resp instanceof WriteRegisterResponse);
    }

    @Test
    public void testCreateResponseWriteCoils() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x0f,
                Unpooled.wrappedBuffer(new byte[]{0x00, 0x05, 0x00, 0x08}));
        assertTrue(resp instanceof WriteCoilsResponse);
    }

    @Test
    public void testCreateResponseWriteRegisters() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x10,
                Unpooled.wrappedBuffer(new byte[]{0x00, 0x05, 0x00, 0x02}));
        assertTrue(resp instanceof WriteRegistersResponse);
    }

    @Test
    public void testCreateResponseException() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x83,
                Unpooled.wrappedBuffer(new byte[]{0x02}));
        assertTrue(resp instanceof ExceptionResponse);
        assertEquals(2, ((ExceptionResponse) resp).getExceptionCode());
    }

    @Test
    public void testCreateResponseFromRequest() {
        ReadHoldingRegistersRequest req = new ReadHoldingRegistersRequest(1, 0, 1);
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{(byte) 0x83, 0x02});
        AbstractModbusResponse resp = req.createResponse(data);
        assertTrue(resp instanceof ExceptionResponse);
    }

    @Test
    public void testCreateResponseFromRequestRead() {
        ReadCoilsRequest req = new ReadCoilsRequest(1, 0, 8);
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x01, 0x01, 0x05});
        AbstractModbusResponse resp = req.createResponse(data);
        assertTrue(resp instanceof ReadCoilsResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateResponseUnsupported() {
        MessageUtil.createResponse(1, (byte) 0x2b, Unpooled.wrappedBuffer(new byte[]{0x00}));
    }

    @Test
    public void testCreateResponseFromRequestWriteCoil() {
        WriteCoilRequest req = new WriteCoilRequest(1, 5, true);
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x05, 0x00, 5, (byte) 0xff, 0x00});
        assertTrue(req.createResponse(data) instanceof WriteCoilResponse);
    }

    @Test
    public void testCreateResponseFromRequestWriteRegister() {
        WriteRegisterRequest req = new WriteRegisterRequest(1, 5, 0x1234);
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x06, 0x00, 5, 0x12, 0x34});
        assertTrue(req.createResponse(data) instanceof WriteRegisterResponse);
    }

    @Test
    public void testCreateResponseFromRequestWriteCoils() {
        WriteCoilsRequest req = new WriteCoilsRequest(1, 0, new byte[]{0x01});
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x0f, 0x00, 0, 0x00, 8});
        assertTrue(req.createResponse(data) instanceof WriteCoilsResponse);
    }

    @Test
    public void testCreateResponseFromRequestWriteRegisters() {
        WriteRegistersRequest req = new WriteRegistersRequest(1, 10, new byte[]{0, 1});
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x10, 0x00, 10, 0x00, 1});
        assertTrue(req.createResponse(data) instanceof WriteRegistersResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateResponseFromRequestUnsupported() {
        ReadCoilsRequest req = new ReadCoilsRequest(1, 0, 8);
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x2b, 0x00});
        req.createResponse(data);
    }
}
