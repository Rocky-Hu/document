https://docs.oracle.com/javase/7/docs/technotes/tools/share/jmap.html

# 1. 全称

Memory Map for Java（Java内存映象工具）

# 2. 功能

生成堆转储快照（一般称为heapdump或dump文件）。

# 3. 使用

## 案例一：**查看堆内存的配置和使用情况**

~~~
C:\Users\87490\Desktop> jmap -heap 4288
Attaching to process ID 4288, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.301-b09

using thread-local object allocation.
Garbage-First (G1) GC with 10 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40                                                  //JVM堆缩减空间比率，低于则进行内存缩减
   MaxHeapFreeRatio         = 70                                                  //JVM堆扩大内存空闲比例，高于则进行内存扩张 
   MaxHeapSize              = 2147483648 (2048.0MB)                               //堆最大内存
   NewSize                  = 1363144 (1.2999954223632812MB)                      //新生代初始化内存大小
   MaxNewSize               = 1287651328 (1228.0MB)                               //新生代最大内存大小
   OldSize                  = 5452592 (5.1999969482421875MB)                      //老年代内存大小
   NewRatio                 = 2                                                   //新生代和老年代占堆内存比率
   SurvivorRatio            = 8                                                   //s区和Eden区占新生代内存比率
   MetaspaceSize            = 226492416 (216.0MB)                                 //元数据初始化空间大小
   CompressedClassSpaceSize = 528482304 (504.0MB)                                 //类指针压缩空间大小
   MaxMetaspaceSize         = 536870912 (512.0MB)                                 //元数据最大内存代销   
   G1HeapRegionSize         = 1048576 (1.0MB)                                     //G1收集器Region单元大小

Heap Usage:
G1 Heap:
   regions  = 2048
   capacity = 2147483648 (2048.0MB)
   used     = 101605872 (96.89891052246094MB)
   free     = 2045877776 (1951.101089477539MB)
   4.731392115354538% used
G1 Young Generation:
Eden Space:
   regions  = 69
   capacity = 813694976 (776.0MB)
   used     = 72351744 (69.0MB)
   free     = 741343232 (707.0MB)
   8.891752577319588% used
Survivor Space:
   regions  = 19
   capacity = 19922944 (19.0MB)
   used     = 19922944 (19.0MB)
   free     = 0 (0.0MB)
   100.0% used
G1 Old Generation:
   regions  = 9
   capacity = 1313865728 (1253.0MB)
   used     = 9331184 (8.898910522460938MB)
   free     = 1304534544 (1244.101089477539MB)
   0.7102083417766112% used

18401 interned Strings occupying 1683016 bytes.
~~~

## 案例二：查看JVM中对应类型对象的数量、占用内存情况

~~~
jmap -histo 18230 | sort -n -r -k 2 | head -10  //统计实例最多的类 前十位有哪些
jmap -histo 18230 | sort -n -r -k 3 | head -10  //统计合计容量前十的类有哪些  
~~~

## **案例三：dump 堆快照**

命令:

~~~
jmap -dump:live,format=b,file=/home/myheapdump.hprof 18230
~~~

live   加上live代表只dump存活的对象 
fomat  格式
filie  导出的文件名

这里生成的 dump文件我们没办法直接对文件内存进行分析，这里需要用我们后面讲的可视化工具VisualVM来打开文件对里面的内容进行分析。
