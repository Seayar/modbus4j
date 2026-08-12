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

import java.math.BigInteger;

public class DataType {
    public static final int BINARY = 1;
    public static final int TWO_BYTE_INT_UNSIGNED = 2;
    public static final int TWO_BYTE_INT_SIGNED = 3;
    public static final int FOUR_BYTE_INT_UNSIGNED = 4;
    public static final int FOUR_BYTE_INT_SIGNED = 5;
    public static final int FOUR_BYTE_INT_UNSIGNED_SWAPPED = 6;
    public static final int FOUR_BYTE_INT_SIGNED_SWAPPED = 7;
    public static final int FOUR_BYTE_FLOAT = 8;
    public static final int FOUR_BYTE_FLOAT_SWAPPED = 9;
    public static final int EIGHT_BYTE_INT_UNSIGNED = 10;
    public static final int EIGHT_BYTE_INT_SIGNED = 11;
    public static final int EIGHT_BYTE_INT_UNSIGNED_SWAPPED = 12;
    public static final int EIGHT_BYTE_INT_SIGNED_SWAPPED = 13;
    public static final int EIGHT_BYTE_FLOAT = 14;
    public static final int EIGHT_BYTE_FLOAT_SWAPPED = 15;
    public static final int TWO_BYTE_BCD = 16;
    public static final int FOUR_BYTE_BCD = 17;
    public static final int CHAR = 18;
    public static final int VARCHAR = 19;
    public static final int FOUR_BYTE_BCD_SWAPPED = 20;
    public static final int FOUR_BYTE_FLOAT_SWAPPED_INVERTED = 21;
    public static final int TWO_BYTE_INT_UNSIGNED_SWAPPED = 22;
    public static final int TWO_BYTE_INT_SIGNED_SWAPPED = 23;
    public static final int FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED = 24;
    public static final int FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED = 25;
    public static final int FOUR_BYTE_MOD_10K = 26;
    public static final int SIX_BYTE_MOD_10K = 27;
    public static final int EIGHT_BYTE_MOD_10K = 28;
    public static final int FOUR_BYTE_MOD_10K_SWAPPED = 29;
    public static final int SIX_BYTE_MOD_10K_SWAPPED = 30;
    public static final int EIGHT_BYTE_MOD_10K_SWAPPED = 31;
    public static final int ONE_BYTE_INT_UNSIGNED_LOWER = 32;
    public static final int ONE_BYTE_INT_UNSIGNED_UPPER = 33;

    public static final int TWO_BYTE_INT_UNSIGNED_AB = 100;
    public static final int TWO_BYTE_INT_UNSIGNED_BA = 101;
    public static final int TWO_BYTE_INT_SIGNED_AB = 102;
    public static final int TWO_BYTE_INT_SIGNED_BA = 103;

    public static final int FOUR_BYTE_INT_UNSIGNED_ABCD = 104;
    public static final int FOUR_BYTE_INT_UNSIGNED_BADC = 105;
    public static final int FOUR_BYTE_INT_UNSIGNED_CDAB = 106;
    public static final int FOUR_BYTE_INT_UNSIGNED_DCBA = 107;
    public static final int FOUR_BYTE_INT_SIGNED_ABCD = 108;
    public static final int FOUR_BYTE_INT_SIGNED_BADC = 109;
    public static final int FOUR_BYTE_INT_SIGNED_CDAB = 110;
    public static final int FOUR_BYTE_INT_SIGNED_DCBA = 111;

    public static final int FOUR_BYTE_FLOAT_ABCD = 112;
    public static final int FOUR_BYTE_FLOAT_BADC = 113;
    public static final int FOUR_BYTE_FLOAT_CDAB = 114;
    public static final int FOUR_BYTE_FLOAT_DCBA = 115;

    public static final int EIGHT_BYTE_INT_UNSIGNED_ABCD = 116;
    public static final int EIGHT_BYTE_INT_UNSIGNED_BADC = 117;
    public static final int EIGHT_BYTE_INT_UNSIGNED_CDAB = 118;
    public static final int EIGHT_BYTE_INT_UNSIGNED_DCBA = 119;
    public static final int EIGHT_BYTE_INT_SIGNED_ABCD = 120;
    public static final int EIGHT_BYTE_INT_SIGNED_BADC = 121;
    public static final int EIGHT_BYTE_INT_SIGNED_CDAB = 122;
    public static final int EIGHT_BYTE_INT_SIGNED_DCBA = 123;

    public static final int EIGHT_BYTE_FLOAT_ABCD = 124;
    public static final int EIGHT_BYTE_FLOAT_BADC = 125;
    public static final int EIGHT_BYTE_FLOAT_CDAB = 126;
    public static final int EIGHT_BYTE_FLOAT_DCBA = 127;

    public static int getRegisterCount(int id) {
        switch (id) {
            case BINARY:
            case TWO_BYTE_INT_UNSIGNED:
            case TWO_BYTE_INT_SIGNED:
            case TWO_BYTE_INT_UNSIGNED_SWAPPED:
            case TWO_BYTE_INT_SIGNED_SWAPPED:
            case TWO_BYTE_BCD:
            case ONE_BYTE_INT_UNSIGNED_LOWER:
            case ONE_BYTE_INT_UNSIGNED_UPPER:
            case TWO_BYTE_INT_UNSIGNED_AB:
            case TWO_BYTE_INT_UNSIGNED_BA:
            case TWO_BYTE_INT_SIGNED_AB:
            case TWO_BYTE_INT_SIGNED_BA:
                return 1;
            case FOUR_BYTE_INT_UNSIGNED:
            case FOUR_BYTE_INT_SIGNED:
            case FOUR_BYTE_INT_UNSIGNED_SWAPPED:
            case FOUR_BYTE_INT_SIGNED_SWAPPED:
            case FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED:
            case FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED:
            case FOUR_BYTE_FLOAT:
            case FOUR_BYTE_FLOAT_SWAPPED:
            case FOUR_BYTE_FLOAT_SWAPPED_INVERTED:
            case FOUR_BYTE_BCD:
            case FOUR_BYTE_BCD_SWAPPED:
            case FOUR_BYTE_MOD_10K:
            case FOUR_BYTE_MOD_10K_SWAPPED:
            case FOUR_BYTE_INT_UNSIGNED_ABCD:
            case FOUR_BYTE_INT_UNSIGNED_BADC:
            case FOUR_BYTE_INT_UNSIGNED_CDAB:
            case FOUR_BYTE_INT_UNSIGNED_DCBA:
            case FOUR_BYTE_INT_SIGNED_ABCD:
            case FOUR_BYTE_INT_SIGNED_BADC:
            case FOUR_BYTE_INT_SIGNED_CDAB:
            case FOUR_BYTE_INT_SIGNED_DCBA:
            case FOUR_BYTE_FLOAT_ABCD:
            case FOUR_BYTE_FLOAT_BADC:
            case FOUR_BYTE_FLOAT_CDAB:
            case FOUR_BYTE_FLOAT_DCBA:
                return 2;
            case SIX_BYTE_MOD_10K:
            case SIX_BYTE_MOD_10K_SWAPPED:
                return 3;
            case EIGHT_BYTE_INT_UNSIGNED:
            case EIGHT_BYTE_INT_SIGNED:
            case EIGHT_BYTE_INT_UNSIGNED_SWAPPED:
            case EIGHT_BYTE_INT_SIGNED_SWAPPED:
            case EIGHT_BYTE_FLOAT:
            case EIGHT_BYTE_FLOAT_SWAPPED:
            case EIGHT_BYTE_MOD_10K:
            case EIGHT_BYTE_MOD_10K_SWAPPED:
            case EIGHT_BYTE_INT_UNSIGNED_ABCD:
            case EIGHT_BYTE_INT_UNSIGNED_BADC:
            case EIGHT_BYTE_INT_UNSIGNED_CDAB:
            case EIGHT_BYTE_INT_UNSIGNED_DCBA:
            case EIGHT_BYTE_INT_SIGNED_ABCD:
            case EIGHT_BYTE_INT_SIGNED_BADC:
            case EIGHT_BYTE_INT_SIGNED_CDAB:
            case EIGHT_BYTE_INT_SIGNED_DCBA:
            case EIGHT_BYTE_FLOAT_ABCD:
            case EIGHT_BYTE_FLOAT_BADC:
            case EIGHT_BYTE_FLOAT_CDAB:
            case EIGHT_BYTE_FLOAT_DCBA:
                return 4;
        }
        return 0;
    }

    public static Class<?> getJavaType(int id) {
        switch (id) {
            case ONE_BYTE_INT_UNSIGNED_LOWER:
            case ONE_BYTE_INT_UNSIGNED_UPPER:
            case TWO_BYTE_INT_UNSIGNED:
            case TWO_BYTE_INT_UNSIGNED_SWAPPED:
            case TWO_BYTE_INT_UNSIGNED_AB:
            case TWO_BYTE_INT_UNSIGNED_BA:
                return Integer.class;
            case BINARY:
                return Boolean.class;
            case TWO_BYTE_INT_SIGNED:
            case TWO_BYTE_INT_SIGNED_SWAPPED:
            case TWO_BYTE_INT_SIGNED_AB:
            case TWO_BYTE_INT_SIGNED_BA:
            case TWO_BYTE_BCD:
                return Short.class;
            case FOUR_BYTE_INT_UNSIGNED:
            case FOUR_BYTE_INT_UNSIGNED_SWAPPED:
            case FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED:
            case FOUR_BYTE_INT_UNSIGNED_ABCD:
            case FOUR_BYTE_INT_UNSIGNED_BADC:
            case FOUR_BYTE_INT_UNSIGNED_CDAB:
            case FOUR_BYTE_INT_UNSIGNED_DCBA:
                return Long.class;
            case FOUR_BYTE_INT_SIGNED:
            case FOUR_BYTE_INT_SIGNED_SWAPPED:
            case FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED:
            case FOUR_BYTE_INT_SIGNED_ABCD:
            case FOUR_BYTE_INT_SIGNED_BADC:
            case FOUR_BYTE_INT_SIGNED_CDAB:
            case FOUR_BYTE_INT_SIGNED_DCBA:
            case FOUR_BYTE_BCD:
            case FOUR_BYTE_BCD_SWAPPED:
                return Integer.class;
            case FOUR_BYTE_FLOAT:
            case FOUR_BYTE_FLOAT_SWAPPED:
            case FOUR_BYTE_FLOAT_SWAPPED_INVERTED:
            case FOUR_BYTE_FLOAT_ABCD:
            case FOUR_BYTE_FLOAT_BADC:
            case FOUR_BYTE_FLOAT_CDAB:
            case FOUR_BYTE_FLOAT_DCBA:
                return Float.class;
            case EIGHT_BYTE_INT_UNSIGNED:
            case EIGHT_BYTE_INT_UNSIGNED_SWAPPED:
            case EIGHT_BYTE_INT_UNSIGNED_ABCD:
            case EIGHT_BYTE_INT_UNSIGNED_BADC:
            case EIGHT_BYTE_INT_UNSIGNED_CDAB:
            case EIGHT_BYTE_INT_UNSIGNED_DCBA:
            case FOUR_BYTE_MOD_10K:
            case SIX_BYTE_MOD_10K:
            case EIGHT_BYTE_MOD_10K:
            case FOUR_BYTE_MOD_10K_SWAPPED:
            case SIX_BYTE_MOD_10K_SWAPPED:
            case EIGHT_BYTE_MOD_10K_SWAPPED:
                return BigInteger.class;
            case EIGHT_BYTE_INT_SIGNED:
            case EIGHT_BYTE_INT_SIGNED_SWAPPED:
            case EIGHT_BYTE_INT_SIGNED_ABCD:
            case EIGHT_BYTE_INT_SIGNED_BADC:
            case EIGHT_BYTE_INT_SIGNED_CDAB:
            case EIGHT_BYTE_INT_SIGNED_DCBA:
                return Long.class;
            case EIGHT_BYTE_FLOAT:
            case EIGHT_BYTE_FLOAT_SWAPPED:
            case EIGHT_BYTE_FLOAT_ABCD:
            case EIGHT_BYTE_FLOAT_BADC:
            case EIGHT_BYTE_FLOAT_CDAB:
            case EIGHT_BYTE_FLOAT_DCBA:
                return Double.class;
            case CHAR:
            case VARCHAR:
                return String.class;
        }
        return null;
    }
}
