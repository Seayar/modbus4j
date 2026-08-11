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
package io.github.seayar.modbus4j.concurrent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdaptiveConcurrencyTest {

    @Test
    public void testInitial() {
        AdaptiveConcurrency ac = new AdaptiveConcurrency(1, 10, 1_000_000L, 0.1);
        assertEquals(1, ac.getCurrentInFlight());
        assertEquals(1, ac.getMinInFlight());
        assertEquals(10, ac.getMaxInFlight());
    }

    @Test
    public void testAdjustIncrease() {
        AdaptiveConcurrency ac = new AdaptiveConcurrency(1, 10, 1_000_000L, 0.1);
        ac.record(true, 100_000L);
        ac.record(true, 100_000L);
        ac.record(true, 100_000L);
        assertEquals(2, ac.adjust());
    }

    @Test
    public void testAdjustDecreaseOnSlow() {
        AdaptiveConcurrency ac = new AdaptiveConcurrency(1, 10, 1_000_000L, 0.1);
        ac.record(true, 10_000_000L);
        ac.record(true, 10_000_000L);
        ac.record(true, 10_000_000L);
        assertEquals(1, ac.adjust());
    }

    @Test
    public void testAdjustDecreaseOnError() {
        AdaptiveConcurrency ac = new AdaptiveConcurrency(1, 10, 1_000_000L, 0.1);
        for (int i = 0; i < 3; i++)
            ac.record(true, 100_000L);
        ac.adjust();
        assertEquals(2, ac.getCurrentInFlight());
        ac.record(false, 1_000L);
        ac.record(false, 1_000L);
        ac.record(false, 1_000L);
        assertEquals(1, ac.adjust());
    }

    @Test
    public void testAdjustNoSamples() {
        AdaptiveConcurrency ac = new AdaptiveConcurrency(2, 10, 1_000_000L, 0.1);
        assertEquals(2, ac.adjust());
    }

    @Test
    public void testDecreaseBounded() {
        AdaptiveConcurrency ac = new AdaptiveConcurrency(1, 10, 1_000_000L, 0.1);
        ac.record(false, 1_000L);
        ac.adjust();
        assertEquals(1, ac.getCurrentInFlight());
    }

    @Test
    public void testIncreaseBounded() {
        AdaptiveConcurrency ac = new AdaptiveConcurrency(1, 2, 1_000_000L, 0.1);
        for (int i = 0; i < 20; i++)
            ac.record(true, 1_000L);
        assertEquals(2, ac.adjust());
    }

    @Test
    public void testBoundaryErrorRate() {
        AdaptiveConcurrency ac = new AdaptiveConcurrency(2, 10, 1_000_000L, 0.1);
        ac.record(false, 1_000L);
        ac.record(true, 1_000L);
        ac.record(true, 1_000L);
        assertEquals(2, ac.adjust());
    }

    @Test
    public void testAdjustResetsSamples() {
        AdaptiveConcurrency ac = new AdaptiveConcurrency(1, 10, 1_000_000L, 0.1);
        ac.record(false, 1_000L);
        ac.adjust();
        int after = ac.adjust();
        assertTrue(after == 1);
    }
}
