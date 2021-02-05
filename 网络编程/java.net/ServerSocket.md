ServerSocket曾经绑定到某个端口，即使它目前已关闭，isBound()仍会返回true。如果需要测试ServerSocket是否打开，就必须同时检查isBound()返回true，而且isClosed()返回false。例如：

~~~java
public static boolean isOpen(ServerSocket ss) {
	return ss.isBound() && !ss.isClosed();
}
~~~

