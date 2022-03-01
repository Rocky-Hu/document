# 一、ChannelOption.AUTO_READ

控制逻辑如下：

~~~
io.netty.channel.DefaultChannelPipeline.HeadContext#channelActive
@Override
public void channelActive(ChannelHandlerContext ctx) {
    ctx.fireChannelActive();

    readIfIsAutoRead();
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

其实就是是否设置感兴趣事件。

AUTO_READ为true，那么Netty在创建Channel之后，自动帮我们设置感兴趣事件，那么对于NioServerSocketChannel在有新的TCP连接就绪的时候就会得到通知，对于NioSocketChannel，当它的连接缓冲区中可读或可写的时候得到通知。

那么如果我们将AUTO_READ设置为false，一般是对NioSocketChannel设置为false，也就是：

~~~
.childOption(ChannelOption.AUTO_READ, false)
~~~

这样设置。那么当数据可读的时候，那channel是得不到通知的，我们可以手动调用：

~~~
io.netty.channel.Channel#read
~~~

read方法，来开启读操作，这个就是和自动开启一样的执行逻辑了，自动开启中有这样的一段代码：

~~~
io.netty.channel.DefaultChannelPipeline.HeadContext#readIfIsAutoRead
private void readIfIsAutoRead() {
    if (channel.config().isAutoRead()) {
        channel.read();
    }
}
~~~

也是调用的channel的read方法。

总结：也就是自动和手动设置感兴趣的事件的区别。





