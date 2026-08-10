# modbus4j

> 一个基于 Netty 的纯 Java Modbus 客户端库。完整支持 **TCP**、**RTU（串口转 TCP 网关场景）** 和 **ASCII** 模式，内置高性能异步引擎、自适应并发控制，并提供针对非标准 Modbus 变体协议的扩展接口。

[English](./README.md) · [License](./LICENSE) · [Contributing](./CONTRIBUTING.md)

## 特性

- 完整支持标准 Modbus 功能码（读写线圈、离散输入、保持/输入寄存器、文件记录、异常码等）
- TCP 模式：单条长连接上流水线式异步读写，一次逻辑批量可读取远超 65,535 字节的数据
- 自适应并发：根据从站响应时间与错误率自动增减在途请求数
- RTU / ASCII over TCP：严格同步的请求-响应时序，保证数据采集可靠性
- 丰富的数据类型与字节序/寄存器序交换（字节交换、字交换、浮点倒序、BCD、MOD10K 等）以及按位读写
- 可扩展：自定义功能码、自定义传输层、Netty 管道定制（SSL、通信前身份认证、厂商私有变体）
- 极简依赖：仅 Netty 与 slf4j-api

## 快速开始

```java
ModbusFactory factory = new ModbusFactory();
ModbusMaster master = factory.createTcpMaster(new IpParameters(), true);
master.init();

// 单点读取
BaseLocator<Float> temp = BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT);
float value = master.getValue(temp);

// 批量读取
BatchRead<String> batch = new BatchRead<>();
batch.addLocator("temp", BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT));
batch.addLocator("on", BaseLocator.coilStatus(1, 0));
BatchResults<String> results = master.send(batch);
Float temp = results.getValue("temp");
```

## 构建

```bash
mvn package          # 构建；license 头校验插件在 generate-sources 阶段自动补齐文件头
mvn test             # 单元 + 集成测试（自包含，无需真实设备）
mvn verify -Pcoverage-gate   # 强制行覆盖率 >= 95%（默认开启）
```

## 文档

- [中文文档](./README.zh-CN.md) · [英文文档](./README.md)
- [贡献指南](./CONTRIBUTING.md)
- [更新日志](./CHANGELOG.md)

## 许可证

GNU General Public License v3.0 或更高版本。商用用户如对本库做出修改，须以相同许可协议将修改回馈本项目。

## 路线图

- v1.0：TCP / RTU / ASCII 主站模式（本版本）
- v2.0：从站（服务器）模式
