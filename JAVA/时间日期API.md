# 方法命名约定

Date-Time API 在丰富的类中提供了丰富的方法。尽可能使方法名称在类之间保持一致。 例如，许多类提供了一种 `now` 的方法，该方法捕获与该类相关的当前时刻的日期或时间值。 有从允许从一个类转换到另一个类的方法。

还有关于方法名称前缀的标准化。由于 Date-Time API 中的大多数类都是不可变的， 因此 API 不包含 set 方法。(在创建之后，不可变对象的值不能改变) 下表列出了常用的前缀：

| Prefix | Method Type    | Use                                                          |
| ------ | -------------- | ------------------------------------------------------------ |
| of     | static factory | 创建工厂主要验证输入参数的实例，而不是转换它们。             |
| from   | static factory | 将输入参数转换为目标类的实例，这可能会导致输入信息丢失。     |
| parse  | static factory | 分析输入字符串以生成目标类的实例。                           |
| format | instance       | 使用指定的格式化程序来格式化时间对象中的值以生成字符串。     |
| get    | instance       | 返回目标对象状态的一部分。                                   |
| is     | instance       | 查询目标对象的状态。                                         |
| with   | instance       | 返回一个元素已更改的目标对象的副本; 类似set方法，不过是返回一个新的对象 |
| plus   | instance       | 加上时间量返回目标对象的副本。                               |
| minus  | instance       | 减去时间量返回目标对象的副本。                               |
| to     | instance       | 将此对象转换为另一种类型                                     |
| at     | instance       | 将此对象与另一个组合起来。                                   |

java.time 包中包含很多类，你的程序可以用来表示时间和日期。这是一个非常丰富的 API。基于 ISO 日期的关键入口点如下：

- Instant 提供了时间轴的机器视图。
- LocalDate, LocalTime, and LocalDateTime 提供了日期和时间的人类视图，不包含时区。
- ZoneId, ZoneRules, and ZoneOffset 描述了时区、时区规则和时区偏移。
- ZonedDateTime 表示日期和时间与时区；OffsetDateTime和OffsetTime 类分别表示日期和时间，或者时间。这些类考虑了时区偏移量。
- Duration 测量日、时、分、秒、毫秒、纳秒的时间量
- Period 测量年、月、日的时间量