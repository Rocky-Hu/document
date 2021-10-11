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

