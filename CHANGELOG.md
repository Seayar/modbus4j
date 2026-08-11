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
- `samples/` module with runnable examples (embedded slave + TCP/RTU/ASCII/batch/polling/data-types/extended-FC/custom-codec/custom-pipeline/custom-transport).
- Detailed bilingual usage + extension guide in README.
