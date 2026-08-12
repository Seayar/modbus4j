# modbus4j

> A pure-Java, Netty-based Modbus client library (`io.github.seayar:modbus4j`). Supports **Modbus TCP**, **RTU over TCP** (serial-to-TCP gateway) and **ASCII over TCP** master modes, a pipelined asynchronous engine, adaptive-concurrency metrics, rich register data types and an extension API for non-standard Modbus variants.

[中文文档](./README.zh-CN.md) · [License](./LICENSE) · [Contributing](./CONTRIBUTING.md) · [Changelog](./CHANGELOG.md)

## Table of contents

- [Highlights](#highlights)
- [Compatibility status](#compatibility-status)
- [Getting the library](#getting-the-library)
- [Quick start](#quick-start)
- [Master modes](#master-modes)
- [Configuration](#configuration)
- [Locators and data types](#locators-and-data-types)
- [Batch operations](#batch-operations)
- [Polling](#polling)
- [Asynchronous reads](#asynchronous-reads)
- [Errors and exceptions](#errors-and-exceptions)
- [Connection lifecycle and performance](#connection-lifecycle-and-performance)
- [Extending modbus4j](#extending-modbus4j)
- [Project layout](#project-layout)
- [Building and testing](#building-and-testing)
- [License](#license)
- [Roadmap](#roadmap)
- [References](#references)

## Highlights

- **Six wire modes** — Modbus TCP and UDP (async, pipelined), RTU and ASCII over TCP and over UDP (strictly synchronous request/response), all framed/checked internally (MBAP, CRC-16, LRC). Matches the common Modbus slave simulator modes: TCP, UDP, RTU over TCP, RTU over UDP.
- **Full read/write coverage** — coils, discrete inputs, holding registers and input registers, single and multiple writes, plus the extended function codes: exception status, report slave id, file records, mask-write and read/write multiple registers (FC 7, 17, 20–23).
- **High throughput on one connection** — TCP/UDP modes pipeline many in-flight requests over a single channel; a logical `BatchRead` can span far more than the 65,535-byte/125-register wire limit because it is transparently split into multiple requests.
- **Adaptive concurrency** — the engine measures per-response round-trip time and error rate and automatically raises/lowers the in-flight limit, so fast slaves are pipelined hard while slow ones are never flooded.
- **Auto-reconnect** — optional idle-timeout detection and automatic reconnection for environments with firewall TCP aging.
- **Rich data types** — 2/4/8-byte integers, floats, BCD, MOD10K, signed/unsigned, upper/lower byte, CHAR/VARCHAR strings, and bit-level access; every 16/32/64-bit value covers the four explicit wire byte orders (ABCD / BADC / CDAB / DCBA).
- **Extensible** — plug in your own function codes, wire codec, Netty pipeline (SSL, pre-connection authentication, vendor framing) or a whole transport via documented SPIs. See [Extending modbus4j](#extending-modbus4j).
- **Samples** — runnable examples for every usage live in the [`samples/`](./samples/README.md) module.
- **Minimal dependencies** — only Netty (`transport`/`codec`/`handler`) and `slf4j-api`; no heavy third-party stack.

## Compatibility status

Implemented in this release:

| Area | Status |
| --- | --- |
| Modbus TCP master | ✅ async, pipelined, MBAP framing |
| Modbus UDP master | ✅ async, pipelined, MBAP in a datagram |
| RTU over TCP / UDP master | ✅ synchronous, CRC-16, FIFO response matching |
| ASCII over TCP / UDP master | ✅ synchronous, LRC, `:`…`CR LF` framing |
| Function codes 1–7, 15–17, 20–23 | ✅ requests + responses |
| Exception responses (0x80 + code) | ✅ decoded into `ExceptionResponse` |
| Register bit read/write (holding/input) | ✅ |
| 2/4/8-byte ints & floats, all 4 byte orders, BCD, MOD10K | ✅ (see [Data types](#data-types)) |
| CHAR / VARCHAR | ✅ |
| Batch read grouping & polling | ✅ |
| Adaptive in-flight throttling | ✅ automatic, based on measured latency/error rate |
| Auto-reconnect (idle timeout / firewall aging) | ✅ opt-in via `IpParameters` |
| Custom function code / codec / pipeline / transport SPIs | ✅ (see [Extending modbus4j](#extending-modbus4j)) |

Remaining planned work lives in the [Roadmap](#roadmap); there are no unimplemented declared constants.

## Getting the library

Maven coordinates:

```xml
<dependency>
    <groupId>io.github.seayar</groupId>
    <artifactId>modbus4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

Build from source (Java 8+, Maven 3.6+):

```bash
mvn package      # compile + auto-format license headers
mvn test         # run the self-contained test suite (no live device needed)
mvn verify       # full build incl. the >=95% line-coverage gate
```

## Quick start

```java
import io.github.seayar.modbus4j.*;
import io.github.seayar.modbus4j.base.DataType;
import io.github.seayar.modbus4j.ip.IpParameters;
import io.github.seayar.modbus4j.locator.*;

IpParameters params = new IpParameters();
params.setHost("192.168.1.10");
params.setPort(502);

ModbusFactory factory = new ModbusFactory();
ModbusMaster master = factory.createTcpMaster(params, true);
master.init();

try {
    // 1) Single-point read (slave 1, holding register 100, 32-bit float)
    BaseLocator<Number> temp = BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT);
    Number value = master.getValue(temp);

    // 2) Batch read — grouped by slave + range, split at the 125-register wire limit
    BatchRead<String> batch = new BatchRead<>();
    batch.addLocator("temp", BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT));
    batch.addLocator("on", BaseLocator.coilStatus(1, 0));
    batch.addLocator("sn", BaseLocator.holdingRegisterString(1, 200, DataType.VARCHAR, 8));
    BatchResults<String> results = master.send(batch);
    Object tempValue = results.getValue("temp");
    Object on = results.getValue("on");
    Object serial = results.getValue("sn");

    // 3) Write
    master.setValue(BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT), 23.5f);
    master.setValue(BaseLocator.coilStatus(1, 0), true);
} finally {
    master.destroy();
}
```

## Samples

Runnable examples for every usage (TCP, RTU/ASCII, batch, polling, data types, extended function codes, custom codec/pipeline/transport) are bundled in the `samples/` module and need no real device — a small embedded Netty slave is included. See [`samples/README.md`](./samples/README.md).

## Master modes

### TCP master — asynchronous

```java
ModbusMaster master = factory.createTcpMaster(params, true);   // validateResponse = true
master.init();
```

One long-lived TCP channel. Requests are pipelined: each gets a transaction id, several may be in flight, and responses are matched by id as they arrive. This is the mode that can push very large logical batches (many `FC3`/`FC4` requests split by `BatchRead`) through a single connection.

### RTU over TCP — synchronous

```java
ModbusMaster master = factory.createRtuMaster(params, true);   // serial-to-TCP gateway
master.init();
```

For devices behind a **serial-to-TCP gateway**: the payload is serial RTU (CRC-16), delivered over TCP. The engine enforces strictly synchronous sequencing — it never sends the next request until the previous response has arrived — which is the safe, reliable behaviour for slow serial lines. Responses are matched FIFO. Frames with a bad CRC (or otherwise corrupt) are dropped automatically and the decoder resynchronizes on the next valid frame, so a single corrupted response never wedges the connection; dropped frames are logged at `warn`.

### ASCII over TCP — synchronous

```java
ModbusMaster master = factory.createAsciiMaster(params, true);
master.init();
```

Same synchronous semantics as RTU, but with ASCII framing (`:` … `CR LF`, LRC checksum). Useful for gateways/devices that only speak ASCII.

### UDP master — asynchronous

```java
ModbusMaster master = factory.createUdpMaster(params, true);
master.init();
```

Modbus UDP (MBAP header in a single datagram). Pipelined with transaction-id matching, same as TCP — each request and response is one UDP datagram. Pair it with a simulator running in **UDP** mode.

### RTU over UDP — synchronous

```java
ModbusMaster master = factory.createRtuUdpMaster(params, true);
master.init();
```

RTU framing (CRC-16) inside a single datagram, strictly synchronous (one request in flight). Use with a simulator in **RTU over UDP** mode.

### ASCII over UDP — synchronous

```java
ModbusMaster master = factory.createAsciiUdpMaster(params, true);
master.init();
```

ASCII framing over a single datagram, synchronous. Use with a simulator in **ASCII over UDP** mode.

## Configuration

`IpParameters` configures host, port and socket behaviour:

| Method | Default | Meaning |
| --- | --- | --- |
| `setHost` / `setPort` | `localhost` / `502` | Target gateway/device |
| `setConnectTimeoutMillis` | `5000` | TCP connect timeout |
| `setReadTimeoutMillis` | `10000` | Response timeout (also drives the idle handler) |
| `setWriteTimeoutMillis` | `5000` | Write timeout |
| `setKeepAlive` / `setTcpNoDelay` | `true` / `true` | SO_KEEPALIVE / TCP_NODELAY |
| `setSoLinger` | `0` | SO_LINGER seconds |
| `setAutoReconnect` | `false` | Auto-reconnect after the connection drops or goes idle |
| `setReconnectDelayMillis` | `1000` | Delay between reconnect attempts |

The second argument to `create*Master` is `validateResponse`: when `true`, a Modbus exception response is thrown as a `ModbusCodeException`; when `false`, the raw `ExceptionResponse` object is returned so callers can inspect it.

When `setAutoReconnect(true)` is set, an idle connection (no traffic for `readTimeoutMillis`, e.g. a firewall dropped it) is closed and re-established after `reconnectDelayMillis`; pending requests at that moment fail with `ModbusTransportException`, subsequent ones use the fresh connection.

## Locators and data types

### Locator factories

`BaseLocator` (package `io.github.seayar.modbus4j.locator`) builds typed points:

```java
BaseLocator<Boolean>  coil = BaseLocator.coilStatus(slaveId, offset);                 // FC 1
BaseLocator<Boolean>  input = BaseLocator.inputStatus(slaveId, offset);               // FC 2
BaseLocator<Number>   hr = BaseLocator.holdingRegister(slaveId, offset, dataType);    // FC 3
BaseLocator<Number>   ir = BaseLocator.inputRegister(slaveId, offset, dataType);      // FC 4
BaseLocator<Boolean>  hrBit = BaseLocator.holdingRegisterBit(slaveId, offset, bit);   // FC 3, one bit
BaseLocator<Boolean>  irBit = BaseLocator.inputRegisterBit(slaveId, offset, bit);     // FC 4, one bit
BaseLocator<String>   hrStr = BaseLocator.holdingRegisterString(slaveId, offset, DataType.VARCHAR, 8);
BaseLocator<String>   irStr = BaseLocator.inputRegisterString(slaveId, offset, DataType.CHAR, 8);
```

`BaseLocator.createLocator(slaveId, registerId, dataType, bit, registerCount)` builds a locator from an absolute Modbus register id (e.g. `400001` for holding register 1) using `RangeAndOffset` range decoding.

All locators are typed, so `master.getValue(locator)` returns the exact Java type and `master.setValue(locator, value)` accepts it.

### Data types

`DataType` constants cover sizes, signedness and byte/word swap orders (all are `int` constants):

| Group | Constants |
| --- | --- |
| 1 register | `TWO_BYTE_INT_UNSIGNED`, `TWO_BYTE_INT_SIGNED`, `TWO_BYTE_INT_UNSIGNED_SWAPPED`, `TWO_BYTE_INT_SIGNED_SWAPPED`, `ONE_BYTE_INT_UNSIGNED_LOWER`, `ONE_BYTE_INT_UNSIGNED_UPPER`, `TWO_BYTE_BCD` |
| 2 registers | `FOUR_BYTE_INT_UNSIGNED`, `FOUR_BYTE_INT_SIGNED` (+ `_SWAPPED`, `_SWAPPED_SWAPPED`), `FOUR_BYTE_FLOAT`, `FOUR_BYTE_FLOAT_SWAPPED`, `FOUR_BYTE_FLOAT_SWAPPED_INVERTED`, `FOUR_BYTE_BCD` (+ `_SWAPPED`), `FOUR_BYTE_MOD_10K` (+ `_SWAPPED`) |
| 3 registers | `SIX_BYTE_MOD_10K` (+ `_SWAPPED`) |
| 4 registers | `EIGHT_BYTE_INT_UNSIGNED`/`SIGNED` (+ `_SWAPPED`), `EIGHT_BYTE_FLOAT` (+ `_SWAPPED`), `EIGHT_BYTE_MOD_10K` (+ `_SWAPPED`) |
| Strings | `CHAR`, `VARCHAR` (optionally with a custom `Charset`) |
| Bit | `BINARY` (via `BinaryLocator`) |

The swap variants cover the two common big-endian conventions and their inversions:

```java
// value is two registers [hi, lo]
BaseLocator<Number> a = BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT);          // ABCD
BaseLocator<Number> b = BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT_SWAPPED);  // CDAB
```

**32/64-bit byte orders.** Every 16/32/64-bit integer and float type also has an explicit, self-documenting constant naming the exact wire byte order. A value spans `N` registers; the two independent axes are *register order* and *byte order within each register*, giving exactly four layouts:

| Suffix | 中文名 | Wire bytes (32-bit example, `0x12345678`) | Meaning |
| --- | --- | --- | --- |
| `_ABCD` | 大端 | `12 34 56 78` | registers in order, bytes not swapped |
| `_BADC` | 先大端后小端 | `34 12 78 56` | registers in order, bytes swapped within each register |
| `_CDAB` | 先小端后大端 | `56 78 12 34` | registers reversed, bytes not swapped |
| `_DCBA` | 小端 | `78 56 34 12` | registers reversed, bytes swapped within each register |

These constants exist for `TWO_BYTE_INT_UNSIGNED/SIGNED` (`_AB`/`_BA`), `FOUR_BYTE_INT_UNSIGNED/SIGNED` and `FOUR_BYTE_FLOAT` (`_ABCD`/`_BADC`/`_CDAB`/`_DCBA`), and `EIGHT_BYTE_INT_UNSIGNED/SIGNED` and `EIGHT_BYTE_FLOAT` (`_ABCD`/`_BADC`/`_CDAB`/`_DCBA`). The legacy Mango-compatible names remain as aliases: `FOUR_BYTE_FLOAT`=`_ABCD`, `FOUR_BYTE_FLOAT_SWAPPED`=`_CDAB`, `FOUR_BYTE_INT_*_SWAPPED`=`_CDAB`, `FOUR_BYTE_INT_*_SWAPPED_SWAPPED`=`_DCBA`, `EIGHT_BYTE_*_SWAPPED`=`_CDAB`. `FOUR_BYTE_FLOAT_SWAPPED_INVERTED` is deprecated (it duplicates `_SWAPPED`).

```java
BaseLocator<Number> big     = BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT_ABCD);
BaseLocator<Number> little  = BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT_DCBA);
BaseLocator<Number> byteSwap = BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT_BADC);
```

BCD types pack two decimal digits per byte (`0x1234` ↔ `1234`); MOD10K types pack up to four decimal digits per register (`1234, 5678` ↔ `12345678`) and map to `BigInteger`. Writes validate the range and throw `IllegalArgumentException` for out-of-range values.

### Bit access

Coils and discrete inputs are inherently bit values. For registers, use `holdingRegisterBit`/`inputRegisterBit` to read one bit, and write it back via `setValue` (the library performs a read-modify-write on the register).

## Extended function codes

The less common standard function codes are exposed as convenience methods on `ModbusMaster`:

```java
byte   status = master.getExceptionStatus(1);                                  // FC 7
byte[] id     = master.reportSlaveId(1);                                       // FC 17
byte[] record = master.readFileRecord(1, 5, 3, 8);                             // FC 20, file 5 record 3, 8 registers
master.writeFileRecord(1, 5, 3, new byte[]{1, 2, 3, 4});                       // FC 21
master.writeMaskRegister(1, 0x4000, 0x00ff, 0x0010);                           // FC 22
byte[] data   = master.readWriteMultipleRegisters(1, 0, 4, 10, new byte[]{1}); // FC 23
```

File records use the `FileRecord` value class; `readFileRecords` / `writeFileRecords` accept a list of them.

## Batch operations

`BatchRead<K>` is the workhorse for polling many points:

```java
BatchRead<String> batch = new BatchRead<>();
batch.addLocator("a", BaseLocator.coilStatus(1, 0));
batch.addLocator("b", BaseLocator.coilStatus(1, 10));
batch.addLocator("c", BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT));
BatchResults<String> results = master.send(batch);          // one FC1 + one FC3 over the wire
Object c = results.getValue("c");
```

Behaviour knobs:

- **Grouping** — locators with the same slave + range are merged into contiguous `ReadFunctionGroup`s, so a batch of 200 consecutive coils becomes two FC1 requests (wire limit is 2000 bits) instead of 200 single reads.
- `setMaxReadRegisterCount(int)` / `setMaxReadBitCount(int)` — override the default 125-register / 2000-bit wire limits.
- `setContiguousRequests(true)` — only merge truly contiguous ranges (leaves gaps as separate requests).
- `setSplitOnException(boolean)` — **on by default**. If a group read hits a slave exception (for example a slave that forbids addresses 51–59 inside a 0–100 range), the range is split in half and each half retried recursively. Readable points are returned normally; only the points that still fail become per-point errors — call `results.isError(key)` / `results.getErrors()` to find them — instead of the whole group failing. Set to `false` to restore fail-fast behaviour (`ModbusCodeException` on the first group exception).
- `setErrorsInResults(true)` — a failed read stores an error marker in the results instead of throwing; check with `results.isError(key)`.
- `setExceptionsInResults(true)` — a Modbus exception response becomes an in-result error instead of a thrown `ModbusCodeException`.
- `setCancel(true)` — abort the batch loop at the next group boundary.
- `send(batch, retry)` and `send(batch, retry, primary)` — reserved for retry-aware callers.

## Polling

`PollTask` drives a batch on a schedule and pushes results to a `PollListener`:

```java
PollTask task = new PollTask(master, new PollListener() {
    public void pollCompleted(BatchResults<String> results) {
        System.out.println("temp=" + results.getValue("temp"));
    }
    public void pollFailed(Throwable cause) {
        log.error("poll failed", cause);
    }
});

task.addLocator("temp", BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT));
task.addLocator("on", BaseLocator.coilStatus(1, 0), 5000L);   // per-locator update period (ms)
task.setPeriodMillis(1000);                                    // global poll cadence
task.start();
// ...
task.stop();
```

## Asynchronous reads

The high-level `ModbusMaster` API is synchronous; underneath, TCP mode is fully asynchronous. To drive it directly, use the transport:

```java
Future<AbstractModbusResponse> future = master.getTransport().sendAsync(
        new ReadHoldingRegistersRequest(1, 0, 10));
AbstractModbusResponse response = future.get(5, TimeUnit.SECONDS);
```

`sendAsync` returns a `CompletableFuture`-backed future that completes on the matching response, on timeout, or when the connection closes (`ModbusTransportException` / `ModbusInitException` on `init`).

## Errors and exceptions

| Exception | When |
| --- | --- |
| `ModbusInitException` | `master.init()` fails (connect refused, timeout, …) |
| `ModbusTransportException` | I/O failure, response timeout, transport not initialized, connection closed mid-request |
| `ModbusCodeException` | The slave returned a Modbus exception response (thrown when `validateResponse` is `true`) |
| `ModbusIdException` | Reserved for slave/unit-id validation |

`ExceptionResponse.getExceptionCode()` returns the Modbus exception code (01 illegal function, 02 illegal data address, 03 illegal data value, 04 slave device failure, …).

## Connection lifecycle and performance

- **One channel per master.** All reads and writes share a single TCP connection (`NettyTransport`), reusing the event loop for IO — no per-request connection setup.
- **Very large logical batches.** `BatchRead` splits at the 125-register / 2000-bit wire limit and pipelines the resulting requests, so a logical batch can span many thousands of registers while keeping each request inside the protocol limit.
- **Adaptive concurrency.** In TCP mode the transport gates in-flight requests to an adaptive window: every request's success/round-trip time feeds `AdaptiveConcurrency(min, max, targetNanos, errorThreshold)`, which is re-evaluated periodically to raise or lower the window automatically. `getMaxInFlight()` reports the current window; `setMaxInFlight(int)` overrides it.
- **Response timeouts.** Each pending request carries `readTimeoutMillis`; a late or missing response fails only that request (`ModbusTransportException`), not the connection.
- **RTU/ASCII reliability.** These modes never pipeline; strict request/response ordering guarantees deterministic acquisition on slow serial gateways.
- **Long connections with auto-reconnect.** `SO_KEEPALIVE` is on by default. With `IpParameters.setAutoReconnect(true)`, an idle/dropped connection is detected and re-established automatically, which handles aggressive firewall TCP aging.

## Extending modbus4j

This is the documented "reserved extension capability". All non-standard Modbus variants — vendor function codes, custom framing, TLS encryption, pre-connection authentication, brand-specific payloads — are supported through a small set of public SPIs. Pick the layer you need:

| You want to change… | SPI to implement/extend |
| --- | --- |
| A new function code (request/response wire format) | `AbstractModbusRequest` / `AbstractModbusResponse` |
| A different wire framing / protocol (TLS-in-frame, extra header, custom PDU) | `ModbusCodec` |
| The Netty pipeline (SSL, handshake/auth, vendor sniffers, filtering) | `ChannelPipelineCustomizer` |
| Everything about the connection (UDP, shared connection, virtual channels) | `ModbusTransport` (+ subclass `ModbusMaster`) |
| A domain-specific value type (date/time, custom BCD, structured data) | `BaseLocator<T>` |

### 1) Custom function code — extend the message classes

Mask-write register (FC 22) is now a built-in convenience method, but it remains the clearest illustration of the extension pattern: create request/response classes for a vendor function code exactly like the built-ins (here shown for a hypothetical vendor FC):

```java
public class MaskWriteRegisterRequest extends AbstractModbusRequest {
    private final int offset, andMask, orMask;

    public MaskWriteRegisterRequest(int slaveId, int offset, int andMask, int orMask) {
        super(slaveId, FunctionCode.WRITE_MASK_REGISTER);   // 0x16
        this.offset = offset;
        this.andMask = andMask;
        this.orMask = orMask;
    }

    @Override protected int getDataLength() { return 6; }

    @Override protected void writeData(ByteBuf buf) {
        buf.writeShort(offset).writeShort(andMask).writeShort(orMask);
    }

    @Override
    public AbstractModbusResponse createResponse(ByteBuf data) {
        return new MaskWriteRegisterResponse(getSlaveId(), data);
    }
}
```

and the matching `MaskWriteRegisterResponse extends AbstractModbusResponse` (parse the echo of address/masks in its constructor). Send it directly:

```java
AbstractModbusResponse resp = master.getTransport().send(new MaskWriteRegisterRequest(1, 0x4000, 0x00FF, 0x0010));
```

For a genuinely new function code, follow the same pattern and register it in `MessageUtil.createResponse` (or teach a custom codec about it — see below).

### 2) Custom wire codec — implement `ModbusCodec`

A codec is responsible for encoding requests into wire bytes and decoding raw bytes into response objects. Implement one to add vendor framing or to teach the transport about function codes that `MessageUtil` does not know (which is where a custom response type actually gets built):

```java
public class VendorCodec implements ModbusCodec {
    private final TcpCodec delegate = new TcpCodec();

    @Override
    public ByteBuf encode(AbstractModbusRequest request, int transactionId) {
        if (request.getFunctionCode() == 0x65)                       // vendor FC
            return encodeVendorFrame(request, transactionId);       // your framing
        return delegate.encode(request, transactionId);              // standard MBAP otherwise
    }

    @Override
    public ModbusFrame decode(ByteBuf in) {
        ModbusFrame frame = decodeVendorFrame(in);                   // try vendor format first
        if (frame != null)
            return frame;
        return delegate.decode(in);                                  // then standard TCP
    }
}
```

Plug it into the standard transport with the codec-based constructor:

```java
ModbusTransport transport = new NettyTransport(params, new VendorCodec(), /*synchronous=*/ false,
        new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1), null);
ModbusMaster master = new TcpMaster(transport, true);
```

See `samples/CustomCodecSample` for a complete, runnable custom-codec example.

### 3) Custom Netty pipeline — implement `ChannelPipelineCustomizer`

The pipeline is where TLS, pre-connection authentication and traffic filtering belong. The customizer runs before the Modbus frame decoder/encoder, so custom handlers see raw bytes:

```java
ChannelPipelineCustomizer customizer = pipeline -> {
    // (a) TLS first — everything below runs over the encrypted channel
    SslContext ssl = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();
    pipeline.addLast("ssl", ssl.newHandler(pipeline.channel().alloc()));

    // (b) pre-connection identity handshake — remove itself on success, close on failure
    pipeline.addLast("auth", new AuthHandshakeHandler());
};

ModbusTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
        new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1), customizer);
ModbusMaster master = new TcpMaster(transport, true);
```

`AuthHandshakeHandler` is application code: write your greeting, verify the peer challenge, then `ctx.pipeline().remove(this)` so Modbus frames flow through. Return it before the Modbus handlers start producing requests if you need to block sends until authenticated (for example a thin `isAuthenticated()` gate used before `send`).

### 4) Custom transport — implement `ModbusTransport`

For anything a pipeline can't express (UDP, connection pooling, virtual multi-drop channels), implement the full interface and hand it to a master subclass:

```java
public class MyModbusMaster extends ModbusMaster {
    public MyModbusMaster(ModbusTransport transport, boolean validateResponse) {
        super(transport, validateResponse);
    }
}

ModbusMaster master = new MyModbusMaster(new MyTransport(), true);
```

The master keeps all the high-level behaviour (`getValue`, `setValue`, `send(BatchRead)`, polling) regardless of the transport underneath.

### 5) Custom value type — extend `BaseLocator<T>`

Add a domain-specific decoding without touching the protocol:

```java
public class BcdTimeLocator extends BaseLocator<Long> {
    public BcdTimeLocator(int slaveId, int offset) {
        super(slaveId, RegisterRange.HOLDING_REGISTER, offset);
    }
    @Override public int getDataType() { return 0x7001; }           // app-defined
    @Override public int getRegisterCount() { return 3; }
    @Override public Long bytesToValueRealOffset(byte[] data, int offset) { /* BCD → epoch */ }
    @Override public short[] valueToShorts(Long value) { /* epoch → BCD */ }
}
```

### Combining everything (vendor variant: custom FC + TLS + auth)

```java
ChannelPipelineCustomizer pipeline = p -> {
    p.addLast("ssl",  sslContext.newHandler(p.channel().alloc()));
    p.addLast("auth", new AuthHandshakeHandler());
};
ModbusCodec codec = new VendorCodec();                          // custom FC 0x16 framing
ModbusTransport transport = new NettyTransport(params, codec, false,
        new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1), pipeline);
ModbusMaster master = new TcpMaster(transport, true);
master.init();
master.getTransport().send(new MaskWriteRegisterRequest(1, 0x4000, 0x00FF, 0x0010));
master.destroy();
```

## Project layout

```
io.github.seayar.modbus4j
├── base/       DataType, FunctionCode, RegisterRange, ReadFunctionGroup, SlaveAndRange, KeyedModbusLocator
├── locator/    BaseLocator, NumericLocator, BinaryLocator, StringLocator, BatchRead, BatchResults
├── msg/        requests/responses + MessageUtil registry
├── codec/      ModbusCodec (+ TCP/RTU/ASCII implementations), ModbusFrame
├── net/        ModbusChannelInitializer, ModbusFrameDecoder/Encoder, ModbusResponseHandler, ChannelPipelineCustomizer
├── transport/  ModbusTransport (SPI), NettyTransport
├── ip/         TcpMaster, IpParameters
├── serial/     RtuMaster, AsciiMaster
├── concurrent/ PendingRequests, AdaptiveConcurrency, TransactionIdGenerator
├── poll/       PollTask, PollListener, PolledLocator
├── exception/  ModbusException hierarchy
└── util/       CRC/LRC, bit set, byte/register, hex helpers
```

## Building and testing

```bash
mvn package              # compile + auto-format license headers (generate-sources)
mvn test                 # 360+ unit/integration tests, fully self-contained
mvn verify               # full build incl. the >=95% line-coverage gate (JaCoCo)
mvn verify -Djacoco.skip=true   # bypass the coverage gate during active development
mvn install -DskipTests  # publish to the local repo, then:
mvn -f samples/pom.xml package   # build the runnable samples
```

Tests are JUnit 4 with an embedded Netty slave and Netty `EmbeddedChannel`; no mock framework and no live device is required. See [CONTRIBUTING.md](./CONTRIBUTING.md) for the development loop and conventions.

## License

GNU General Public License v3.0 **or later** (see [LICENSE](./LICENSE)). Commercial users who modify this library must contribute their modifications back to the project under the same license.

## Roadmap

- v1.0: TCP / UDP / RTU-over-TCP+UDP / ASCII-over-TCP+UDP master modes, full function-code & data-type coverage (all four byte orders), batch & polling, adaptive concurrency, auto-reconnect, extension SPIs, samples *(this release)*
- v1.1: `SIX_BYTE_INT` variants, richer reconnect backoff policies
- v2.0: Slave (server) mode

## References

- Protocol: Modbus Application Protocol Specification V1.1b3, Modbus Messaging on TCP/IP
- API compatibility target: [MangoAutomation/modbus4j](https://github.com/MangoAutomation/modbus4j)
- [Chinese README](./README.zh-CN.md) · [Contributing guide](./CONTRIBUTING.md) · [Changelog](./CHANGELOG.md)
