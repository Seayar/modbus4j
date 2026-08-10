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
package com.seayar.modbus4j.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class BitSetUtil {
    private BitSetUtil() {}

    public static BitSet copy(BitSet b) {
        return (BitSet) b.clone();
    }

    public static BitSet getRandomBits(int n, Random random) {
        BitSet bits = new BitSet(n);
        for (int i = 0; i < n; i++)
            bits.set(i, random.nextBoolean());
        return bits;
    }

    public static String toString(BitSet bits, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++)
            sb.append(bits.get(i) ? "1 " : "0 ");
        return sb.toString();
    }

    public static String toString(BitSet bits) {
        return toString(bits, bits.length());
    }

    public static BitSet fromList(List<Boolean> list) {
        BitSet bitset = new BitSet(list.size());
        for (int i = 0; i < list.size(); i++)
            bitset.set(i, list.get(i));
        return bitset;
    }

    public static BitSet fromArray(boolean[] array) {
        BitSet bitset = new BitSet(array.length);
        for (int i = 0; i < array.length; i++)
            bitset.set(i, array[i]);
        return bitset;
    }

    public static boolean[] toArray(BitSet bitset, int length) {
        if (length < 0)
            throw new IllegalArgumentException("Size of array must not be negative but was " + length);
        boolean[] array = new boolean[length];
        for (int i = 0; i < length; i++)
            array[i] = bitset.get(i);
        return array;
    }

    public static List<Boolean> toList(BitSet bitset, int n) {
        if (n < 0)
            throw new IllegalArgumentException("Size of list must not be negative but was " + n);
        List<Boolean> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
            list.add(bitset.get(i));
        return list;
    }
}
