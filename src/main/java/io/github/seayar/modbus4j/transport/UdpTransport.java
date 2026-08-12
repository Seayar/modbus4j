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
 * @date 2026-08-12
 */
package io.github.seayar.modbus4j.transport;

import io.github.seayar.modbus4j.codec.ModbusCodec;
import io.github.seayar.modbus4j.codec.ModbusCodecType;
import io.github.seayar.modbus4j.codec.ModbusFrame;
import io.github.seayar.modbus4j.concurrent.AdaptiveConcurrency;
import io.github.seayar.modbus4j.concurrent.PendingRequests;
import io.github.seayar.modbus4j.concurrent.TransactionIdGenerator;
import io.github.seayar.modbus4j.exception.ModbusInitException;
import io.github.seayar.modbus4j.exception.ModbusTransportException;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.msg.AbstractModbusRequest;
import io.github.seayar.modbus4j.msg.AbstractModbusResponse;
import io.github.seayar.modbus4j.net.ChannelPipelineCustomizer;
import io.github.seayar.modbus4j.net.UdpChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class UdpTransport implements ModbusTransport {
    private final IpParameters parameters;
    private final ModbusCodec codec;
    private final boolean synchronous;
    private final PendingRequests pendingRequests = new PendingRequests();
    private final TransactionIdGenerator transactionIdGenerator = new TransactionIdGenerator();
    private final AdaptiveConcurrency adaptiveConcurrency;
    private final ChannelPipelineCustomizer pipelineCustomizer;
    private final Object concurrencyLock = new Object();

    private EventLoopGroup eventLoopGroup;
    private ScheduledExecutorService maintenanceExecutor;
    private Channel channel;
    private volatile boolean initialized;
    private volatile boolean destroyed;
    private volatile int allowedConcurrency;

    public UdpTransport(IpParameters parameters, ModbusCodecType codecType, boolean synchronous,
            AdaptiveConcurrency adaptiveConcurrency) {
        this(parameters, codecType.getCodec(), synchronous, adaptiveConcurrency, null);
    }

    public UdpTransport(IpParameters parameters, ModbusCodecType codecType, boolean synchronous,
            AdaptiveConcurrency adaptiveConcurrency, ChannelPipelineCustomizer pipelineCustomizer) {
        this(parameters, codecType.getCodec(), synchronous, adaptiveConcurrency, pipelineCustomizer);
    }

    public UdpTransport(IpParameters parameters, ModbusCodec codec, boolean synchronous,
            AdaptiveConcurrency adaptiveConcurrency, ChannelPipelineCustomizer pipelineCustomizer) {
        this.parameters = parameters;
        this.codec = codec;
        this.synchronous = synchronous;
        this.adaptiveConcurrency = adaptiveConcurrency;
        this.pipelineCustomizer = pipelineCustomizer;
        this.allowedConcurrency = adaptiveConcurrency == null ? 1 : adaptiveConcurrency.getCurrentInFlight();
    }

    @Override
    public void init() throws ModbusInitException {
        destroyed = false;
        eventLoopGroup = new NioEventLoopGroup(0, new DaemonThreadFactory());
        maintenanceExecutor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
        startMaintenance();
        try {
            ChannelFuture future = createBootstrap().connect(parameters.getHost(), parameters.getPort()).sync();
            channel = future.channel();
            initialized = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            shutdownResources();
            throw new ModbusInitException(e);
        } catch (Exception e) {
            shutdownResources();
            throw new ModbusInitException(e);
        }
    }

    private Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        return bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .handler(new UdpChannelInitializer(codec, pendingRequests, parameters.getReadTimeoutMillis(),
                        pipelineCustomizer));
    }

    private void startMaintenance() {
        maintenanceExecutor.scheduleWithFixedDelay(() -> pendingRequests.expire(System.currentTimeMillis()),
                500, 500, TimeUnit.MILLISECONDS);
        if (adaptiveConcurrency != null)
            maintenanceExecutor.scheduleWithFixedDelay(this::adjustConcurrency, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private void adjustConcurrency() {
        synchronized (concurrencyLock) {
            int newValue = adaptiveConcurrency.adjust();
            if (newValue != allowedConcurrency) {
                allowedConcurrency = newValue;
                concurrencyLock.notifyAll();
            }
        }
    }

    private void shutdownResources() {
        if (maintenanceExecutor != null) {
            maintenanceExecutor.shutdownNow();
            maintenanceExecutor = null;
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully(0, 1, TimeUnit.SECONDS);
            eventLoopGroup = null;
        }
    }

    @Override
    public void destroy() {
        destroyed = true;
        initialized = false;
        pendingRequests.close();
        if (channel != null) {
            channel.close().awaitUninterruptibly();
            channel = null;
        }
        shutdownResources();
    }

    @Override
    public boolean isInitialized() {
        return initialized && channel != null && channel.isActive();
    }

    @Override
    public AbstractModbusResponse send(AbstractModbusRequest request) throws ModbusTransportException {
        try {
            Future<AbstractModbusResponse> future = sendAsync(request);
            return future.get(parameters.getReadTimeoutMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new ModbusTransportException("Response timeout", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ModbusTransportException("Interrupted", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ModbusTransportException)
                throw (ModbusTransportException) cause;
            throw new ModbusTransportException(cause);
        }
    }

    @Override
    public Future<AbstractModbusResponse> sendAsync(AbstractModbusRequest request) throws ModbusTransportException {
        checkActive();
        acquireConcurrencySlot();
        int transactionId = synchronous ? -1 : transactionIdGenerator.next();
        long startNanos = System.nanoTime();
        CompletableFuture<Object> future = pendingRequests
                .putAndGetFuture(transactionId, parameters.getReadTimeoutMillis());
        CompletableFuture<AbstractModbusResponse> result = future
                .thenApply(msg -> (AbstractModbusResponse) msg)
                .whenComplete((resp, t) -> {
                    if (adaptiveConcurrency != null) {
                        adaptiveConcurrency.record(t == null, System.nanoTime() - startNanos);
                        synchronized (concurrencyLock) {
                            concurrencyLock.notifyAll();
                        }
                    }
                });
        channel.writeAndFlush(new ModbusFrame(transactionId, request));
        return result;
    }

    private void acquireConcurrencySlot() throws ModbusTransportException {
        if (adaptiveConcurrency == null)
            return;
        synchronized (concurrencyLock) {
            long deadline = System.currentTimeMillis() + parameters.getReadTimeoutMillis();
            while (pendingRequests.size() >= allowedConcurrency) {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0)
                    throw new ModbusTransportException("Timed out waiting for a concurrency slot");
                try {
                    concurrencyLock.wait(Math.min(remaining, 100));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ModbusTransportException("Interrupted while waiting for a concurrency slot", e);
                }
            }
        }
    }

    private void checkActive() throws ModbusTransportException {
        if (!isInitialized())
            throw new ModbusTransportException("Transport not connected");
    }

    @Override
    public int getInFlight() {
        return pendingRequests.size();
    }

    @Override
    public int getMaxInFlight() {
        return allowedConcurrency;
    }

    @Override
    public void setMaxInFlight(int maxInFlight) {
        synchronized (concurrencyLock) {
            allowedConcurrency = Math.max(1, maxInFlight);
            concurrencyLock.notifyAll();
        }
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "modbus4j-udp-" + counter.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }
}
