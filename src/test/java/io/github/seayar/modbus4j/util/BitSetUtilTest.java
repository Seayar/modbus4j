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
package io.github.seayar.modbus4j.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class BitSetUtilTest {

    @Test
    public void testCopy() {
        BitSet bits = new BitSet();
        bits.set(0);
        bits.set(3);
        BitSet copy = BitSetUtil.copy(bits);
        assertNotSame(bits, copy);
        assertEquals(bits, copy);
    }

    @Test
    public void testGetRandomBits() {
        BitSet bits = BitSetUtil.getRandomBits(10, new Random(1));
        assertEquals(10, bits.length());
    }

    @Test
    public void testToStringN() {
        BitSet bits = new BitSet();
        bits.set(0);
        bits.set(2);
        assertEquals("1 0 1 0 ", BitSetUtil.toString(bits, 4));
    }

    @Test
    public void testFromList() {
        List<Boolean> list = Arrays.asList(true, false, true);
        BitSet bits = BitSetUtil.fromList(list);
        assertTrue(bits.get(0));
        assertFalse(bits.get(1));
        assertTrue(bits.get(2));
    }

    @Test
    public void testFromArray() {
        BitSet bits = BitSetUtil.fromArray(new boolean[]{true, false, true});
        assertTrue(bits.get(0));
        assertFalse(bits.get(1));
        assertTrue(bits.get(2));
    }

    @Test
    public void testToArray() {
        BitSet bits = new BitSet();
        bits.set(0);
        bits.set(2);
        assertArrayEquals(new boolean[]{true, false, true}, BitSetUtil.toArray(bits, 3));
    }

    @Test
    public void testToArrayNegative() {
        try {
            BitSetUtil.toArray(new BitSet(), -1);
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("expected exception");
    }

    @Test
    public void testToList() {
        BitSet bits = new BitSet();
        bits.set(0);
        List<Boolean> list = BitSetUtil.toList(bits, 3);
        assertEquals(Arrays.asList(true, false, false), list);
    }

    @Test
    public void testToListNegative() {
        try {
            BitSetUtil.toList(new BitSet(), -1);
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("expected exception");
    }
}
