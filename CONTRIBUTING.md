# Contributing to modbus4j

Thank you for contributing! This project is licensed under the GPL v3 (or later); by contributing code you agree that your changes are released under the same license.

## Getting started

- Java 8+ and Maven 3.6+ are required.
- Every `.java` file must start with the GPL header (see `HEADER`). The license plugin auto-adds missing headers during `mvn package` (generate-sources phase) and `strictCheck` fails the build on incorrect headers at `verify`.

## Development loop

```bash
mvn package    # compile + auto-format license headers
mvn test       # run the test suite
mvn verify -Dmodbus4j.skipCoverage=true   # build without the 95% coverage gate
mvn verify     # full build incl. >=95% line coverage gate
```

## Test conventions

- Tests are JUnit 4, self-contained. No mock framework: use the embedded Netty Modbus slave/server and Netty `EmbeddedChannel` for codec tests.
- Do not add tests that require a live device or external simulator.

## Code style

- Match the existing conventions: Java 8 syntax, `io.github.seayar.modbus4j` root package, minimal third-party dependencies.
- No code comments unless they add meaning beyond the code.

## Git workflow

- Use conventional commits (e.g. `feat(core): add read file record request`).
- Branch from `main`, open a pull request. All checks must pass.
