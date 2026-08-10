# AGENTS.md

## Project status

- This repo is a **new, from-scratch build**. The directory is currently empty — scaffold the project (Maven layout, build files, `.gitignore`) before adding source. Do not expect existing code.

## Reference project (follow its conventions, don't copy wholesale)

Design is based on `/mnt/d/Workspace/Java/modbus4j` (Netty-based Java Modbus client, `com.seayar:modbus4j:1.0`). It is reference only. Verified conventions to match:

- Maven build, `maven.compiler.source/target = 8`
- Dependency stack: `io.netty:netty-all` 4.1.x, Guava, `slf4j-api` + `logback-classic`, JUnit 4 (`junit:4.13.2`, test scope)
- Root package: `com.seayar.modbus4j`
- Every `.java` file must start with the GPL header (see the `HEADER` file in the reference repo). `com.mycila:license-maven-plugin` 3.0 with `strictCheck=true` runs at the `generate-sources` phase and fails the build on any missing/incorrect header. Adds every new file's header before committing.
- Typical package layout: `base/` (locators, `BatchRead`, `DataType`), `client/` (Tcp/Rtu master+slave clients and factories), `codec/` (`tcp/`, `rtu/`, `m2m/`, `ping/`, `util/`), `core/` (`func/request`, `func/response`, `protocol/`, `common/util`), plus `handler/`, `sender/`, `channel/`, `cache/`, `schedule/`, `event/`
- No mock framework in tests. The reference `ModbusClientTest` requires a live device/simulator on `127.0.0.1:502` and is not self-contained; prefer self-contained tests in the new project.

## Build

- Compile/package: `mvn package` (add the license plugin + `HEADER` when scaffolding, or headers won't be enforced)
- Tests: JUnit 4 via `mvn test`
