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

public class SlaveAndRange {
    private final int slaveId;
    private final int range;

    public SlaveAndRange(int slaveId, int range) {
        this.slaveId = slaveId;
        this.range = range;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public int getRange() {
        return range;
    }

    @Override
    public int hashCode() {
        int result = slaveId;
        result = 31 * result + range;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SlaveAndRange)) return false;
        SlaveAndRange other = (SlaveAndRange) obj;
        return slaveId == other.slaveId && range == other.range;
    }

    @Override
    public String toString() {
        return "SlaveAndRange(slaveId=" + slaveId + ", range=" + range + ")";
    }
}
