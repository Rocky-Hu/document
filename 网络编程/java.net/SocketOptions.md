# SO_KEEPALIVE

When the keepalive option is set for a TCP socket and no data has been exchanged across the socket in either direction for
2 hours (NOTE: the actual value is implementation dependent), TCP automatically sends a keepalive probe to the peer. This probe is a TCP segment to which the peer must respond.

One of three responses is expected:

1. The peer responds with the expected ACK. The application is not notified (since everything is OK). TCP will send another probe following another 2 hours of inactivity.
2. The peer responds with an RST, which tells the local TCP that the peer host has crashed and rebooted. The socket is closed.
3. There is no response from the peer. The socket is closed.

The purpose of this option is to detect if the peer host crashes.

# SO_LINGER

Specify a linger-on-close timeout.  This option disables/enables immediate return from a <B>close()</B> of a TCP Socket.  Enabling this option with a non-zero Integer <I>timeout</I> means that a  close() will block pending the transmission and acknowledgement of all data written to the peer, at which point the socket is closed <I>gracefully</I>.  Upon reaching the linger timeout, the socket is closed <I>forcefully</I>, with a TCP RST. Enabling the option with a timeout of zero does a forceful close immediately. If the specified timeout value exceeds 65,535 it will be reduced to 65,535.

Valid only for TCP: SocketImpl.

# SO_OOBINLINE

When the OOBINLINE option is set, any TCP urgent data received on the socket will be received through the socket input stream.
When the option is disabled (which is the default) urgent data is silently discarded.

OOB（Out Of Band 带外数据，又称紧急数据）。

# SO_RCVBUF

Set a hint the size of the underlying buffers used by the platform for incoming network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be received over the socket. When used in get, this must return the size of the buffer actually used by the platform when receiving in data on this socket.

# SO_SNDBUF

Set a hint the size of the underlying buffers used by the platform for outgoing network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be sent over the socket. When used in get, this must return the size of the buffer actually used by the platform when sending out data on this socket.

Valid for all sockets: SocketImpl, DatagramSocketImpl

> 每个TCP socket在内核中都有一个发送缓冲区和一个接收缓冲区，TCP的全双工的工作模式以及TCP的滑动窗口便是依赖于这两个独立的buffer以及此buffer的填充状态。接收缓冲区把数据缓存入内核，应用进程一直没有调用read进行读取的话，此数据会一直缓存在相应socket的接收缓冲区内。再啰嗦一点，不管进程是否读取socket，对端发来的数据都会经由内核接收并且缓存到socket的内核接收缓冲区之中。read所做的工作，就是把内核缓冲区中的数据拷贝到应用层用户的buffer里面，仅此而已。进程调用send发送的数据的时候，最简单情况（也是一般情况），将数据拷贝进入socket的内核发送缓冲区之中，然后send便会在上层返回。换句话说，send返回之时，数据不一定会发送到对端去（和write写文件有点类似），send仅仅是把应用层buffer的数据拷贝进socket的内核发送buffer中。
>
> 每个UDP socket都有一个接收缓冲区，没有发送缓冲区，从概念上来说就是只要有数据就发，不管对方是否可以正确接收，所以不缓冲，不需要发送缓冲区。
>
> 接收缓冲区被TCP和UDP用来缓存网络上来的数据，一直保存到应用进程读走为止。对于TCP，如果应用进程一直没有读取，buffer满了之后，发生的动作是：通知对端TCP协议中的窗口关闭。这个便是滑动窗口的实现。保证TCP套接口接收缓冲区不会溢出，从而保证了TCP是可靠传输。因为对方不允许发出超过所通告窗口大小的数据。 这就是TCP的流量控制，如果对方无视窗口大小而发出了超过窗口大小的数据，则接收方TCP将丢弃它。  UDP：当套接口接收缓冲区满时，新来的数据报无法进入接收缓冲区，此数据报就被丢弃。UDP是没有流量控制的；快的发送者可以很容易地就淹没慢的接收者，导致接收方的UDP丢弃数据报。
>
> 以上便是TCP可靠，UDP不可靠的实现。
>
> 这两个选项就是来设置TCP连接的两个buffer尺寸的。

# SO_TIMEOUT

Set a timeout on blocking Socket operations: 

```
 ServerSocket.accept();
 SocketInputStream.read();
 DatagramSocket.receive();
```

The option must be set prior to entering a blocking operation to take effect.  If the timeout expires and the operation would continue to block, **java.io.InterruptedIOException** is raised.  The Socket is not closed in this case.  

Valid for all sockets: SocketImpl, DatagramSocketImpl

# TCP_NODELAY

Disable Nagle's algorithm for this connection.  Written data to the network is not buffered pending acknowledgement of previously written data.

 Valid for TCP only: SocketImpl.

