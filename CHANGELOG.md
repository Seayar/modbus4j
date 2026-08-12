# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added

- Project scaffold: Maven build (Java 8), GPL header enforcement via license-maven-plugin, JaCoCo coverage gate (>=95% line), bilingual README.
- Extension SPI: `ChannelPipelineCustomizer` for Netty pipeline customization (SSL, pre-connection authentication, vendor framing).
- `NettyTransport` constructor accepting a custom `ModbusCodec`; `TcpMaster`/`RtuMaster`/`AsciiMaster` constructors accepting a custom `ModbusTransport`.
- Extended function codes: FC 7 (read exception status), FC 17 (report slave id), FC 20/21 (file records), FC 22 (mask-write register), FC 23 (read/write multiple registers) — message classes, codec registry, RTU framing and `ModbusMaster` convenience methods.
- Data types: BCD (`TWO/FOUR_BYTE_BCD` + swapped) and MOD10K (`FOUR/SIX/EIGHT_BYTE_MOD_10K` + swapped) read/write, plus unsigned 8-byte integer read support, in `NumericLocator`.
- Adaptive in-flight throttling: `NettyTransport` gates requests to the `AdaptiveConcurrency` window and re-evaluates it periodically.
- Auto-reconnect: `IpParameters.autoReconnect` / `reconnectDelayMillis`; idle connections are closed and re-established automatically.
- Async response timeouts: pending requests are swept and complete exceptionally on `readTimeoutMillis`.
- `BatchRead.splitOnException` (default on): a group read that hits a slave exception (e.g. illegal data address in the middle of a range) is split in half and retried recursively, so readable points are returned and only the truly failing points become per-point errors in `BatchResults` instead of failing the whole batch. Disable with `batch.setSplitOnException(false)` for fail-fast behaviour.
- `samples/` module with runnable examples (embedded slave + TCP/RTU/ASCII/batch/polling/data-types/extended-FC/custom-codec/custom-pipeline/custom-transport).
- Detailed bilingual usage + extension guide in README.

### Added

- UDP transport (`UdpTransport`) and three new master modes: Modbus UDP (MBAP over UDP), RTU over UDP, ASCII over UDP — via `ModbusFactory.createUdpMaster` / `createRtuUdpMaster` / `createAsciiUdpMaster`, matching the common Modbus slave simulator's UDP modes. New `DatagramFrameDecoder`/`DatagramFrameEncoder`/`UdpChannelInitializer` wire the datagram channel.
- Explicit 32/64-bit byte-order data types: every 16/32/64-bit signed/unsigned int and float now has `_AB`/`_BA` (16-bit) and `_ABCD`/`_BADC`/`_CDAB`/`_DCBA` (32/64-bit) constants covering the four wire layouts (register order × byte order within register). Legacy Mango-compatible names kept as aliases; `FOUR_BYTE_FLOAT_SWAPPED_INVERTED` deprecated.
- Samples: `UdpSample`; `EmbeddedModbusSlave` now also listens for UDP MBAP, RTU over UDP and ASCII over UDP.

### Fixed

- RTU / ASCII decoders now resynchronize instead of blocking forever when a corrupt frame arrives (bad CRC/LRC, stray bytes, malformed function code). Previously such a frame was kept in the decoder buffer indefinitely, so every later — valid — response was mis-parsed and the connection appeared permanently stuck with `ModbusTransportException: Response timeout` even though the slave was replying normally. Corrupt frames are now dropped (with a `warn` log) and the decoder picks up the next valid frame.
