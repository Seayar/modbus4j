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
package com.seayar.modbus4j.net;

import com.seayar.modbus4j.codec.ModbusFrame;
import com.seayar.modbus4j.concurrent.PendingRequest;
import com.seayar.modbus4j.concurrent.PendingRequests;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ModbusResponseHandler extends SimpleChannelInboundHandler<ModbusFrame> {
    private final PendingRequests pendingRequests;

    public ModbusResponseHandler(PendingRequests pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusFrame frame) throws Exception {
        PendingRequest request;
        if (frame.getTransactionId() < 0)
            request = pendingRequests.removeFirst();
        else
            request = pendingRequests.remove(frame.getTransactionId());
        if (request != null)
            request.getFuture().complete(frame.getMessage());
    }    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        pendingRequests.failAll(cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        pendingRequests.failAll(new IllegalStateException("connection closed"));
        super.channelInactive(ctx);
    }
}
