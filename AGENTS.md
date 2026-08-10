# AGENTS.md

## Project status

- This is a **from-scratch** Maven/Netty Java Modbus client library (`com.seayar:modbus4j:1.0.0`, Java 8). Build it with `mvn package`.
- Scope: TCP + RTU-over-TCP + ASCII master modes, async pipelined reads, adaptive concurrency, rich data types, extension SPIs. Slave mode is a future release.
- Reference-only design source: `/mnt/d/Workspace/Java/modbus4j` (older Netty client). Public API mirrors **MangoAutomation/modbus4j** (ModbusFactory/ModbusMaster/IpParameters/BaseLocator/BatchRead) under package `com.seayar.modbus4j`.

## Build & verification

- Compile/tests: `mvn package`. Tests: JUnit 4, self-contained (embedded Netty slave + `EmbeddedChannel`). No mock framework; never add tests needing a live device.
- **Coverage gate**: JaCoCo enforces >=95% line coverage at `verify` (`jacoco:check`). During active development run `mvn verify -Djacoco.skip=true` (or `-Djacoco.skip=true` on `package`) to bypass until coverage catches up.
- **License headers**: every `.java` file must start with the GPL header from `HEADER`. The mycila license plugin auto-adds headers at `generate-sources` and `strictCheck` fails `verify` on incorrect ones. Run `mvn generate-sources` after adding files so headers exist before committing.

## Conventions

- Root package `com.seayar.modbus4j`; layout: `base/` (DataType, FunctionCode, RegisterRange), `locator/`, `msg/` (requests/responses + `MessageUtil` registry), `codec/`, `transport/`, `ip/`, `serial/`, `concurrent/`, `poll/`, `net/`, `exception/`, `util/`.
- Minimal deps: Netty (transport/codec/handler), slf4j-api. logback-classic and junit are test-only.
- Java 8 syntax only. No comments unless they add meaning.
- Commit with conventional messages; the repo-local git identity is `Seayar <seayar@seayar.com>`.
- Docs: `README.md` (EN) + `README.zh-CN.md` (CN) must stay in sync.
