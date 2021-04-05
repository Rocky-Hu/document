CompletionService 的应用场景还是非常多的，比如

- Dubbo 中的 Forking Cluster
- 多仓库文件/镜像下载（从最近的服务中心下载后终止其他下载过程）
- 多服务调用（天气预报服务，最先获取到的结果）

`Future get()` 方法的致命缺陷:

> 如果 Future 结果没有完成，调用 get() 方法，程序会**阻塞**在那里，直至获取返回结果

