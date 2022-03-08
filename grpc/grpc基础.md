在实现分布式应用程序时，我们必须考虑两个因素：网络协议和传输载荷的编码。

高效性 + 可读性（RMI+Java原生序列化、HTTP+JSON/XML）

HTTP+JSON形式：网络传输载荷低效、接口规范松散。

gRPC最大的特点是高性能，HTTP/2+protocol buffers组合使其在性能方面具备了天然的优势，这也是gRPC广受欢迎的原因。

