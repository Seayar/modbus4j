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
package io.github.seayar.modbus4j.base;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataTypeTest {

    @Test
    public void testGetRegisterCount() {
        assertEquals(1, DataType.getRegisterCount(DataType.BINARY));
        assertEquals(1, DataType.getRegisterCount(DataType.TWO_BYTE_INT_UNSIGNED));
        assertEquals(1, DataType.getRegisterCount(DataType.TWO_BYTE_INT_SIGNED));
        assertEquals(1, DataType.getRegisterCount(DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED));
        assertEquals(1, DataType.getRegisterCount(DataType.TWO_BYTE_INT_SIGNED_SWAPPED));
        assertEquals(1, DataType.getRegisterCount(DataType.TWO_BYTE_BCD));
        assertEquals(1, DataType.getRegisterCount(DataType.ONE_BYTE_INT_UNSIGNED_LOWER));
        assertEquals(1, DataType.getRegisterCount(DataType.ONE_BYTE_INT_UNSIGNED_UPPER));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_INT_UNSIGNED));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_INT_SIGNED));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_INT_SIGNED_SWAPPED));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_FLOAT));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_FLOAT_SWAPPED));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_BCD));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_BCD_SWAPPED));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_MOD_10K));
        assertEquals(2, DataType.getRegisterCount(DataType.FOUR_BYTE_MOD_10K_SWAPPED));
        assertEquals(3, DataType.getRegisterCount(DataType.SIX_BYTE_MOD_10K));
        assertEquals(3, DataType.getRegisterCount(DataType.SIX_BYTE_MOD_10K_SWAPPED));
        assertEquals(4, DataType.getRegisterCount(DataType.EIGHT_BYTE_INT_UNSIGNED));
        assertEquals(4, DataType.getRegisterCount(DataType.EIGHT_BYTE_INT_SIGNED));
        assertEquals(4, DataType.getRegisterCount(DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED));
        assertEquals(4, DataType.getRegisterCount(DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED));
        assertEquals(4, DataType.getRegisterCount(DataType.EIGHT_BYTE_FLOAT));
        assertEquals(4, DataType.getRegisterCount(DataType.EIGHT_BYTE_FLOAT_SWAPPED));
        assertEquals(4, DataType.getRegisterCount(DataType.EIGHT_BYTE_MOD_10K));
        assertEquals(4, DataType.getRegisterCount(DataType.EIGHT_BYTE_MOD_10K_SWAPPED));
        assertEquals(0, DataType.getRegisterCount(999));
    }

    @Test
    public void testGetJavaType() {
        assertEquals(Integer.class, DataType.getJavaType(DataType.TWO_BYTE_INT_UNSIGNED));
        assertEquals(Boolean.class, DataType.getJavaType(DataType.BINARY));
        assertEquals(Short.class, DataType.getJavaType(DataType.TWO_BYTE_INT_SIGNED));
        assertEquals(Long.class, DataType.getJavaType(DataType.FOUR_BYTE_INT_UNSIGNED));
        assertEquals(Integer.class, DataType.getJavaType(DataType.FOUR_BYTE_INT_SIGNED));
        assertEquals(Float.class, DataType.getJavaType(DataType.FOUR_BYTE_FLOAT));
        assertEquals(BigInteger.class, DataType.getJavaType(DataType.FOUR_BYTE_MOD_10K));
        assertEquals(Long.class, DataType.getJavaType(DataType.EIGHT_BYTE_INT_SIGNED));
        assertEquals(Double.class, DataType.getJavaType(DataType.EIGHT_BYTE_FLOAT));
        assertEquals(String.class, DataType.getJavaType(DataType.CHAR));
        assertEquals(String.class, DataType.getJavaType(DataType.VARCHAR));
        assertEquals(Integer.class, DataType.getJavaType(DataType.ONE_BYTE_INT_UNSIGNED_LOWER));
        assertEquals(Integer.class, DataType.getJavaType(DataType.ONE_BYTE_INT_UNSIGNED_UPPER));
        assertEquals(Integer.class, DataType.getJavaType(DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED));
        assertEquals(Short.class, DataType.getJavaType(DataType.TWO_BYTE_INT_SIGNED_SWAPPED));
        assertEquals(Short.class, DataType.getJavaType(DataType.TWO_BYTE_BCD));
        assertEquals(Long.class, DataType.getJavaType(DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED));
        assertEquals(Integer.class, DataType.getJavaType(DataType.FOUR_BYTE_INT_SIGNED_SWAPPED));
        assertEquals(Integer.class, DataType.getJavaType(DataType.FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED));
        assertEquals(Long.class, DataType.getJavaType(DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED));
        assertEquals(Float.class, DataType.getJavaType(DataType.FOUR_BYTE_FLOAT_SWAPPED));
        assertEquals(Float.class, DataType.getJavaType(DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED));
        assertEquals(Integer.class, DataType.getJavaType(DataType.FOUR_BYTE_BCD));
        assertEquals(Integer.class, DataType.getJavaType(DataType.FOUR_BYTE_BCD_SWAPPED));
        assertEquals(BigInteger.class, DataType.getJavaType(DataType.SIX_BYTE_MOD_10K));
        assertEquals(BigInteger.class, DataType.getJavaType(DataType.EIGHT_BYTE_MOD_10K));
        assertEquals(BigInteger.class, DataType.getJavaType(DataType.FOUR_BYTE_MOD_10K_SWAPPED));
        assertEquals(BigInteger.class, DataType.getJavaType(DataType.SIX_BYTE_MOD_10K_SWAPPED));
        assertEquals(BigInteger.class, DataType.getJavaType(DataType.EIGHT_BYTE_MOD_10K_SWAPPED));
        assertEquals(BigInteger.class, DataType.getJavaType(DataType.EIGHT_BYTE_INT_UNSIGNED));
        assertEquals(BigInteger.class, DataType.getJavaType(DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED));
        assertEquals(Long.class, DataType.getJavaType(DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED));
        assertEquals(Double.class, DataType.getJavaType(DataType.EIGHT_BYTE_FLOAT_SWAPPED));
        assertNull(DataType.getJavaType(999));
    }
}
