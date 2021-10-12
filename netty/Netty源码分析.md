# 一、ServerSocketChannel的注册

Nio中对于接受客户端连接的Channel，模板代码如下：

~~~
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.configureBlocking(false);

ServerSocket serverSocket = serverChannel.socket();
serverSocket.bind(new InetSocketAddress(1234));

Selector selector = Selector.open();
serverChannel.register(selector, SelectionKey.OP_ACCEPT);
~~~

这里可以看到要将ServerSocketChannel注册到Selector中，感兴趣的事件为OP_ACCEPT事件。

Netty是对NIO的封装，其代码内部必然也会有上面的这段逻辑。

io.netty.channel.nio.AbstractNioChannel#doRegister

# 二、NioEventLoop中的Selector是怎么创建的

io.netty.channel.nio.NioEventLoop#NioEventLoop

# 三、Selector的select方法是什么时候被调用的

io.netty.channel.nio.NioEventLoop#run

# 四、ServerSocketChannel的accept方法是什么时候被调用的

io.netty.channel.nio.NioEventLoop#processSelectedKey(java.nio.channels.SelectionKey, io.netty.channel.nio.AbstractNioChannel)

->

io.netty.channel.nio.AbstractNioMessageChannel.NioMessageUnsafe#read

->

io.netty.channel.socket.nio.NioServerSocketChannel#doReadMessages

->

io.netty.util.internal.SocketUtils#accept

# 五、ChannelPipeline是什么时候创建的

Each channel has its own pipeline and it is created automatically when a new channel is created.

# 六、事件触发时机

## fireChannelRegistered

io.netty.channel.AbstractChannel.AbstractUnsafe#register0

## fireChannelActive

io.netty.channel.DefaultChannelPipeline#fireChannelActive

# 七、NioServerSocketChannel的read和NioSocketChannel的read

NioEventLoop的io.netty.channel.nio.NioEventLoop#processSelectedKey(java.nio.channels.SelectionKey, io.netty.channel.nio.AbstractNioChannel)方法，处理OP_ACCEPT和OP_READ用到的是同一段代码：

~~~
// Also check for readOps of 0 to workaround possible JDK bug which may otherwise lead
// to a spin loop
if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
	unsafe.read();
}
~~~

这里的unsafe实例为：

~~~
final AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
~~~

NioUnsafe接口的定义如下：

~~~
/**
* Special {@link Unsafe} sub-type which allows to access the underlying {@link SelectableChannel}
*/
public interface NioUnsafe extends Unsafe {
    /**
    * Return underlying {@link SelectableChannel}
    */
    SelectableChannel ch();

    /**
    * Finish connect
    */
    void finishConnect();

    /**
    * Read from underlying {@link SelectableChannel}
    */
    void read();

    void forceFlush();
}
~~~

上面说到OP_ACCEPT和OP_READ事件都调用的是unsafe的read方法，这里面对于不同的Socket Channel，执行逻辑是不一样的，ServerSocketChannel感兴趣的是OP_ACCEPT，那么read方法的逻辑就是从就绪队列中获取已经准备就绪的连接，而对于SocketChannel感兴趣的是OP_READ事件，那么read方法就是真正的进行数据的读取。Netty的NioServerSocketChannel和NioSocketChannel分别是对NIO的ServerSocketChannel和SocketChannel的封装，那么对于NioUnsafe接口就会有不同的实现。

NioServerSocketChannel继承了AbstractNioMessageChannel，AbstractNioMessageChannel中NioUnsafe的实现为io.netty.channel.nio.AbstractNioMessageChannel.NioMessageUnsafe，read方法的实现如下：

~~~
public void read() {
            assert eventLoop().inEventLoop();
            final ChannelConfig config = config();
            final ChannelPipeline pipeline = pipeline();
            final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
            allocHandle.reset(config);

            boolean closed = false;
            Throwable exception = null;
            try {
                try {
                    do {
                        int localRead = doReadMessages(readBuf);
                        if (localRead == 0) {
                            break;
                        }
                        if (localRead < 0) {
                            closed = true;
                            break;
                        }

                        allocHandle.incMessagesRead(localRead);
                    } while (continueReading(allocHandle));
                } catch (Throwable t) {
                    exception = t;
                }

                int size = readBuf.size();
                for (int i = 0; i < size; i ++) {
                    readPending = false;
                    pipeline.fireChannelRead(readBuf.get(i));
                }
                readBuf.clear();
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();

                if (exception != null) {
                    closed = closeOnReadError(exception);

                    pipeline.fireExceptionCaught(exception);
                }

                if (closed) {
                    inputShutdown = true;
                    if (isOpen()) {
                        close(voidPromise());
                    }
                }
            } finally {
                // Check if there is a readPending which was not processed yet.
                // This could be for two reasons:
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
                //
                // See https://github.com/netty/netty/issues/2254
                if (!readPending && !config.isAutoRead()) {
                    removeReadOp();
                }
            }
        }
~~~

NioSocketChannel继承了AbstractNioByteChannel类，AbstractNioByteChannel中NioUnsafe的实现为io.netty.channel.nio.AbstractNioByteChannel.NioByteUnsafe，read方法的逻辑为：

~~~
@Override
        public final void read() {
            final ChannelConfig config = config();
            if (shouldBreakReadReady(config)) {
                clearReadPending();
                return;
            }
            final ChannelPipeline pipeline = pipeline();
            final ByteBufAllocator allocator = config.getAllocator();
            final RecvByteBufAllocator.Handle allocHandle = recvBufAllocHandle();
            allocHandle.reset(config);

            ByteBuf byteBuf = null;
            boolean close = false;
            try {
                do {
                    byteBuf = allocHandle.allocate(allocator);
                    allocHandle.lastBytesRead(doReadBytes(byteBuf));
                    if (allocHandle.lastBytesRead() <= 0) {
                        // nothing was read. release the buffer.
                        byteBuf.release();
                        byteBuf = null;
                        close = allocHandle.lastBytesRead() < 0;
                        if (close) {
                            // There is nothing left to read as we received an EOF.
                            readPending = false;
                        }
                        break;
                    }

                    allocHandle.incMessagesRead(1);
                    readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                    byteBuf = null;
                } while (allocHandle.continueReading());

                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();

                if (close) {
                    closeOnRead(pipeline);
                }
            } catch (Throwable t) {
                handleReadException(pipeline, byteBuf, t, close, allocHandle);
            } finally {
                // Check if there is a readPending which was not processed yet.
                // This could be for two reasons:
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
                //
                // See https://github.com/netty/netty/issues/2254
                if (!readPending && !config.isAutoRead()) {
                    removeReadOp();
                }
            }
        }
~~~

# 八、NioServerSocketChannel和NioSocketChannel分别是在什么地方创建的

NioServerSocketChannel:

~~~
io.netty.bootstrap.AbstractBootstrap#initAndRegister

final ChannelFuture initAndRegister() {
    Channel channel = null;
    try {
        channel = channelFactory.newChannel();
        init(channel);
    } catch (Throwable t) {
        if (channel != null) {
            // channel can be null if newChannel crashed (eg SocketException("too many open files"))
            channel.unsafe().closeForcibly();
            // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
            return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
        }
        // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
        return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
    }

    ChannelFuture regFuture = config().group().register(channel);
    if (regFuture.cause() != null) {
        if (channel.isRegistered()) {
            channel.close();
        } else {
            channel.unsafe().closeForcibly();
        }
    }

    // If we are here and the promise is not failed, it's one of the following cases:
    // 1) If we attempted registration from the event loop, the registration has been completed at this point.
    //    i.e. It's safe to attempt bind() or connect() now because the channel has been registered.
    // 2) If we attempted registration from the other thread, the registration request has been successfully
    //    added to the event loop's task queue for later execution.
    //    i.e. It's safe to attempt bind() or connect() now:
    //         because bind() or connect() will be executed *after* the scheduled registration task is executed
    //         because register(), bind(), and connect() are all bound to the same thread.

    return regFuture;
}
~~~

通过反射进行创建。

NioSocketChannel：

~~~
io.netty.channel.socket.nio.NioServerSocketChannel#doReadMessages

@Override
protected int doReadMessages(List<Object> buf) throws Exception {
    SocketChannel ch = SocketUtils.accept(javaChannel());

    try {
        if (ch != null) {
            buf.add(new NioSocketChannel(this, ch));
            return 1;
        }
    } catch (Throwable t) {
        logger.warn("Failed to create a new channel from an accepted socket.", t);

        try {
            ch.close();
        } catch (Throwable t2) {
            logger.warn("Failed to close a socket.", t2);
        }
    }

    return 0;
}
~~~

# 九、Channel是什么时候注册到Selector的

io.netty.channel.nio.AbstractNioChannel#doRegister

~~~
/**
 * Is called after the {@link Channel} is registered with its {@link EventLoop} as part of the register process.
 *
 * Sub-classes may override this method
 */
protected void doRegister() throws Exception {
    // NOOP
}
~~~

调用时机：

~~~
@Override
protected void doRegister() throws Exception {
    boolean selected = false;
    for (;;) {
        try {
            selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);
            return;
        } catch (CancelledKeyException e) {
            if (!selected) {
                // Force the Selector to select now as the "canceled" SelectionKey may still be
                // cached and not removed because no Select.select(..) operation was called yet.
                eventLoop().selectNow();
                selected = true;
            } else {
                // We forced a select operation on the selector before but the SelectionKey is still cached
                // for whatever reason. JDK bug ?
                throw e;
            }
        }
    }
}
~~~

# 十、Channel感兴趣的操作是什么时候设置的

NIO中Channel往Selector注册的示例代码如下：

~~~
 ServerSocketChannel serverChannel = ServerSocketChannel.open();
 serverChannel.configureBlocking(false);

 ServerSocket serverSocket = serverChannel.socket();
 serverSocket.bind(new InetSocketAddress(1234));

 Selector selector = Selector.open();
 serverChannel.register(selector, SelectionKey.OP_ACCEPT);
~~~

从这里可以看到在调用register的时候就把感兴趣的事件设置了，但是上一小节可以看到在往selector注册的时候，是没有设置感兴趣的操作的。

那么执行register的时候，这个ops的设置到底是做了什么操作呢：

~~~
k.interestOps(ops);

这个方法可以在任何时候调用，方法的说明如下：

This method may be invoked at any time.  Whether or not it blocks,  and for how long, is implementation-dependent.
~~~

其实就是做这个操作。

那么如果Channel没有设置这个感兴趣的事件值会发生什么。比如说对于ServerSocketChannel，它对连接就绪事件是感兴趣的，这里我们没有设置OP_ACCEPT，会发生什么情况。

看Selector文档中的这句话：

~~~
The underlying operating system is queried for an update as to the readiness of each remaining channel to perform any of the operations identified by its key's interest set as of the moment that the selection operation began. 
~~~

这个具体是什么含义呢，简单的来说：拿一个channel来说，底层的操作系统会识别这个channel感兴趣的事件，比如说OP_ACCEPT，然后就会去连接就绪队列中检查是否有就绪的连接，如果有那么就将这个channel对应的SelectionKey中readOps设置为OP_ACCEET，表示已经就绪的事件，然后把这个SelectionKey添加到selectedKeys集合中然后返回。如果没有设置感兴趣的事件，那么对于这个channel就不会以这种方式得到事件通知。

现在回到我们的问题，在上一小节分析Netty中Channel注册的时候，我们看到是没有设置ops的，那么根据上面的分析，这个NioServerSocketChannel在有客户端连接进来的时候是不会得到通知的。在代码中的某个地方，肯定是有这个设置的。这里说一下，在创建NioServerSocketChannel和NioSocketChannel的时候是分别定义了它们感兴趣的事件的：

~~~java
io.netty.channel.socket.nio.NioServerSocketChannel#NioServerSocketChannel(java.nio.channels.ServerSocketChannel)

public NioServerSocketChannel(ServerSocketChannel channel) {
       super(null, channel, SelectionKey.OP_ACCEPT);
       config = new NioServerSocketChannelConfig(this, javaChannel().socket());
}

io.netty.channel.socket.nio.NioSocketChannel#NioSocketChannel(io.netty.channel.Channel, java.nio.channels.SocketChannel)
public NioSocketChannel(Channel parent, SocketChannel socket) {
        super(parent, socket);
        config = new NioSocketChannelConfig(this, socket.socket());
}
->
io.netty.channel.nio.AbstractNioByteChannel#AbstractNioByteChannel
protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
        super(parent, ch, SelectionKey.OP_READ);
}
~~~

可以看到NioServerSocketChannel预定义的感兴趣事件是OP_ACCEPT，NioSocketChannel预定义的感兴趣的事件是OP_READ。

现在就来看，感兴趣事件是在哪设置的，这里通过调试找到了设置的时机，在channelActive事件触发的时候，channelActive事件触发，然后就执行pipeline流程，而pipeline中第一个inboud handler是HeadContext，它的channelActive方法如下：

~~~
io.netty.channel.DefaultChannelPipeline.HeadContext#channelActive
@Override
public void channelActive(ChannelHandlerContext ctx) {
    ctx.fireChannelActive();

    readIfIsAutoRead();
}

io.netty.channel.DefaultChannelPipeline.HeadContext#readIfIsAutoRead
private void readIfIsAutoRead() {
    if (channel.config().isAutoRead()) {
        channel.read();
    }
}

io.netty.channel.AbstractChannel#read
@Override
public Channel read() {
    pipeline.read();
    return this;
}

io.netty.channel.DefaultChannelPipeline.HeadContext#read
@Override
public void read(ChannelHandlerContext ctx) {
    unsafe.beginRead();
}

io.netty.channel.AbstractChannel.AbstractUnsafe#beginRead
@Override
public final void beginRead() {
    assertEventLoop();

    try {
        doBeginRead();
    } catch (final Exception e) {
        invokeLater(new Runnable() {
            @Override
            public void run() {
                pipeline.fireExceptionCaught(e);
            }
        });
        close(voidPromise());
    }
}

io.netty.channel.nio.AbstractNioChannel#doBeginRead
@Override
protected void doBeginRead() throws Exception {
    // Channel.read() or ChannelHandlerContext.read() was called
    final SelectionKey selectionKey = this.selectionKey;
    if (!selectionKey.isValid()) {
        return;
    }

    readPending = true;

    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
~~~

这里readInterestOp就是上面在创建NioServerSocketChannel和NioSocketChannel时预定义的感兴趣的值。

# 十一、ChannelOutboundHandler为什么有read方法

~~~
Inbound handlers are supposed to handle inbound events. Events are triggered by external stimuli such as data received from a socket.

Outbound handlers are supposed to intercept the operations issued by your application.

Re: Q1) read() is an operation you can issue to tell Netty to continue reading the inbound data from the socket, and that's why it's in an outbound handler.

Re: Q2) You don't usually issue a read() operation because Netty does that for you automatically if autoRead property is set to true. Typical flow when autoRead is on:

Netty triggers an inbound event channelActive when socket is connected, and then issues a read() request to itself (see DefaultChannelPipeline.fireChannelActive())
Netty reads something from the socket in response to the read() request.
If something was read, Netty triggers channelRead().
If there's nothing left to read, Netty triggers channelReadComplete()
Netty issues another read() request to continue reading from the socket.
If autoRead is off, you have to issue a read() request manually. It's sometimes useful to turn autoRead off. For example, you might want to implement a backpressure mechanism by keeping the received data in the kernel space.
~~~





