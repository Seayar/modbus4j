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
package io.github.seayar.modbus4j;

import io.github.seayar.modbus4j.base.DataType;
import io.github.seayar.modbus4j.base.RegisterRange;
import io.github.seayar.modbus4j.exception.ModbusCodeException;
import io.github.seayar.modbus4j.exception.ModbusTransportException;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.locator.BaseLocator;
import io.github.seayar.modbus4j.locator.BatchRead;
import io.github.seayar.modbus4j.locator.BatchResults;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ModbusMasterTest {

    private ModbusMaster createMaster() {
        return ModbusMasterTestSupport.createMaster();
    }

    @Test(timeout = 15000)
    public void testGetValueHoldingRegister() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED);
        Number value = master.getValue(locator);
        assertEquals(101, value.intValue());
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testGetValueFloat() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT);
        Number value = master.getValue(locator);
        assertNotNull(value);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testGetValueCoil() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Boolean> locator = BaseLocator.coilStatus(1, 0);
        Boolean value = master.getValue(locator);
        assertTrue(value);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testSetValueRegister() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED);
        master.setValue(locator, 42);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testSetValueFloat() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT);
        master.setValue(locator, 3.14f);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testSetValueString() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<String> locator = BaseLocator.holdingRegisterString(1, 100, DataType.CHAR, 4);
        master.setValue(locator, "abcd");
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testBatchRead() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchRead<String> batch = new BatchRead<>();
        batch.addLocator("a", BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator("b", BaseLocator.holdingRegister(1, 105, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator("c", BaseLocator.coilStatus(1, 0));
        BatchResults<String> results = master.send(batch);
        assertEquals(101, ((Number) results.getValue("a")).intValue());
        assertEquals(106, ((Number) results.getValue("b")).intValue());
        assertTrue((Boolean) results.getValue("c"));
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testBatchReadCancel() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchRead<String> batch = new BatchRead<>();
        batch.setCancel(true);
        batch.addLocator("a", BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED));
        BatchResults<String> results = master.send(batch);
        assertTrue(results.isEmpty());
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testBatchReadRetry() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchRead<String> batch = new BatchRead<>();
        batch.addLocator("a", BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED));
        BatchResults<String> results = master.send(batch, true);
        assertEquals(101, ((Number) results.getValue("a")).intValue());
        master.destroy();
    }

    @Test(timeout = 15000, expected = ModbusTransportException.class)
    public void testUnsupportedLocator() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED);
        BaseLocator<Number> bogus = new BaseLocator<Number>(1, RegisterRange.HOLDING_REGISTER, 100) {
            @Override
            public int getDataType() {
                return 999;
            }

            @Override
            public int getRegisterCount() {
                return 1;
            }

            @Override
            public Number bytesToValueRealOffset(byte[] data, int offset) {
                return null;
            }

            @Override
            public short[] valueToShorts(Number value) {
                return new short[0];
            }
        };
        master.getValue(bogus);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testGetValueUnsupportedRange() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.inputRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED);
        Number value = master.getValue(locator);
        assertNotNull(value);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testSetValueRegisterWithFloat() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED);
        master.setValue(locator, 42);
        master.destroy();
    }

    @Test(timeout = 15000, expected = ModbusTransportException.class)
    public void testSetValueInputRegister() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.inputRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED);
        master.setValue(locator, 42);
        master.destroy();
    }

    @Test(timeout = 15000, expected = ModbusTransportException.class)
    public void testSetValueInputStatus() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Boolean> locator = BaseLocator.inputStatus(1, 0);
        master.setValue(locator, true);
        master.destroy();
    }

    @Test(timeout = 15000, expected = ModbusTransportException.class)
    public void testSetValueUnsupportedType() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED);
        master.setValue(locator, null);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testSetValueBit() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Boolean> locator = BaseLocator.holdingRegisterBit(1, 100, 3);
        master.setValue(locator, true);
        master.setValue(locator, false);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testSetValueStringInput() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<String> locator = BaseLocator.inputRegisterString(1, 100, DataType.CHAR, 2);
        try {
            master.setValue(locator, "ab");
            org.junit.Assert.fail("expected exception");
        } catch (ModbusTransportException expected) {
        }
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testBatchReadExceptionInResults() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchRead<String> batch = new BatchRead<>();
        batch.setExceptionsInResults(true);
        batch.addLocator("ok", BaseLocator.holdingRegister(1, 100, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator("bad", BaseLocator.holdingRegister(0x7f, 100, DataType.TWO_BYTE_INT_UNSIGNED));
        BatchResults<String> results = master.send(batch);
        assertEquals(101, ((Number) results.getValue("ok")).intValue());
        assertTrue(results.isError("bad"));
        master.destroy();
    }

    @Test(timeout = 15000, expected = ModbusCodeException.class)
    public void testBatchReadExceptionThrows() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchRead<String> batch = new BatchRead<>();
        batch.setSplitOnException(false);
        batch.addLocator("bad", BaseLocator.holdingRegister(0x7f, 100, DataType.TWO_BYTE_INT_UNSIGNED));
        master.send(batch);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testGetValueException() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = BaseLocator.holdingRegister(0x7f, 100, DataType.TWO_BYTE_INT_UNSIGNED);
        try {
            master.getValue(locator);
            org.junit.Assert.fail("expected exception");
        } catch (ModbusCodeException expected) {
        }
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testInputStatusGetValue() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Boolean> locator = BaseLocator.inputStatus(1, 0);
        assertTrue(master.getValue(locator));
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testGetValueUnsupportedNumericRange() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Number> locator = new io.github.seayar.modbus4j.locator.NumericLocator(1,
                RegisterRange.COIL_STATUS, 0, DataType.TWO_BYTE_INT_UNSIGNED);
        try {
            master.getValue(locator);
            org.junit.Assert.fail("expected exception");
        } catch (ModbusTransportException expected) {
        }
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testGetValueRegisterBit() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BaseLocator<Boolean> locator = BaseLocator.holdingRegisterBit(1, 100, 0);
        assertNotNull(master.getValue(locator));
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testSetValueCoil() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        master.setValue(BaseLocator.coilStatus(1, 0), true);
        master.destroy();
    }

    @Test(timeout = 15000)
    public void testBatchReadInputs() throws Exception {
        ModbusMaster master = createMaster();
        master.init();
        BatchRead<String> batch = new BatchRead<>();
        batch.addLocator("in", BaseLocator.inputStatus(1, 0));
        batch.addLocator("reg", BaseLocator.inputRegister(1, 10, DataType.TWO_BYTE_INT_UNSIGNED));
        BatchResults<String> results = master.send(batch);
        assertNotNull(results.getValue("in"));
        assertNotNull(results.getValue("reg"));
        master.destroy();
    }
}
