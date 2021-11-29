所以默认情况下，可以申请的DirectByteBuffer大小为Runtime.getRuntime().maxMemory()，而这个值等于可用的最大Java堆大小，也就是我们-Xmx参数指定的值。

# 一、分配

# 二、回收

## 2.1. 虚引用（PhantomReference）

