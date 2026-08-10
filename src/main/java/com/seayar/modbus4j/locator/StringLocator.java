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
package com.seayar.modbus4j.locator;

import com.seayar.modbus4j.base.DataType;
import com.seayar.modbus4j.base.RegisterRange;

import java.nio.charset.Charset;

public class StringLocator extends BaseLocator<String> {
    public static final Charset ASCII = Charset.forName("ASCII");

    private final int dataType;
    private final int registerCount;
    private final Charset charset;

    public StringLocator(int slaveId, int range, int offset, int dataType, int registerCount) {
        this(slaveId, range, offset, dataType, registerCount, ASCII);
    }

    public StringLocator(int slaveId, int range, int offset, int dataType, int registerCount, Charset charset) {
        super(slaveId, range, offset);
        this.dataType = dataType;
        this.registerCount = registerCount;
        this.charset = charset;
        validate(registerCount);
    }

    @Override
    public int getDataType() {
        return dataType;
    }

    @Override
    public int getRegisterCount() {
        return registerCount;
    }

    @Override
    public String bytesToValueRealOffset(byte[] data, int offset) {
        offset *= 2;
        int length = registerCount * 2;
        if (dataType == DataType.CHAR)
            return new String(data, offset, length, charset);
        if (dataType == DataType.VARCHAR) {
            int nullPos = -1;
            for (int i = offset; i < offset + length; i++) {
                if (data[i] == 0) {
                    nullPos = i;
                    break;
                }
            }
            if (nullPos == -1)
                return new String(data, offset, length, charset);
            return new String(data, offset, nullPos - offset, charset);
        }
        throw new IllegalArgumentException("Unsupported data type: " + dataType);
    }

    @Override
    public short[] valueToShorts(String value) {
        short[] result = new short[registerCount];
        int resultByteLen = registerCount * 2;
        int length;
        if (value != null) {
            byte[] bytes = value.getBytes(charset);
            length = Math.min(resultByteLen, bytes.length);
            for (int i = 0; i < length; i++)
                setByte(result, i, bytes[i] & 0xff);
        } else
            length = 0;
        if (dataType == DataType.CHAR) {
            for (int i = length; i < resultByteLen; i++)
                setByte(result, i, 0x20);
        } else if (dataType == DataType.VARCHAR) {
            if (length >= resultByteLen)
                result[registerCount - 1] &= 0xff00;
            else {
                for (int i = length; i < resultByteLen; i++)
                    setByte(result, i, 0);
            }
        } else
            throw new IllegalArgumentException("Unsupported data type: " + dataType);
        return result;
    }

    private void setByte(short[] s, int byteIndex, int value) {
        if (byteIndex % 2 == 0)
            s[byteIndex / 2] |= value << 8;
        else
            s[byteIndex / 2] |= value;
    }

    @Override
    public String toString() {
        return "StringLocator(slaveId=" + getSlaveId() + ", range=" + range + ", offset=" + offset + ", dataType="
                + dataType + ", registerCount=" + registerCount + ", charset=" + charset + ")";
    }
}
