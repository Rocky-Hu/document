# 一、连接建立和终止

为了建立连接，TCP使用三次握手。在客户端尝试与服务器连接之前，服务器必须首先绑定并侦听端口以打开其连接：这称为被动打开。一旦建立了被动打开，客户端就可以发起主动打开。要建立连接，需要进行三次（或三步）握手：

- **SYNC**：The active open is performed by the client sending a SYN to the server. The client sets the segment's sequence number to a random value A.
- **SYN-ACK**: In response, the server replies with a SYN-ACK. The acknowledgment number is set to one more than the received sequence number i.e. A+1, and the sequence number that the server chooses for the packet is another random number, B.
- **ACK**: Finally, the client sends an ACK back to the server. The sequence number is set to the received acknowledgement value i.e. A+1, and the acknowledgement number is set to one more than the received sequence number i.e. B+1.

![](../../images/TCP连接建立和终止.jpg)

# 二、TCP为什么是四次挥手，而不是三次挥手

因为TCP是全双工通信的。

   （1）第一次挥手

​     因此当主动方发送断开连接的请求（即FIN报文）给被动方时，仅仅代表主动方不会再发送数据报文了，但主动方仍可以接收数据报文。

​    （2）第二次挥手

​     被动方此时有可能还有相应的数据报文需要发送，因此需要先发送ACK报文，告知主动方“我知道你想断开连接的请求了”。这样主动方便不会因为没有收到应答而继续发送断开连接的请求（即FIN报文）。

   （3）第三次挥手

​    被动方在处理完数据报文后，便发送给主动方FIN报文；这样可以保证数据通信正常可靠地完成。发送完FIN报文后，被动方进入LAST_ACK阶段（超时等待）。

   （4）第四挥手

​    如果主动方及时发送ACK报文进行连接中断的确认，这时被动方就直接释放连接，进入可用状态。
