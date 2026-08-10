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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BatchResults<K> {
    private final Map<K, Object> data = new HashMap<>();
    private final Set<K> errors = new HashSet<>();

    public Object getValue(K key) {
        return data.get(key);
    }

    public boolean isError(K key) {
        return errors.contains(key);
    }

    public Set<K> getErrors() {
        return errors;
    }

    public void setValue(K key, Object value) {
        data.put(key, value);
    }

    public void setError(K key) {
        data.put(key, null);
        errors.add(key);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public String toString() {
        return "BatchResults(" + data + ")";
    }
}
