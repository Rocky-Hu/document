# 一、EventExecutorGroup

EventExecutor组。

# 二、EventExecutor

It's basically as and `EventExecutor` is just `EventExecutorGroup` that only contains itself. Also this allows to re-use an `EventExecutor` where you are required to pass and `EventExecutorGroup`.

This comes in handy in many different situations like for example it allows you to create a `Bootstrap` and use one `EventLoop` as its `EventLoopGroup` and so ensure all the I/O is handled by the same thread. This is super useful for example when you build a proxy.

