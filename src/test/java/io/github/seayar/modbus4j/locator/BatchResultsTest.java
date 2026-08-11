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
package io.github.seayar.modbus4j.locator;

import io.github.seayar.modbus4j.base.DataType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BatchResultsTest {

    @Test
    public void testSetAndGet() {
        BatchResults<String> results = new BatchResults<>();
        results.setValue("a", 42);
        assertEquals(42, results.getValue("a"));
        assertFalse(results.isError("a"));
        assertTrue(results.getErrors().isEmpty());
    }

    @Test
    public void testSetError() {
        BatchResults<String> results = new BatchResults<>();
        results.setValue("a", 1);
        results.setError("b");
        assertTrue(results.isError("b"));
        assertNull(results.getValue("b"));
        assertEquals(1, results.getErrors().size());
        assertFalse(results.isEmpty());
    }

    @Test
    public void testEmpty() {
        BatchResults<String> results = new BatchResults<>();
        assertTrue(results.isEmpty());
        assertNull(results.getValue("missing"));
    }

    @Test
    public void testToString() {
        BatchResults<String> results = new BatchResults<>();
        results.setValue("a", 42);
        assertEquals("BatchResults({a=42})", results.toString());
    }
}
