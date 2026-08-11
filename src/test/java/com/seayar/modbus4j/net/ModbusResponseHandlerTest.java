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
 * @date 2026-08-11
 */
package com.seayar.modbus4j.net;

import com.seayar.modbus4j.codec.ModbusFrame;
import com.seayar.modbus4j.concurrent.PendingRequests;
import com.seayar.modbus4j.msg.ReadExceptionStatusRequest;
import com.seayar.modbus4j.msg.ReadExceptionStatusResponse;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.timeout.IdleStateEvent;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModbusResponseHandlerTest {

    @Test
    public void testChannelReadMatchesTransactionId() {
        PendingRequests pending = new PendingRequests();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending));
        java.util.concurrent.CompletableFuture<Object> future = pending.putAndGetFuture(7, 1000);
        channel.writeInbound(new ModbusFrame(7, new ReadExceptionStatusResponse(1, 0x05)));
        assertTrue(future.isDone());
        assertTrue(future.join() instanceof ReadExceptionStatusResponse);
        channel.finishAndReleaseAll();
    }

    @Test
    public void testChannelReadFifoMatch() {
        PendingRequests pending = new PendingRequests();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending));
        java.util.concurrent.CompletableFuture<Object> future = pending.putAndGetFuture(-1, 1000);
        channel.writeInbound(new ModbusFrame(-1, new ReadExceptionStatusResponse(1, 0x05)));
        assertTrue(future.isDone());
        channel.finishAndReleaseAll();
    }

    @Test
    public void testIdleCloseWhenAutoReconnect() {
        PendingRequests pending = new PendingRequests();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending, true));
        channel.pipeline().fireUserEventTriggered(IdleStateEvent.READER_IDLE_STATE_EVENT);
        channel.runPendingTasks();
        assertFalse(channel.isActive());
        assertFalse(channel.isOpen());
        channel.finishAndReleaseAll();
    }

    @Test
    public void testIdleNoCloseWithoutAutoReconnect() {
        PendingRequests pending = new PendingRequests();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending, false));
        channel.pipeline().fireUserEventTriggered(IdleStateEvent.READER_IDLE_STATE_EVENT);
        assertTrue(channel.isActive());
        channel.finishAndReleaseAll();
    }

    @Test
    public void testUnknownEventPassedThrough() {
        PendingRequests pending = new PendingRequests();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending, true));
        channel.pipeline().fireUserEventTriggered("some-event");
        assertTrue(channel.isActive());
        channel.finishAndReleaseAll();
    }

    @Test
    public void testExceptionCaughtFailsAllAndCloses() {
        PendingRequests pending = new PendingRequests();
        EmbeddedChannel channel = new EmbeddedChannel(new ModbusResponseHandler(pending, false));
        java.util.concurrent.CompletableFuture<Object> future = pending.putAndGetFuture(1, 1000);
        channel.pipeline().fireExceptionCaught(new RuntimeException("boom"));
        channel.runPendingTasks();
        assertTrue(future.isCompletedExceptionally());
        assertFalse(channel.isActive());
        channel.finishAndReleaseAll();
    }
}
