-XX:MaxDirectMemorySize=size用于设置New I/O(`java.nio`) direct-buffer allocations的最大大小，size的单位可以使用k/K、m/M、g/G；如果没有设置该参数则默认值为0，意味着JVM自己自动给NIO direct-buffer allocations选择最大大小。从代码java.base/jdk/internal/misc/VM.java中可以看到默认是取的Runtime.getRuntime().maxMemory()

~~~
[root@localhost ~]# jinfo -flag MaxDirectMemorySize 9210
-XX:MaxDirectMemorySize=0
~~~

From `sun.misc.VM`, it's `Runtime.getRuntime.maxMemory()`, that's what is configured with `-Xmx`. E. g. if you *don't* configure `-XX:MaxDirectMemorySize` and *do* configure `-Xmx5g`, the "default" `MaxDirectMemorySize` will also be 5 Gb, and the total heap+direct memory usage of the app may grow up to 5 + 5 = 10 Gb.

