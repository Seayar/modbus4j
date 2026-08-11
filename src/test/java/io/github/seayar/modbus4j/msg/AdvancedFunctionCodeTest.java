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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdvancedFunctionCodeTest {

    private static byte[] bytes(ByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(0, data);
        return data;
    }

    @Test
    public void testReadExceptionStatusRequest() {
        ReadExceptionStatusRequest req = new ReadExceptionStatusRequest(3);
        assertEquals(0x07, req.getFunctionCode());
        assertEquals(1, req.getPduLength());
        ByteBuf buf = Unpooled.buffer();
        req.writePdu(buf);
        assertArrayEquals(new byte[]{0x07}, bytes(buf));
        buf.release();
    }

    @Test
    public void testReadExceptionStatusResponse() {
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x05});
        ReadExceptionStatusResponse resp = new ReadExceptionStatusResponse(3, data);
        assertEquals(5, resp.getExceptionStatus());
        ByteBuf out = Unpooled.buffer();
        resp.writePdu(out);
        assertArrayEquals(new byte[]{0x07, 0x05}, bytes(out));
        out.release();
    }

    @Test
    public void testReportSlaveIdRequest() {
        ReportSlaveIdRequest req = new ReportSlaveIdRequest(1);
        assertEquals(0x11, req.getFunctionCode());
        ByteBuf buf = Unpooled.buffer();
        req.writePdu(buf);
        assertArrayEquals(new byte[]{0x11}, bytes(buf));
        buf.release();
    }

    @Test
    public void testReportSlaveIdResponse() {
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x02, 0x63, (byte) 0xff});
        ReportSlaveIdResponse resp = new ReportSlaveIdResponse(1, data);
        assertEquals(2, resp.getByteCount());
        assertArrayEquals(new byte[]{0x63, (byte) 0xff}, resp.getData());
        ByteBuf out = Unpooled.buffer();
        resp.writePdu(out);
        assertArrayEquals(new byte[]{0x11, 0x02, 0x63, (byte) 0xff}, bytes(out));
        out.release();
    }

    @Test
    public void testReadFileRecordRequest() {
        FileRecord record = new FileRecord(5, 3, 4);
        ReadFileRecordRequest req = new ReadFileRecordRequest(1, Arrays.asList(record));
        assertEquals(0x14, req.getFunctionCode());
        assertEquals(9, req.getPduLength());
        ByteBuf buf = Unpooled.buffer();
        req.writePdu(buf);
        assertArrayEquals(new byte[]{0x14, 0x07, 0x06, 0x00, 0x05, 0x00, 0x03, 0x00, 0x04}, bytes(buf));
        buf.release();
    }

    @Test
    public void testReadFileRecordResponse() {
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x07, 0x06, 0x00, 0x02, 0x12, 0x34, 0x56, 0x78});
        ReadFileRecordResponse resp = new ReadFileRecordResponse(1, data);
        assertEquals(1, resp.getFileData().size());
        assertArrayEquals(new byte[]{0x12, 0x34, 0x56, 0x78}, resp.getFileData().get(0).getData());
        ByteBuf out = Unpooled.buffer();
        resp.writePdu(out);
        assertArrayEquals(new byte[]{0x14, 0x07, 0x06, 0x00, 0x02, 0x12, 0x34, 0x56, 0x78}, bytes(out));
        out.release();
    }

    @Test
    public void testWriteFileRecordRequest() {
        FileRecord record = new FileRecord(5, 3, new byte[]{0x12, 0x34, 0x56, 0x78});
        WriteFileRecordRequest req = new WriteFileRecordRequest(1, Arrays.asList(record));
        assertEquals(0x15, req.getFunctionCode());
        ByteBuf buf = Unpooled.buffer();
        req.writePdu(buf);
        assertArrayEquals(new byte[]{0x15, 0x0b, 0x06, 0x00, 0x05, 0x00, 0x03, 0x00, 0x02, 0x12, 0x34, 0x56,
                0x78}, bytes(buf));
        buf.release();
    }

    @Test
    public void testWriteFileRecordResponse() {
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x0b, 0x06, 0x00, 0x05, 0x00, 0x03, 0x00, 0x02, 0x12,
                0x34, 0x56, 0x78});
        WriteFileRecordResponse resp = new WriteFileRecordResponse(1, data);
        assertEquals(1, resp.getRecords().size());
        FileRecord record = resp.getRecords().get(0);
        assertEquals(5, record.getFileNumber());
        assertEquals(3, record.getRecordNumber());
        assertArrayEquals(new byte[]{0x12, 0x34, 0x56, 0x78}, record.getData());
        ByteBuf out = Unpooled.buffer();
        resp.writePdu(out);
        assertArrayEquals(new byte[]{0x15, 0x0b, 0x06, 0x00, 0x05, 0x00, 0x03, 0x00, 0x02, 0x12, 0x34, 0x56,
                0x78}, bytes(out));
        out.release();
    }

    @Test
    public void testWriteMaskRegisterRequest() {
        WriteMaskRegisterRequest req = new WriteMaskRegisterRequest(1, 0x10, 0x00ff, 0x000f);
        assertEquals(0x16, req.getFunctionCode());
        assertEquals(0x10, req.getOffset());
        assertEquals(0x00ff, req.getAndMask());
        assertEquals(0x000f, req.getOrMask());
        ByteBuf buf = Unpooled.buffer();
        req.writePdu(buf);
        assertArrayEquals(new byte[]{0x16, 0x00, 0x10, 0x00, (byte) 0xff, 0x00, 0x0f}, bytes(buf));
        buf.release();
    }

    @Test
    public void testWriteMaskRegisterResponse() {
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x00, 0x10, 0x00, (byte) 0xff, 0x00, 0x0f});
        WriteMaskRegisterResponse resp = new WriteMaskRegisterResponse(1, data);
        assertEquals(0x10, resp.getOffset());
        assertEquals(0x00ff, resp.getAndMask());
        assertEquals(0x000f, resp.getOrMask());
        ByteBuf out = Unpooled.buffer();
        resp.writePdu(out);
        assertArrayEquals(new byte[]{0x16, 0x00, 0x10, 0x00, (byte) 0xff, 0x00, 0x0f}, bytes(out));
        out.release();
    }

    @Test
    public void testReadWriteMultipleRegistersRequest() {
        ReadWriteMultipleRegistersRequest req = new ReadWriteMultipleRegistersRequest(1, 0, 4, 10,
                new byte[]{0x00, 0x01});
        assertEquals(0x17, req.getFunctionCode());
        assertEquals(4, req.getReadQuantity());
        assertEquals(1, req.getWriteQuantity());
        ByteBuf buf = Unpooled.buffer();
        req.writePdu(buf);
        assertArrayEquals(new byte[]{0x17, 0x00, 0x00, 0x00, 0x04, 0x00, 0x0a, 0x00, 0x01, 0x02, 0x00,
                0x01}, bytes(buf));
        buf.release();
    }

    @Test
    public void testReadWriteMultipleRegistersResponse() {
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x04, 0x00, 0x01, 0x00, 0x02});
        ReadWriteMultipleRegistersResponse resp = new ReadWriteMultipleRegistersResponse(1, data);
        assertEquals(4, resp.getByteCount());
        assertArrayEquals(new byte[]{0x00, 0x01, 0x00, 0x02}, resp.getData());
    }

    @Test
    public void testMessageUtilDispatchExceptionStatus() {
        AbstractModbusResponse resp = MessageUtil.createResponse(3, (byte) 0x07,
                Unpooled.wrappedBuffer(new byte[]{0x05}));
        assertTrue(resp instanceof ReadExceptionStatusResponse);
        assertEquals(5, ((ReadExceptionStatusResponse) resp).getExceptionStatus());
    }

    @Test
    public void testMessageUtilDispatchReportSlaveId() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x11,
                Unpooled.wrappedBuffer(new byte[]{0x02, 0x63, (byte) 0xff}));
        assertTrue(resp instanceof ReportSlaveIdResponse);
    }

    @Test
    public void testMessageUtilDispatchReadFileRecord() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x14,
                Unpooled.wrappedBuffer(new byte[]{0x07, 0x06, 0x00, 0x02, 0x12, 0x34, 0x56, 0x78}));
        assertTrue(resp instanceof ReadFileRecordResponse);
    }

    @Test
    public void testMessageUtilDispatchWriteFileRecord() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x15,
                Unpooled.wrappedBuffer(new byte[]{0x0b, 0x06, 0x00, 0x05, 0x00, 0x03, 0x00, 0x02, 0x12,
                        0x34, 0x56, 0x78}));
        assertTrue(resp instanceof WriteFileRecordResponse);
    }

    @Test
    public void testMessageUtilDispatchMaskRegister() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x16,
                Unpooled.wrappedBuffer(new byte[]{0x00, 0x10, 0x00, (byte) 0xff, 0x00, 0x0f}));
        assertTrue(resp instanceof WriteMaskRegisterResponse);
    }

    @Test
    public void testMessageUtilDispatchReadWriteMultiple() {
        AbstractModbusResponse resp = MessageUtil.createResponse(1, (byte) 0x17,
                Unpooled.wrappedBuffer(new byte[]{0x02, 0x00, 0x05}));
        assertTrue(resp instanceof ReadWriteMultipleRegistersResponse);
    }

    @Test
    public void testFileRecordAccessors() {
        FileRecord record = new FileRecord(5, 3, 4);
        assertEquals(5, record.getFileNumber());
        assertEquals(3, record.getRecordNumber());
        assertEquals(4, record.getRecordLength());
        record.setData(new byte[]{0x01, 0x02, 0x03, 0x04});
        assertEquals(2, record.getRecordLength());
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03, 0x04}, record.getData());
        FileRecord bare = new FileRecord(new byte[]{0x01, 0x02});
        assertEquals(-1, bare.getFileNumber());
        assertEquals(1, bare.getRecordLength());
    }

    @Test
    public void testRequestBasedCreateResponseExceptionStatus() {
        ReadExceptionStatusRequest req = new ReadExceptionStatusRequest(3);
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x07, 0x05});
        AbstractModbusResponse resp = req.createResponse(data);
        assertTrue(resp instanceof ReadExceptionStatusResponse);
        assertEquals(5, ((ReadExceptionStatusResponse) resp).getExceptionStatus());
    }

    @Test
    public void testRequestBasedCreateResponseReportSlaveId() {
        ReportSlaveIdRequest req = new ReportSlaveIdRequest(1);
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x11, 0x02, 0x63, (byte) 0xff});
        assertTrue(req.createResponse(data) instanceof ReportSlaveIdResponse);
    }

    @Test
    public void testRequestBasedCreateResponseReadFileRecord() {
        ReadFileRecordRequest req = new ReadFileRecordRequest(1, Arrays.asList(new FileRecord(5, 3, 2)));
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x14, 0x07, 0x06, 0x00, 0x02, 0x12, 0x34, 0x56, 0x78});
        assertTrue(req.createResponse(data) instanceof ReadFileRecordResponse);
    }

    @Test
    public void testRequestBasedCreateResponseWriteFileRecord() {
        WriteFileRecordRequest req = new WriteFileRecordRequest(1, Arrays.asList(new FileRecord(5, 3, 4)));
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x15, 0x0b, 0x06, 0x00, 0x05, 0x00, 0x03, 0x00, 0x02,
                0x12, 0x34, 0x56, 0x78});
        assertTrue(req.createResponse(data) instanceof WriteFileRecordResponse);
    }

    @Test
    public void testRequestBasedCreateResponseMaskRegister() {
        WriteMaskRegisterRequest req = new WriteMaskRegisterRequest(1, 0x10, 0x00ff, 0x000f);
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x16, 0x00, 0x10, 0x00, (byte) 0xff, 0x00, 0x0f});
        AbstractModbusResponse resp = req.createResponse(data);
        assertTrue(resp instanceof WriteMaskRegisterResponse);
        assertEquals(0x0f, ((WriteMaskRegisterResponse) resp).getOrMask());
    }

    @Test
    public void testRequestBasedCreateResponseReadWriteMultiple() {
        ReadWriteMultipleRegistersRequest req = new ReadWriteMultipleRegistersRequest(1, 0, 4, 10,
                new byte[]{0x00, 0x01});
        ByteBuf data = Unpooled.wrappedBuffer(new byte[]{0x17, 0x02, 0x00, 0x05});
        assertTrue(req.createResponse(data) instanceof ReadWriteMultipleRegistersResponse);
    }

    @Test
    public void testAccessorsAndNoOpReadPdu() {
        ReadExceptionStatusRequest esr = new ReadExceptionStatusRequest(1);
        esr.readPdu(Unpooled.EMPTY_BUFFER);
        ReadExceptionStatusResponse es = new ReadExceptionStatusResponse(3, 0x05);
        es.readPdu(Unpooled.EMPTY_BUFFER);
        assertEquals(5, es.getExceptionStatus());

        ReportSlaveIdRequest rsi = new ReportSlaveIdRequest(1);
        rsi.readPdu(Unpooled.EMPTY_BUFFER);
        ReportSlaveIdResponse rr = new ReportSlaveIdResponse(1, new byte[]{0x01, 0x02});
        rr.readPdu(Unpooled.wrappedBuffer(new byte[]{0x02, 0x01, 0x02}));
        assertEquals(2, rr.getByteCount());
        assertArrayEquals(new byte[]{0x01, 0x02}, rr.getData());

        WriteMaskRegisterRequest wreq = new WriteMaskRegisterRequest(1, 0x10, 0x00ff, 0x000f);
        wreq.readPdu(Unpooled.EMPTY_BUFFER);
        assertEquals(0x10, wreq.getOffset());
        assertEquals(0x00ff, wreq.getAndMask());
        assertEquals(0x000f, wreq.getOrMask());
        WriteMaskRegisterResponse wr = new WriteMaskRegisterResponse(1, 0x10, 0x00ff, 0x000f);
        wr.readPdu(Unpooled.EMPTY_BUFFER);
        assertEquals(0x00ff, wr.getAndMask());

        ReadFileRecordRequest rfr = new ReadFileRecordRequest(1, Arrays.asList(new FileRecord(5, 3, 4)));
        rfr.readPdu(Unpooled.EMPTY_BUFFER);
        assertEquals(1, rfr.getRecords().size());

        WriteFileRecordRequest wfr = new WriteFileRecordRequest(1,
                Arrays.asList(new FileRecord(5, 3, new byte[]{0x01, 0x02, 0x03, 0x04})));
        wfr.readPdu(Unpooled.EMPTY_BUFFER);
        assertEquals(1, wfr.getRecords().size());

        ReadWriteMultipleRegistersRequest rw = new ReadWriteMultipleRegistersRequest(1, 0, 4, 10,
                new byte[]{0x00, 0x01});
        rw.readPdu(Unpooled.EMPTY_BUFFER);
        assertEquals(0, rw.getReadStartOffset());
        assertEquals(4, rw.getReadQuantity());
        assertEquals(10, rw.getWriteStartOffset());
        assertEquals(1, rw.getWriteQuantity());
        assertArrayEquals(new byte[]{0x00, 0x01}, rw.getWriteData());

        ReadWriteMultipleRegistersResponse rwr = new ReadWriteMultipleRegistersResponse(1, 2,
                new byte[]{0x00, 0x01});
        assertEquals(2, rwr.getByteCount());
        assertTrue(rwr.toString().startsWith("ReadWriteMultipleRegistersResponse"));
        assertTrue(wreq.toString().startsWith("WriteMaskRegisterRequest"));
    }
}
