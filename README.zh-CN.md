# modbus4j

> 一个基于 Netty 的纯 Java Modbus 客户端库（`io.github.seayar:modbus4j`）。完整支持 **Modbus TCP**、**RTU over TCP**（串口转 TCP 网关场景）与 **ASCII over TCP** 主站模式，内置流水线式异步引擎、自适应并发度量、丰富的寄存器数据类型，并为非标准 Modbus 变体协议提供扩展接口。

[English](./README.md) · [License](./LICENSE) · [Contributing](./CONTRIBUTING.md) · [Changelog](./CHANGELOG.md)

## 目录

- [特性](#特性)
- [兼容性现状](#兼容性现状)
- [获取依赖](#获取依赖)
- [快速开始](#快速开始)
- [主站模式](#主站模式)
- [连接参数配置](#连接参数配置)
- [点位与数据类型](#点位与数据类型)
- [批量操作](#批量操作)
- [轮询](#轮询)
- [异步读写](#异步读写)
- [错误与异常](#错误与异常)
- [连接生命周期与性能](#连接生命周期与性能)
- [扩展 modbus4j（预留扩展能力）](#扩展-modbus4j预留扩展能力)
- [工程结构](#工程结构)
- [构建与测试](#构建与测试)
- [许可证](#许可证)
- [路线图](#路线图)
- [参考](#参考)

## 特性

- **三种线协议** —— Modbus TCP（异步、流水线）、RTU over TCP、ASCII over TCP（严格同步请求/响应），内部自动完成 MBAP 帧、CRC-16、LRC 校验。
- **读写全覆盖** —— 线圈、离散输入、保持寄存器、输入寄存器的单点与批量读写，并包含扩展功能码：读异常状态、报告从站 ID、文件记录、掩码写、读/写多寄存器（FC 7、17、20–23）。
- **单连接高吞吐** —— TCP 模式在单条长连接上流水线式并发多个在途请求；`BatchRead` 逻辑批量可远超 65,535 字节 / 125 寄存器的单报文上限，内部自动拆分多个请求。
- **自适应并发** —— 引擎测量每个请求的往返时间与错误率，自动提升/降低在途请求上限：从站快则深度流水线，从站慢则绝不压垮。
- **自动重连** —— 可选空闲超时检测与自动重建连接，适配防火墙 TCP 老化场景。
- **丰富数据类型** —— 2/4/8 字节整型与浮点、字节/字交换、有符号/无符号、BCD、MOD10K、高低字节、CHAR/VARCHAR 字符串，以及寄存器按位读写。
- **可扩展** —— 通过公开 SPI 可接入自定义功能码、线编解码器、Netty 管道（SSL、通信前身份认证、厂商私有帧）乃至完整自定义传输层。详见[扩展 modbus4j](#扩展-modbus4j预留扩展能力)。
- **样例代码** —— 每种用法的可运行示例位于 [`samples/`](./samples/README.md) 模块。
- **极简依赖** —— 仅 Netty（`transport`/`codec`/`handler`）与 `slf4j-api`，不引入重型三方依赖。

## 兼容性现状

本版本已实现：

| 项目 | 状态 |
| --- | --- |
| Modbus TCP 主站 | ✅ 异步、流水线、MBAP 帧 |
| RTU over TCP 主站 | ✅ 同步、CRC-16、FIFO 响应匹配 |
| ASCII over TCP 主站 | ✅ 同步、LRC、`:`…`CR LF` 帧 |
| 功能码 1–7、15–17、20–23 | ✅ 请求 + 响应 |
| 异常响应（0x80 + 异常码） | ✅ 解码为 `ExceptionResponse` |
| 寄存器按位读写（保持/输入） | ✅ |
| 2/4/8 字节整型与浮点、交换变体、BCD、MOD10K | ✅（见[数据类型](#数据类型)） |
| CHAR / VARCHAR | ✅ |
| 批量分组读取与轮询 | ✅ |
| 自适应在途限流 | ✅ 依据实测延迟/错误率自动调节 |
| 自动重连（空闲超时 / 防火墙老化） | ✅ 通过 `IpParameters` 开启 |
| 自定义功能码 / 编解码器 / 管道 / 传输层 SPI | ✅（见[扩展 modbus4j](#扩展-modbus4j预留扩展能力)） |

其余规划见[路线图](#路线图)；已声明常量均已完整实现，无未接通项。

## 获取依赖

Maven 坐标：

```xml
<dependency>
    <groupId>io.github.seayar</groupId>
    <artifactId>modbus4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

源码构建（需 Java 8+ 与 Maven 3.6+）：

```bash
mvn package      # 编译 + 自动补齐 license 文件头
mvn test         # 运行自包含测试套件（无需真实设备）
mvn verify       # 完整构建，含行覆盖率 >= 95% 门禁
```

## 快速开始

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
    // 1) 单点读取（从站 1，保持寄存器 100，32 位浮点）
    BaseLocator<Number> temp = BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT);
    Number value = master.getValue(temp);

    // 2) 批量读取 —— 按从站 + 区自动分组，超出 125 寄存器报文上限时自动拆分
    BatchRead<String> batch = new BatchRead<>();
    batch.addLocator("temp", BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT));
    batch.addLocator("on", BaseLocator.coilStatus(1, 0));
    batch.addLocator("sn", BaseLocator.holdingRegisterString(1, 200, DataType.VARCHAR, 8));
    BatchResults<String> results = master.send(batch);
    Object tempValue = results.getValue("temp");
    Object on = results.getValue("on");
    Object serial = results.getValue("sn");

    // 3) 写入
    master.setValue(BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT), 23.5f);
    master.setValue(BaseLocator.coilStatus(1, 0), true);
} finally {
    master.destroy();
}
```

## 样例代码

每种用法的可运行示例（TCP、RTU/ASCII、批量、轮询、数据类型、扩展功能码、自定义编解码器/管道/传输层）都打包在 `samples/` 模块，内置一个微型 Netty 从站，无需真实设备。详见 [`samples/README.md`](./samples/README.md)。

## 主站模式

### TCP 主站 —— 异步

```java
ModbusMaster master = factory.createTcpMaster(params, true);   // validateResponse = true
master.init();
```

单条长连接。请求带事务号流水线式发出，多个请求可同时在途，响应按事务号匹配返回。这是唯一能通过单连接推动超大逻辑批量的模式（`BatchRead` 拆分的多条 `FC3`/`FC4` 请求流水线处理）。

### RTU over TCP —— 同步

```java
ModbusMaster master = factory.createRtuMaster(params, true);   // 串口转 TCP 网关
master.init();
```

面向串口转 TCP 网关下的串口从站：载荷为串口 RTU（CRC-16），经 TCP 传输。引擎强制严格同步时序——上一条响应未到达前绝不发送下一条，保证慢速串口链路的采集可靠性。响应按 FIFO 匹配。CRC 错误或其它损坏的帧会被自动丢弃，解码器自动在下一个有效帧处重新同步，因此单个坏帧不会卡死连接；丢弃的帧会以 `warn` 级别记录日志。

### ASCII over TCP —— 同步

```java
ModbusMaster master = factory.createAsciiMaster(params, true);
master.init();
```

与 RTU 相同的同步语义，帧格式为 ASCII（`:`…`CR LF`，LRC 校验），适用于仅支持 ASCII 的网关/设备。

## 连接参数配置

`IpParameters` 配置主机、端口与套接字行为：

| 方法 | 默认值 | 说明 |
| --- | --- | --- |
| `setHost` / `setPort` | `localhost` / `502` | 目标网关/设备 |
| `setConnectTimeoutMillis` | `5000` | TCP 连接超时 |
| `setReadTimeoutMillis` | `10000` | 响应超时（同时驱动空闲检测） |
| `setWriteTimeoutMillis` | `5000` | 写超时 |
| `setKeepAlive` / `setTcpNoDelay` | `true` / `true` | SO_KEEPALIVE / TCP_NODELAY |
| `setSoLinger` | `0` | SO_LINGER 秒数 |
| `setAutoReconnect` | `false` | 连接断开或空闲后自动重连 |
| `setReconnectDelayMillis` | `1000` | 重连尝试间隔 |

`create*Master` 的第二个参数 `validateResponse`：为 `true` 时，Modbus 异常响应以 `ModbusCodeException` 抛出；为 `false` 时返回原始 `ExceptionResponse` 对象供调用方检查。

设置 `setAutoReconnect(true)` 后，空闲连接（`readTimeoutMillis` 内无流量，如被防火墙断开）会被关闭并在 `reconnectDelayMillis` 后自动重建；断开瞬间的在途请求以 `ModbusTransportException` 失败，后续请求走新连接。

## 点位与数据类型

### 点位工厂

`BaseLocator`（包 `io.github.seayar.modbus4j.locator`）用于构造带类型的点位：

```java
BaseLocator<Boolean>  coil = BaseLocator.coilStatus(slaveId, offset);                 // FC 1
BaseLocator<Boolean>  input = BaseLocator.inputStatus(slaveId, offset);               // FC 2
BaseLocator<Number>   hr = BaseLocator.holdingRegister(slaveId, offset, dataType);    // FC 3
BaseLocator<Number>   ir = BaseLocator.inputRegister(slaveId, offset, dataType);      // FC 4
BaseLocator<Boolean>  hrBit = BaseLocator.holdingRegisterBit(slaveId, offset, bit);   // FC 3，按位
BaseLocator<Boolean>  irBit = BaseLocator.inputRegisterBit(slaveId, offset, bit);     // FC 4，按位
BaseLocator<String>   hrStr = BaseLocator.holdingRegisterString(slaveId, offset, DataType.VARCHAR, 8);
BaseLocator<String>   irStr = BaseLocator.inputRegisterString(slaveId, offset, DataType.CHAR, 8);
```

`BaseLocator.createLocator(slaveId, registerId, dataType, bit, registerCount)` 可根据绝对 Modbus 寄存器号（如 `400001` 表示保持寄存器 1）经 `RangeAndOffset` 换算区与偏移后构造点位。

所有点位均带泛型：`master.getValue(locator)` 返回精确的 Java 类型，`master.setValue(locator, value)` 接受对应类型。

### 数据类型

`DataType` 常量覆盖字节数、符号与字节/字交换顺序（全部为 `int` 常量）：

| 分组 | 常量 |
| --- | --- |
| 1 寄存器 | `TWO_BYTE_INT_UNSIGNED`、`TWO_BYTE_INT_SIGNED`、`TWO_BYTE_INT_UNSIGNED_SWAPPED`、`TWO_BYTE_INT_SIGNED_SWAPPED`、`ONE_BYTE_INT_UNSIGNED_LOWER`、`ONE_BYTE_INT_UNSIGNED_UPPER`、`TWO_BYTE_BCD` |
| 2 寄存器 | `FOUR_BYTE_INT_UNSIGNED`、`FOUR_BYTE_INT_SIGNED`（及 `_SWAPPED`、`_SWAPPED_SWAPPED`）、`FOUR_BYTE_FLOAT`、`FOUR_BYTE_FLOAT_SWAPPED`、`FOUR_BYTE_FLOAT_SWAPPED_INVERTED`、`FOUR_BYTE_BCD`（及 `_SWAPPED`）、`FOUR_BYTE_MOD_10K`（及 `_SWAPPED`） |
| 3 寄存器 | `SIX_BYTE_MOD_10K`（及 `_SWAPPED`） |
| 4 寄存器 | `EIGHT_BYTE_INT_UNSIGNED`/`SIGNED`（及 `_SWAPPED`）、`EIGHT_BYTE_FLOAT`（及 `_SWAPPED`）、`EIGHT_BYTE_MOD_10K`（及 `_SWAPPED`） |
| 字符串 | `CHAR`、`VARCHAR`（可指定自定义 `Charset`） |
| 位 | `BINARY`（经 `BinaryLocator`） |

交换变体覆盖两种常见大端约定及其反序：

```java
// 数值占用两个寄存器 [高, 低]
BaseLocator<Number> a = BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT);          // ABCD
BaseLocator<Number> b = BaseLocator.holdingRegister(1, 0, DataType.FOUR_BYTE_FLOAT_SWAPPED);  // CDAB
```

BCD 类型每个字节压缩两位十进制数（`0x1234` ↔ `1234`）；MOD10K 类型每个寄存器压缩最多四位十进制数（`1234, 5678` ↔ `12345678`），映射为 `BigInteger`。写入时会校验取值范围，越界抛出 `IllegalArgumentException`。

### 按位读写

线圈与离散输入本身就是位值。寄存器按位使用 `holdingRegisterBit`/`inputRegisterBit` 读取单 bit，再通过 `setValue` 写回（库内部对该寄存器执行读-改-写）。

## 扩展功能码

不常用的标准功能码在 `ModbusMaster` 上以便捷方法提供：

```java
byte   status = master.getExceptionStatus(1);                                  // FC 7
byte[] id     = master.reportSlaveId(1);                                       // FC 17
byte[] record = master.readFileRecord(1, 5, 3, 8);                             // FC 20，文件 5 记录 3，8 个寄存器
master.writeFileRecord(1, 5, 3, new byte[]{1, 2, 3, 4});                       // FC 21
master.writeMaskRegister(1, 0x4000, 0x00ff, 0x0010);                           // FC 22
byte[] data   = master.readWriteMultipleRegisters(1, 0, 4, 10, new byte[]{1}); // FC 23
```

文件记录使用 `FileRecord` 值类；`readFileRecords` / `writeFileRecords` 接收记录列表。

## 批量操作

`BatchRead<K>` 是批量采集多个点位的主力：

```java
BatchRead<String> batch = new BatchRead<>();
batch.addLocator("a", BaseLocator.coilStatus(1, 0));
batch.addLocator("b", BaseLocator.coilStatus(1, 10));
batch.addLocator("c", BaseLocator.holdingRegister(1, 100, DataType.FOUR_BYTE_FLOAT));
BatchResults<String> results = master.send(batch);          // 线上实际仅 1 条 FC1 + 1 条 FC3
Object c = results.getValue("c");
```

行为开关：

- **分组** —— 相同从站 + 区的点位合并为连续的 `ReadFunctionGroup`，200 个连续线圈只会变成 2 条 FC1（报文上限 2000 bit）而非 200 次单读。
- `setMaxReadRegisterCount(int)` / `setMaxReadBitCount(int)` —— 覆盖默认 125 寄存器 / 2000 bit 报文上限。
- `setContiguousRequests(true)` —— 仅合并真正连续的范围（有缺口处另起请求）。
- `setSplitOnException(boolean)` —— **默认开启**。若分组读取遇到从站异常（例如从站禁止 0–100 范围内的 51–59 地址），会自动将范围对半拆分并递归重试：可读点位正常返回，只有最终仍失败的点位记为逐点错误，通过 `results.isError(key)` / `results.getErrors()` 识别，而不是整组失败。设为 `false` 恢复 fail-fast 行为（首个组异常即抛 `ModbusCodeException`）。
- `setErrorsInResults(true)` —— 读取失败时在结果中记录错误标记而非抛出，用 `results.isError(key)` 判断。
- `setExceptionsInResults(true)` —— Modbus 异常响应转为结果内错误而非抛出 `ModbusCodeException`。
- `setCancel(true)` —— 在下一个分组边界中止批量循环。
- `send(batch, retry)`、`send(batch, retry, primary)` —— 为需要重试语义的调用方预留。

## 轮询

`PollTask` 按调度周期驱动批量读取，并把结果推送给 `PollListener`：

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
task.addLocator("on", BaseLocator.coilStatus(1, 0), 5000L);   // 点位独立更新周期（毫秒）
task.setPeriodMillis(1000);                                    // 全局轮询节拍
task.start();
// ...
task.stop();
```

## 异步读写

高层 `ModbusMaster` API 是同步外观；底层 TCP 模式完全异步。需要直接驱动时使用传输层：

```java
Future<AbstractModbusResponse> future = master.getTransport().sendAsync(
        new ReadHoldingRegistersRequest(1, 0, 10));
AbstractModbusResponse response = future.get(5, TimeUnit.SECONDS);
```

`sendAsync` 返回基于 `CompletableFuture` 的 Future，在收到匹配响应、超时或连接关闭时完成（对应 `ModbusTransportException`；`init` 失败抛 `ModbusInitException`）。

## 错误与异常

| 异常 | 场景 |
| --- | --- |
| `ModbusInitException` | `master.init()` 失败（连接被拒、超时等） |
| `ModbusTransportException` | IO 故障、响应超时、传输层未初始化、请求中途连接断开 |
| `ModbusCodeException` | 从站返回 Modbus 异常响应（`validateResponse` 为 `true` 时抛出） |
| `ModbusIdException` | 保留，用于从站/单元号校验 |

`ExceptionResponse.getExceptionCode()` 返回 Modbus 异常码（01 非法功能、02 非法数据地址、03 非法数据值、04 从站设备故障等）。

## 连接生命周期与性能

- **每 master 一条连接。** 所有读写共享同一条 TCP 连接（`NettyTransport`），复用事件循环处理 IO，无逐请求建连开销。
- **超大逻辑批量。** `BatchRead` 在 125 寄存器 / 2000 bit 报文上限处分片并流水线发出，逻辑批量可覆盖数千寄存器而单请求始终不超协议上限。
- **自适应并发。** TCP 模式下传输层把在途请求限制在一个自适应窗口内：每个请求的成功/往返耗时送入 `AdaptiveConcurrency(min, max, targetNanos, errorThreshold)`，并周期性重新评估以自动调高/调低窗口。`getMaxInFlight()` 返回当前窗口，`setMaxInFlight(int)` 可手动覆盖。
- **响应超时。** 每个在途请求携带 `readTimeoutMillis`；迟到或缺失的响应只让该请求失败（`ModbusTransportException`），不影响连接。
- **RTU/ASCII 可靠性。** 这两类模式绝不流水线，严格请求/响应时序保证慢速串口网关上的确定性采集。
- **长连接 + 自动重连。** 默认开启 `SO_KEEPALIVE`。设置 `IpParameters.setAutoReconnect(true)` 后，空闲/被断开的连接会被自动检测并重建，适配防火墙 TCP 老化场景。

## 扩展 modbus4j（预留扩展能力）

这是需求文档中"预留扩展能力"的正式落点。所有非标准 Modbus 变体——厂商私有功能码、自定义帧格式、TLS 加密、通信前身份认证、品牌私有载荷——都可通过一组公开 SPI 接入。按需选择扩展层：

| 想变更的内容 | 需要实现/继承的 SPI |
| --- | --- |
| 新增功能码（请求/响应线格式） | `AbstractModbusRequest` / `AbstractModbusResponse` |
| 不同的线编解码 / 协议（帧内 TLS、附加头、自定义 PDU） | `ModbusCodec` |
| Netty 管道（SSL、握手/认证、厂商嗅探、报文过滤） | `ChannelPipelineCustomizer` |
| 关于连接的一切（UDP、共享连接、虚拟多从站通道） | `ModbusTransport`（并继承 `ModbusMaster`） |
| 领域特定值类型（日期时间、自定义 BCD、结构化数据） | `BaseLocator<T>` |

### 1) 自定义功能码 —— 继承报文类

掩码写寄存器（FC 22）已是内置便捷方法，但它仍是最清晰的扩展范例：像内置类一样为厂商功能码创建请求/响应类（这里以示意的厂商功能码为例）：

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

以及对应的 `MaskWriteRegisterResponse extends AbstractModbusResponse`（在其构造器中解析地址/掩码回显）。直接发送：

```java
AbstractModbusResponse resp = master.getTransport().send(new MaskWriteRegisterRequest(1, 0x4000, 0x00FF, 0x0010));
```

若真正新增一个功能码，遵循同样模式并注册到 `MessageUtil.createResponse`（或让自定义编解码器识别，见下）。

### 2) 自定义线编解码器 —— 实现 `ModbusCodec`

编解码器负责把请求编码成线字节、把原始字节解码成响应对象。实现它即可加入厂商私有帧，或让传输层识别 `MessageUtil` 不认识的功能码（自定义响应类型正是在解码阶段被构造的）：

```java
public class VendorCodec implements ModbusCodec {
    private final TcpCodec delegate = new TcpCodec();

    @Override
    public ByteBuf encode(AbstractModbusRequest request, int transactionId) {
        if (request.getFunctionCode() == 0x65)                       // 厂商功能码
            return encodeVendorFrame(request, transactionId);       // 你的私有帧
        return delegate.encode(request, transactionId);              // 其余走标准 MBAP
    }

    @Override
    public ModbusFrame decode(ByteBuf in) {
        ModbusFrame frame = decodeVendorFrame(in);                   // 先解析私有帧
        if (frame != null)
            return frame;
        return delegate.decode(in);                                  // 再走标准 TCP 帧
    }
}
```

用基于编解码器的构造器接入标准传输层：

```java
ModbusTransport transport = new NettyTransport(params, new VendorCodec(), /*synchronous=*/ false,
        new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1), null);
ModbusMaster master = new TcpMaster(transport, true);
```

完整的可运行示例见 `samples/CustomCodecSample`。

### 3) 自定义 Netty 管道 —— 实现 `ChannelPipelineCustomizer`

TLS、通信前身份认证与流量过滤都放在管道层。自定义器先于 Modbus 帧编解码器执行，因此自定义 handler 看到的是原始字节：

```java
ChannelPipelineCustomizer customizer = pipeline -> {
    // (a) 先加 TLS —— 其下所有内容都运行在加密通道上
    SslContext ssl = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();
    pipeline.addLast("ssl", ssl.newHandler(pipeline.channel().alloc()));

    // (b) 通信前身份握手 —— 成功时自移除，失败时关闭连接
    pipeline.addLast("auth", new AuthHandshakeHandler());
};

ModbusTransport transport = new NettyTransport(params, ModbusCodecType.TCP, false,
        new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1), customizer);
ModbusMaster master = new TcpMaster(transport, true);
```

`AuthHandshakeHandler` 属于应用代码：发送问候、校验对端挑战后 `ctx.pipeline().remove(this)` 放行 Modbus 帧。若需在认证完成前阻塞发送，可增加一个薄门（如 `isAuthenticated()`）在 `send` 前拦截。

### 4) 自定义传输层 —— 实现 `ModbusTransport`

管道无法表达的场景（UDP、连接池、虚拟多从站通道）需要实现完整接口，并交给 master 子类：

```java
public class MyModbusMaster extends ModbusMaster {
    public MyModbusMaster(ModbusTransport transport, boolean validateResponse) {
        super(transport, validateResponse);
    }
}

ModbusMaster master = new MyModbusMaster(new MyTransport(), true);
```

无论底层传输如何，master 都保留全部高层行为（`getValue`、`setValue`、`send(BatchRead)`、轮询）。

### 5) 自定义值类型 —— 继承 `BaseLocator<T>`

不触碰协议即可增加领域特定解码：

```java
public class BcdTimeLocator extends BaseLocator<Long> {
    public BcdTimeLocator(int slaveId, int offset) {
        super(slaveId, RegisterRange.HOLDING_REGISTER, offset);
    }
    @Override public int getDataType() { return 0x7001; }           // 应用自定义
    @Override public int getRegisterCount() { return 3; }
    @Override public Long bytesToValueRealOffset(byte[] data, int offset) { /* BCD → 时间戳 */ }
    @Override public short[] valueToShorts(Long value) { /* 时间戳 → BCD */ }
}
```

### 组合示例（厂商变体：自定义功能码 + TLS + 认证）

```java
ChannelPipelineCustomizer pipeline = p -> {
    p.addLast("ssl",  sslContext.newHandler(p.channel().alloc()));
    p.addLast("auth", new AuthHandshakeHandler());
};
ModbusCodec codec = new VendorCodec();                          // 自定义 FC 0x16 帧
ModbusTransport transport = new NettyTransport(params, codec, false,
        new AdaptiveConcurrency(1, 32, 100_000_000L, 0.1), pipeline);
ModbusMaster master = new TcpMaster(transport, true);
master.init();
master.getTransport().send(new MaskWriteRegisterRequest(1, 0x4000, 0x00FF, 0x0010));
master.destroy();
```

## 工程结构

```
io.github.seayar.modbus4j
├── base/       DataType、FunctionCode、RegisterRange、ReadFunctionGroup、SlaveAndRange、KeyedModbusLocator
├── locator/    BaseLocator、NumericLocator、BinaryLocator、StringLocator、BatchRead、BatchResults
├── msg/        请求/响应类 + MessageUtil 注册表
├── codec/      ModbusCodec（含 TCP/RTU/ASCII 实现）、ModbusFrame
├── net/        ModbusChannelInitializer、ModbusFrameDecoder/Encoder、ModbusResponseHandler、ChannelPipelineCustomizer
├── transport/  ModbusTransport（SPI）、NettyTransport
├── ip/         TcpMaster、IpParameters
├── serial/     RtuMaster、AsciiMaster
├── concurrent/ PendingRequests、AdaptiveConcurrency、TransactionIdGenerator
├── poll/       PollTask、PollListener、PolledLocator
├── exception/  ModbusException 异常体系
└── util/       CRC/LRC、位集、字节/寄存器、十六进制工具
```

## 构建与测试

```bash
mvn package              # 编译 + 自动补齐 license 文件头（generate-sources 阶段）
mvn test                 # 360+ 个单元/集成测试，完全自包含
mvn verify               # 完整构建，含行覆盖率 >= 95% 门禁（JaCoCo）
mvn verify -Djacoco.skip=true   # 开发中可临时跳过覆盖率门禁
mvn install -DskipTests  # 安装到本地仓库，然后：
mvn -f samples/pom.xml package   # 构建可运行样例
```

测试基于 JUnit 4，使用内嵌 Netty 从站与 `EmbeddedChannel`，无需 mock 框架、无需真实设备。开发流程与规范见 [CONTRIBUTING.md](./CONTRIBUTING.md)。

## 许可证

GNU General Public License v3.0 或更高版本（见 [LICENSE](./LICENSE)）。商用用户如对本库做出修改，须以相同许可协议将修改回馈本项目。

## 路线图

- v1.0：TCP / RTU / ASCII 主站模式，完整功能码与数据类型，批量与轮询，自适应并发，自动重连，扩展 SPI，样例代码（本版本）
- v1.1：`SIX_BYTE_INT` 变体，更完善的重连退避策略
- v2.0：从站（服务器）模式

## 参考

- 协议：Modbus Application Protocol Specification V1.1b3、Modbus Messaging on TCP/IP
- 接口对齐目标：[MangoAutomation/modbus4j](https://github.com/MangoAutomation/modbus4j)
- [英文文档](./README.md) · [贡献指南](./CONTRIBUTING.md) · [更新日志](./CHANGELOG.md)
