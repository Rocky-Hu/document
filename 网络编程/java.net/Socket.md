Socket对程序员掩盖了网络的底层细节，如错误检测、包大小、包分解、包重传、网络地址等。

构造函数不只是创建Socket对象，还会尝试连接远程主机的socket，所以可以用这个对象确定是否允许与某个端口建立连接。

要查看一个Socket当前是否打开，需要检查两个条件，首先isConnected()要返回true，另外isClosed()要返回false。

例如：

~~~java
boolean connected = socket.isConnected() && !socket.isClosed();
~~~



