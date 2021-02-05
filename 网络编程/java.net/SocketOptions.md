# SO_KEEPALIVE

When the keepalive option is set for a TCP socket and no data has been exchanged across the socket in either direction for
2 hours (NOTE: the actual value is implementation dependent), TCP automatically sends a keepalive probe to the peer. This probe is a TCP segment to which the peer must respond.

One of three responses is expected:

1. The peer responds with the expected ACK. The application is not notified (since everything is OK). TCP will send another probe following another 2 hours of inactivity.
2. The peer responds with an RST, which tells the local TCP that the peer host has crashed and rebooted. The socket is closed.
3. There is no response from the peer. The socket is closed.

The purpose of this option is to detect if the peer host crashes.

如果将这个参数这是为True，客户端每隔一段时间（一般不少于2小时）就像服务器发送一个试探性的数据包，服务器一般会有三种回应：

   1、服务器正常回一个ACK，这表明远程服务器一切OK，那么客户端不会关闭连接，而是再下一个2小时后再发个试探包。

   2、服务器返回一个RST，这表明远程服务器挂了，这时候客户端会关闭连接。

   3、如果服务器未响应这个数据包，在大约11分钟后，客户端Socket再发送一个数据包，如果在12分钟内，服务器还没响应，那么客户端Socket将关闭。

# SO_LINGER

**SO_LINGER选项指定了Socket关闭时如何处理尚未发送的数据报。**

Specify a linger-on-close timeout.  This option disables/enables immediate return from a <B>close()</B> of a TCP Socket.  Enabling this option with a non-zero Integer <I>timeout</I> means that a  close() will block pending the transmission and acknowledgement of all data written to the peer, at which point the socket is closed <I>gracefully</I>.  Upon reaching the linger timeout, the socket is closed <I>forcefully</I>, with a TCP RST. Enabling the option with a timeout of zero does a forceful close immediately. If the specified timeout value exceeds 65,535 it will be reduced to 65,535.

Valid only for TCP: SocketImpl.

 先看Socket源代码：

~~~java
/**
     * Enable/disable SO_LINGER with the specified linger time in seconds. 
     * The maximum timeout value is platform specific.
     *
     * The setting only affects socket close.
     * 
     * @param on     whether or not to linger on.
     * @param linger how long to linger for, if on is true.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     * @exception IllegalArgumentException if the linger value is negative.
     * @since JDK1.1
     * @see #getSoLinger()
*/
public void setSoLinger(boolean on, int linger) throws SocketException {
    if (isClosed())
        throw new SocketException("Socket is closed");
    if (!on) {
        getImpl().setOption(SocketOptions.SO_LINGER, new Boolean(on));
    } else {
        if (linger < 0) {
        throw new IllegalArgumentException("invalid value for SO_LINGER");
        }
            if (linger > 65535)
                linger = 65535;
        getImpl().setOption(SocketOptions.SO_LINGER, new Integer(linger));
    }
}
~~~

这个字段对Socket的close方法产生影响，当这个字段设置为false时，close会立即执行并返回，如果这时仍然有未送出的数据包，那么这些数据包将被丢弃。如果设置为True时，有一个延迟时间可以设置。这个延迟的时间就是close真正执行所有等待时间，最大为65535。

# SO_OOBINLINE

When the OOBINLINE option is set, any TCP urgent data received on the socket will be received through the socket input stream.
When the option is disabled (which is the default) urgent data is silently discarded.

OOB（Out Of Band 带外数据，又称紧急数据）。

如果这个Socket选项打开，可以通过Socket类的sendUrgentData方法向服务器发送一个单字节的数据。这个单字节数据并不经过输出缓冲区，而是立即发出。虽然在客户端并不是使用OutputStream向服务器发送数据，但在服务端程序中这个单字节的数据是和其它的普通数据混在一起的。因此，在服务端程序中并不知道由客户端发过来的数据是由OutputStream还是由sendUrgentData发过来的。

# SO_RCVBUF

Set a hint the size of the underlying buffers used by the platform for incoming network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be received over the socket. When used in get, this must return the size of the buffer actually used by the platform when receiving in data on this socket.

在默认情况下，输入流的接收缓冲区是8096个字节（8K）。这个值是Java所建议的输入缓冲区的大小。如果这个默认值不能满足要求，可以用setReceiveBufferSize方法来重新设置缓冲区的大小。

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

在默认情况下，输出流的发送缓冲区是8096个字节（8K）。这个值是Java所建议的输出缓冲区的大小。如果这个默认值不能满足要求，可以用setSendBufferSize方法来重新设置缓冲区的大小。

# SO_TIMEOUT

Set a timeout on blocking Socket operations: 

```
 ServerSocket.accept();
 SocketInputStream.read();
 DatagramSocket.receive();
```

The option must be set prior to entering a blocking operation to take effect.  If the timeout expires and the operation would continue to block, **java.io.InterruptedIOException** is raised.  The Socket is not closed in this case.  

Valid for all sockets: SocketImpl, DatagramSocketImpl

~~~java
/**
     *  Enable/disable SO_TIMEOUT with the specified timeout, in
     *  milliseconds.  With this option set to a non-zero timeout,
     *  a read() call on the InputStream associated with this Socket
     *  will block for only this amount of time.  If the timeout expires,
     *  a <B>java.net.SocketTimeoutException</B> is raised, though the
     *  Socket is still valid. The option <B>must</B> be enabled
     *  prior to entering the blocking operation to have effect. The
     *  timeout must be > 0.
     *  A timeout of zero is interpreted as an infinite timeout.
     * @param timeout the specified timeout, in milliseconds.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     * @since   JDK 1.1
     * @see #getSoTimeout()
*/
public synchronized void setSoTimeout(int timeout) throws SocketException {
    if (isClosed())
        throw new SocketException("Socket is closed");
    if (timeout < 0)
      throw new IllegalArgumentException("timeout can't be negative");

    getImpl().setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
}
~~~

这个参数用来控制客户端读取socket数据的超时时间，如果timeout设置为0，那么就一直阻塞，否则阻塞直到超时后直接抛出超时异常。

# SO_NODELAY

Disable Nagle's algorithm for this connection.  Written data to the network is not buffered pending acknowledgement of previously written data.

 Valid for TCP only: SocketImpl.

要理解这个参数，首先要理解Nagle算法，下面先说说这个Nagle算法：

**Nagle算法产生的背景**

当网络传输中存在大量小包传输时，会严重影响传输效率。比如一个包，包头40字节，而真正的内容只有一个字节或者几个字节（典型的有Tlenet），这样的传输效率是十分低下的。Nagle算法要解决的就是这种效率低下的传输问题。

**Nagle算法的原理**

用通俗的话来说就是，把小包要发送的字节先缓存，当到达一定的阈值的时候再一次性传输。具体算法（伪代码）如下：

~~~
if there is new data to send
  if the window size >= MSS and available data is >= MSS
    send complete MSS segment now
  else
    if there is unconfirmed data still in the pipe
      enqueue data in the buffer until an acknowledge is received
    else
      send data immediately
    end if
  end if
end if
~~~

其中MSS为maximum segment size的缩写，是TCP头部的一个字段，表示一个TCP段最大的数据承载量。

**Nagle算法的问题**

在传输大文件的时候，如果使用这个算法，那么会出现明显的延迟现象，因此，在这种情况下，最好是关闭这个算法。

知道了Nagle算法，就知道了TCP_NODELAY这个参数的意义了，如果这个参数被设置为True，那么就是关闭Nagle算法，实现无延迟传输，如果设置为false，则是打开这个算法，会对发送的数据进行缓存。

# SO_REUSEADDR



