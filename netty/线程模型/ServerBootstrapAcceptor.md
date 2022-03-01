ServerBootstrap进行初始化时有下面的这个操作：

io.netty.bootstrap.ServerBootstrap#init

~~~
p.addLast(new ChannelInitializer<Channel>() {
    @Override
    public void initChannel(final Channel ch) {
        final ChannelPipeline pipeline = ch.pipeline();
        ChannelHandler handler = config.handler();
        if (handler != null) {
            pipeline.addLast(handler);
        }

        ch.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                pipeline.addLast(new ServerBootstrapAcceptor(
                ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
            }
        });
    }
});
~~~

ServerBootstrapAcceptor本身就是一个ChannelInboundHandler。感兴趣的是channelRead事件。

这个Channel是NioServerSocketChannel。NioServerSocketChannel是对java NIO ServerSocketChannel的封装，内部会调用Selector的select方法阻塞获取新的客户端连接，当有客户端连接进来之后，通过io.netty.channel.nio.NioEventLoop#processSelectedKey(java.nio.channels.SelectionKey, io.netty.channel.nio.AbstractNioChannel)这个方法的处理，触发io.netty.channel.nio.AbstractNioMessageChannel.NioMessageUnsafe#read方法。

> 注意，这里Channel一直都是NioServerSocketChannel

然后通过io.netty.channel.socket.nio.NioServerSocketChannel#doReadMessages此方法获取到客户端连接，

~~~
SocketChannel ch = SocketUtils.accept(javaChannel());
->
public static SocketChannel accept(final ServerSocketChannel serverSocketChannel) throws IOException {
    try {
    	return AccessController.doPrivileged(new PrivilegedExceptionAction<SocketChannel>() {
            @Override
            public SocketChannel run() throws IOException {
                return serverSocketChannel.accept();
            }
    	});
    } catch (PrivilegedActionException e) {
    	throw (IOException) e.getCause();
    }
}
~~~

这里我们就看到了NIO看到了NIO中熟悉的accept方法。

~~~
java.nio.channels.ServerSocketChannel#accept
~~~

得到的SocketChannel会被封装到NioSocketChannel中。获取到连接之后，就会触发ChannelRead事件：

~~~
int size = readBuf.size();
for (int i = 0; i < size; i ++) {
	readPending = false;
	pipeline.fireChannelRead(readBuf.get(i));
}
~~~

这里pipeline是NioServerSocketChannel关联的pipeline，内容如下：

~~~
DefaultChannelPipeline{(LoggingHandler#0 = io.netty.handler.logging.LoggingHandler), (ServerBootstrap$ServerBootstrapAcceptor#0 = io.netty.bootstrap.ServerBootstrap$ServerBootstrapAcceptor)}
~~~

可以看到pipe line中包含了ServerBootstrapAcceptor。接下来就是执行ServerBoostrapAcceptor的channelRead方法。

这里有个比较重要的东西，得到的连接被封装到了NioSocketChannel中，它同样会关联一个pipe line， pipe line中也会放置一些ChannelHandler，那么这些ChannelHandler是什么，什么时候设置进去的呢。

回到上面初始化的方法：

~~~
p.addLast(new ChannelInitializer<Channel>() {
    @Override
    public void initChannel(final Channel ch) {
        final ChannelPipeline pipeline = ch.pipeline();
        ChannelHandler handler = config.handler();
        if (handler != null) {
            pipeline.addLast(handler);
        }

        ch.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                pipeline.addLast(new ServerBootstrapAcceptor(
                ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
            }
        });
    }
});
~~~

在创建ServerBootstrapAcceptor的时候设置了channel handler，这个设置是在这里：

~~~
public ServerBootstrap childHandler(ChannelHandler childHandler) {
	this.childHandler = ObjectUtil.checkNotNull(childHandler, "childHandler");
	return this;
}
~~~

调用是在这里：

~~~
b.group(bossGroup, workerGroup)
	.channel(NioServerSocketChannel.class)
	.handler(new LoggingHandler(LogLevel.INFO))
	.childHandler(new HexDumpProxyInitializer(REMOTE_HOST, REMOTE_PORT))
	.childOption(ChannelOption.AUTO_READ, false)
.bind(LOCAL_PORT).sync().channel().closeFuture().sync();
~~~

回到ServerBootstrapAcceptor的channelRead方法，得到的NioSocketChannel会被注册到子EventLoopGroup组中：

~~~
 try {
 	childGroup.register(child).addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    forceClose(child, future.cause());
                }
        }
     });
 } catch (Throwable t) {
 	forceClose(child, t);
 }
~~~

注册的逻辑就类似于这样：

~~~
client.configureBlocking(false);
client.register(selector, SelectionKey.OP_READ);
~~~



   