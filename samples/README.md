# modbus4j samples

Runnable examples showing every usage of the library. Each sample is a `main` class and needs no real device — start the bundled in-memory slave first.

## Requirements

- Java 8+ and Maven 3.6+
- The library must be installed into your local Maven repository once:

```bash
# from the repository root
mvn install -DskipTests
```

## Running

From the repository root, first start the demo slave on port 1502:

```bash
mvn -f samples/pom.xml exec:java -Dexec.mainClass=com.seayar.modbus4j.samples.EmbeddedModbusSlave
```

Then, in a second terminal, run any sample:

```bash
mvn -f samples/pom.xml exec:java -Dexec.mainClass=com.seayar.modbus4j.samples.TcpSample
```

## Sample index

| Sample | Demonstrates |
| --- | --- |
| `EmbeddedModbusSlave` | In-memory Netty Modbus TCP slave (FC 1–7, 15–17, 20–23) used by the other samples |
| `TcpSample` | TCP master: single reads, writes, bit access, strings, `BatchRead`, async via transport |
| `RtuAsciiSample` | RTU-over-TCP and ASCII-over-TCP masters (strictly synchronous) |
| `PollingSample` | `PollTask` + `PollListener` periodic polling |
| `DataTypeSample` | All register data types incl. swap variants, BCD and MOD10K |
| `AdvancedFunctionCodeSample` | FC 7 / 17 / 20 / 21 / 22 / 23 through the master convenience methods |
| `CustomCodecSample` | Vendor function code (FC 22) with a custom request + custom `ModbusCodec` |
| `SslPipelineSample` | `ChannelPipelineCustomizer` wiring TLS + a pre-connection auth handshake |
| `CustomTransportSample` | Implementing `ModbusTransport` and subclassing `ModbusMaster` |

## Notes

- On **WSL / some virtualized filesystems**, Netty's filesystem-type probe can throw `Maximum number of bytes read: 8192`. Add the JVM flag `-Dio.netty.osClassifiers=linux` (or `exec.args` with the exec plugin) to skip the probe.
- `SslPipelineSample` targets a real TLS/authenticating gateway — `EmbeddedModbusSlave` is plain TCP, so that sample will fail the auth handshake by design. Treat it as a wiring template.
- `RtuAsciiSample` also expects an RTU/ASCII serial-to-TCP gateway in front of the slave.
- Every file is licensed under the same GPL v3 (or later) as the library.
