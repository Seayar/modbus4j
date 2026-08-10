# modbus4j

> A pure-Java Modbus client library built on Netty. Supports **TCP**, **RTU (over TCP serial-to-network gateways)** and **ASCII** modes, with a high-performance asynchronous engine, adaptive concurrency control, and an extension API for non-standard Modbus variants.

[中文文档](./README.zh-CN.md) · [License](./LICENSE) · [Contributing](./CONTRIBUTING.md)

## Highlights

- Full Modbus function code support (read/write coils, discrete inputs, holding/input registers, file records, exceptions)
- TCP: pipelined asynchronous reads/writes over a single long connection — read well over 65,535 bytes in one logical batch
- Adaptive concurrency: automatically raises/lowers in-flight request count based on measured slave response time and error rate
- RTU / ASCII over TCP: strictly synchronous request-response sequencing for reliable data acquisition
- Rich data types with register/byte swap variants (byte-swap, word-swap, inverted float, BCD, MOD10K, …) and bit-level access
- Extensible: custom function codes, custom transports, and netty pipeline customization (SSL, pre-connection authentication, vendor variants)
- Minimal dependencies: Netty + slf4j-api only

## Quick start

```java
ModbusFactory factory = new ModbusFactory();
ModbusMaster master = factory.createTcpMaster(new IpParameters(), true);
master.init();

// Single point read
BaseLocator<Float> temp = BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT);
float value = master.getValue(temp);

// Batch read
BatchRead<String> batch = new BatchRead<>();
batch.addLocator("temp", BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT));
batch.addLocator("on", BaseLocator.coilStatus(1, 0));
BatchResults<String> results = master.send(batch);
Float temp = results.getValue("temp");
```

## Build

```bash
mvn package          # builds; the license header plugin runs at generate-sources
mvn test             # unit + integration tests (self-contained, no live device needed)
mvn verify -Pcoverage-gate   # enforces >=95% line coverage (default on)
```

## Documentation

- [English README](./README.md) · [Chinese README](./README.zh-CN.md)
- [Contributing guide](./CONTRIBUTING.md)
- [Changelog](./CHANGELOG.md)

## License

GNU General Public License v3.0 or later. Commercial users who modify this library must contribute their modifications back under the same license.

## Roadmap

- v1.0: TCP / RTU / ASCII master modes (this release)
- v2.0: Slave (server) mode
