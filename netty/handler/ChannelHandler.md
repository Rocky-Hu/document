# ChannelHandler

# ChannelInboundHandler

ChannelHandler which adds callbacks（回调） for state changes. This allows the user to hook in to state changes easily.

客户端连接：

~~~mermaid
graph LR
	handlerAdded --> channelRegistered --> channelActive
~~~

~~~
======回调执行handlerAdded，Channel被添加到上下文======
======回调执行channelRegistered，Channel被注册到EventLoop中======
======回调执行channelActive======
~~~

客户端写入数据：

~~~mermaid
graph LR
	 接着上面 --> channelRead --> channelReadComplete
~~~

~~~
======回调执行channelRead======
======回调执行channelReadComplete======
~~~

客户端断开：

~~~mermaid
graph LR
	channelRead --> channelReadComplete --> channelInactive --> channelUnregistered --> handlerRemoved
~~~

~~~
======回调执行channelRead======
======回调执行channelReadComplete======
======回调执行channelReadComplete======
======回调执行channelInactive======
======回调执行channelUnregistered======
======回调执行handlerRemoved，Channel被从上下文移除======
~~~







