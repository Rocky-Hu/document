# 一、ChannelOption.AUTO_READ

NioServerSocketChannel通过Selector获取到连接就绪的连接，包装成NioSocketChannel。包装完成，io.netty.bootstrap.ServerBootstrap.ServerBootstrapAcceptor的io.netty.bootstrap.ServerBootstrap.ServerBootstrapAcceptor#channelRead方法会被执行，获取到的NioSocketChannel会被注册到子EventLoopGroup中：

~~~
@Override
@SuppressWarnings("unchecked")
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    final Channel child = (Channel) msg;

    child.pipeline().addLast(childHandler);

    setChannelOptions(child, childOptions, logger);
    setAttributes(child, childAttrs);

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
}
~~~

内部执行io.netty.channel.AbstractChannel.AbstractUnsafe#register0此方法，这里会执行下面的代码

~~~
pipeline.fireChannelActive();
~~~

这个pipeline是NioSocketChannel的pipeline。接着调用io.netty.channel.DefaultChannelPipeline#fireChannelActive方法：

~~~
@Override
public final ChannelPipeline fireChannelActive() {
    AbstractChannelHandlerContext.invokeChannelActive(head);
    return this;
}
~~~

io.netty.channel.AbstractChannelHandlerContext#invokeChannelActive(io.netty.channel.AbstractChannelHandlerContext)：

~~~
static void invokeChannelActive(final AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeChannelActive();
    } else {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelActive();
            }
        });
    }
}
~~~

这里的next是HeadContext，也就是Pipeline中的第一个ChannelHandler，

io.netty.channel.AbstractChannelHandlerContext#invokeChannelActive()：

~~~
private void invokeChannelActive() {
    if (invokeHandler()) {
        try {
            ((ChannelInboundHandler) handler()).channelActive(this);
        } catch (Throwable t) {
            invokeExceptionCaught(t);
        }
    } else {
        fireChannelActive();
    }
}
~~~

io.netty.channel.DefaultChannelPipeline.HeadContext#channelActive：

~~~
public void channelActive(ChannelHandlerContext ctx) {
    ctx.fireChannelActive();

    readIfIsAutoRead();
}
~~~

现在就碰到了跟ChannelOption.AUTO_READ相关联的方法readIfIsAutoRead():

~~~
private void readIfIsAutoRead() {
    if (channel.config().isAutoRead()) {
        channel.read();
    }
}
~~~



