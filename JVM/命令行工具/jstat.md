https://docs.oracle.com/javase/7/docs/technotes/tools/share/jstat.html

https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jstat.html

基于Java8。

# 全称

JVM Statistics Monitoring Tool（虚拟机统计信息监视工具）

# 功能

jstat用于监视虚拟机各种运行状态信息的命令行工具。它可以显示本地或者远程虚拟机进程中的类装载、内存、垃圾收集、JIT编译等运行数据。

# 语法

~~~
jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]
~~~

参数interval和count代表查询间隔和次数，如果省略这两个参数，说明只查询一次。假设需要每250毫秒查询一次进程2764垃圾收集状况，一共查询20次，那命令应当是：

~~~
jstat -gc 2764 250 20
~~~

# 选项

选项option代表着用户希望查询的虚拟机信息，主要分为3类：类装载、垃圾收集、运行期编译状况。

## -class

监视类装载、卸载数量、总空间以及类装载所耗费的时间。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -class 23686 250 5
Loaded  Bytes  Unloaded  Bytes     Time   
 10096 18543.3        0     0.0       6.17
 10096 18543.3        0     0.0       6.17
 10096 18543.3        0     0.0       6.17
 10096 18543.3        0     0.0       6.17
 10096 18543.3        0     0.0       6.17
~~~

列说明：

- Loaded：加载class的数量
- Bytes：加载的类占用空间大小
- Unloaded：卸载数量
- Bytes：卸载的类占用空间大小
- Time：时间

## -gc

监视Java堆状况，包括Eden区、两个survivor区、老年代、永久代等的容量、已用空间、GC时间合计等信息。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -gc 23686 250 5
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
1600.0 1600.0  0.0   1600.0 12864.0   4000.6   31884.0    26019.0   58240.0 55390.1 7296.0 6747.9    191    0.466   3      0.154    0.620
1600.0 1600.0  0.0   1600.0 12864.0   4000.6   31884.0    26019.0   58240.0 55390.1 7296.0 6747.9    191    0.466   3      0.154    0.620
1600.0 1600.0  0.0   1600.0 12864.0   4000.6   31884.0    26019.0   58240.0 55390.1 7296.0 6747.9    191    0.466   3      0.154    0.620
1600.0 1600.0  0.0   1600.0 12864.0   4000.6   31884.0    26019.0   58240.0 55390.1 7296.0 6747.9    191    0.466   3      0.154    0.620
1600.0 1600.0  0.0   1600.0 12864.0   4000.6   31884.0    26019.0   58240.0 55390.1 7296.0 6747.9    191    0.466   3      0.154    0.620
~~~

列说明：

- S0C：survivor0区的容量(KB)
- S1C：survivor1区的容量(KB)
- S0U：survivor0区的使用容量(KB)
- S1U：survivor1区的使用容量(KB)
- EC：当前Eden区的容量(KB)
- EU：Eden区的使用容量(KB)
- OC：当前老年代空间容量(KB)
- OU：老年代使用容量(KB)
- MC：元空间容量(KB)
- MU：元空间使用容量(KB)
- CCSC：压缩类空间容量(KB)
- CCSU：压缩类空间使用容量(KB)
- YGC：年轻代垃圾回收次数
- YGCT：年轻代垃圾回收消耗时间
- FGC：老年代垃圾回收次数
- FGCT：老年代垃圾回收消耗时间
- GCT：垃圾回收消耗总时间

## -gccapacity

监视内容与-gc基本相同，但输出主要关注Java堆各个区域使用的最大、最小空间。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -gccapacity 23686 250 5
 NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC 
  5440.0  79872.0  16064.0 1600.0 1600.0  12864.0    10944.0   159744.0    31884.0    31884.0      0.0 1099776.0  58240.0      0.0 1048576.0   7296.0    191     3
  5440.0  79872.0  16064.0 1600.0 1600.0  12864.0    10944.0   159744.0    31884.0    31884.0      0.0 1099776.0  58240.0      0.0 1048576.0   7296.0    191     3
  5440.0  79872.0  16064.0 1600.0 1600.0  12864.0    10944.0   159744.0    31884.0    31884.0      0.0 1099776.0  58240.0      0.0 1048576.0   7296.0    191     3
  5440.0  79872.0  16064.0 1600.0 1600.0  12864.0    10944.0   159744.0    31884.0    31884.0      0.0 1099776.0  58240.0      0.0 1048576.0   7296.0    191     3
  5440.0  79872.0  16064.0 1600.0 1600.0  12864.0    10944.0   159744.0    31884.0    31884.0      0.0 1099776.0  58240.0      0.0 1048576.0   7296.0    191     3
~~~

列说明（与-gc相同的列不做说明）：

- NGCMN：新生代最小容量
- NGCMX：新生代最大容量
- NGC：当前新生代容量
- OGCMN：老年代最小容量
- OGCMX：老年代最大容量
- OGC：当前老年代容量（Current old generation capacity）
- OC：Current old space capacity
- MCMN：元空间最小容量
- MCMX：元空间最大容量
- MC：当前元空间容量
- CCSMN：压缩类空间最小容量
- CCSMX：压缩类空间最大容量
- CCSC：压缩类空间当前容量

> https://stackoverflow.com/questions/11253285/jstat-difference-between-ogc-oc-pgc-pc
>
> NGC = EC + S0C + S1C
>
> OGC = sum(all OC) 
>
> However, Hotspot old gen has only 1 space ( young gen has 3: eden , s0 and s1 ), jstat shows the same value for them.

## -gcutil

监视内容与-gc基本相同，但输出主要关注已使用空间占总空间的百分比。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -gcutil 23686 250 5
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT   
 67.98   0.00   0.38  85.47  94.89  92.67    192    0.471     3    0.154    0.625
 67.98   0.00   0.38  85.47  94.89  92.67    192    0.471     3    0.154    0.625
 67.98   0.00   0.38  85.47  94.89  92.67    192    0.471     3    0.154    0.625
 67.98   0.00   0.38  85.47  94.89  92.67    192    0.471     3    0.154    0.625
 67.98   0.00   0.38  85.47  94.89  92.67    192    0.471     3    0.154    0.625
~~~

列说明：

- S0：Survivor1区当前使用比例
- S1：Survivor2区当前使用比例
- E：Eden区使用比例
- O：老年代使用比例
- M：元空间使用比例
- CCS：压缩类空间使用比例

## -gccause

与-gcutil功能一样，但是会额外输出导致上一次GC产生的原因。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -gccause 23686 250 5
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT    LGCC                 GCC                 
 67.98   0.00   1.23  85.47  94.89  92.67    192    0.471     3    0.154    0.625 Allocation Failure   No GC               
 67.98   0.00   1.23  85.47  94.89  92.67    192    0.471     3    0.154    0.625 Allocation Failure   No GC               
 67.98   0.00   1.23  85.47  94.89  92.67    192    0.471     3    0.154    0.625 Allocation Failure   No GC               
 67.98   0.00   1.23  85.47  94.89  92.67    192    0.471     3    0.154    0.625 Allocation Failure   No GC               
 67.98   0.00   1.23  85.47  94.89  92.67    192    0.471     3    0.154    0.625 Allocation Failure   No G
~~~

列说明：

- LGCC：最后一次GC原因。
- GCC：当前GC原因（No GC为当前没有执行GC）

## -gcnew

监视新生代GC状况。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -gcnew 23686 250 5
 S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT  
1600.0 1600.0  625.0    0.0 15  15  800.0  12864.0   2224.4    194    0.480
1600.0 1600.0  625.0    0.0 15  15  800.0  12864.0   2224.4    194    0.480
1600.0 1600.0  625.0    0.0 15  15  800.0  12864.0   2224.4    194    0.480
1600.0 1600.0  625.0    0.0 15  15  800.0  12864.0   2224.4    194    0.480
1600.0 1600.0  625.0    0.0 15  15  800.0  12864.0   2224.4    194    0.480
~~~

列说明：

- TT：Tenuring threshold.
- MTT：Maximum tenuring threshold.
- DSS：Desired survivor size (kB).

## -gcnewcapacity

监视的内容与-gcnew基本相同，输出主要关注使用到的最大、最小空间。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -gcnewcapacity 23686 250 5
  NGCMN      NGCMX       NGC      S0CMX     S0C     S1CMX     S1C       ECMX        EC      YGC   FGC 
    5440.0    79872.0    16064.0   7936.0   1600.0   7936.0   1600.0    64000.0    12864.0   194     3
    5440.0    79872.0    16064.0   7936.0   1600.0   7936.0   1600.0    64000.0    12864.0   194     3
    5440.0    79872.0    16064.0   7936.0   1600.0   7936.0   1600.0    64000.0    12864.0   194     3
    5440.0    79872.0    16064.0   7936.0   1600.0   7936.0   1600.0    64000.0    12864.0   194     3
    5440.0    79872.0    16064.0   7936.0   1600.0   7936.0   1600.0    64000.0    12864.0   194     3
~~~

## -gcold

监视老年代GC状况。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -gcold 23686 250 5
   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT   
 58496.0  55689.1   7296.0   6765.7     31884.0     28437.5    194     3    0.154    0.633
 58496.0  55689.1   7296.0   6765.7     31884.0     28437.5    194     3    0.154    0.633
 58496.0  55689.1   7296.0   6765.7     31884.0     28437.5    194     3    0.154    0.633
 58496.0  55689.1   7296.0   6765.7     31884.0     28437.5    194     3    0.154    0.633
 58496.0  55689.1   7296.0   6765.7     31884.0     28437.5    194     3    0.154    0.633
~~~

## -gcoldcapacity

监视内容与-gcold基本相同，输出主要关注使用到的最大、最小空间。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -gcoldcapacity 23686 250 5
   OGCMN       OGCMX        OGC         OC       YGC   FGC    FGCT     GCT   
    10944.0    159744.0     31884.0     31884.0   194     3    0.154    0.633
    10944.0    159744.0     31884.0     31884.0   194     3    0.154    0.633
    10944.0    159744.0     31884.0     31884.0   194     3    0.154    0.633
    10944.0    159744.0     31884.0     31884.0   194     3    0.154    0.633
    10944.0    159744.0     31884.0     31884.0   194     3    0.154    0.633
~~~

## -gcmetacapacity

元空间容量监控。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -gcmetacapacity 23686 250 5
   MCMN       MCMX        MC       CCSMN      CCSMX       CCSC     YGC   FGC    FGCT     GCT   
       0.0  1099776.0    58496.0        0.0  1048576.0     7296.0   194     3    0.154    0.633
       0.0  1099776.0    58496.0        0.0  1048576.0     7296.0   194     3    0.154    0.633
       0.0  1099776.0    58496.0        0.0  1048576.0     7296.0   194     3    0.154    0.633
       0.0  1099776.0    58496.0        0.0  1048576.0     7296.0   194     3    0.154    0.633
       0.0  1099776.0    58496.0        0.0  1048576.0     7296.0   194     3    0.154    0.633
~~~

## -compiler

输出JIT编译期编译过的方法、耗时等信息。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -compiler 23686 250 5
Compiled Failed Invalid   Time   FailedType FailedMethod
    8113      0       0    17.85          0             
    8113      0       0    17.85          0             
    8113      0       0    17.85          0             
    8113      0       0    17.85          0             
    8113      0       0    17.85          0  
~~~

列说明：

- Compiled：执行的编译任务数量。
- Failed：编译任务失败的数目。
- Invalid：无效的编译任务数量。
- Time：执行编译任务所花费的时间。
- FailedType：上次失败编译的编译类型。
- FailedMethod：上次编译失败的类名和方法。

## -printcompilation

输出已经被JIT编译的方法。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jstat -printcompilation 23686 250 5
Compiled  Size  Type Method
    8113     17    1 java/util/concurrent/ConcurrentHashMap mappingCount
    8113     17    1 java/util/concurrent/ConcurrentHashMap mappingCount
    8113     17    1 java/util/concurrent/ConcurrentHashMap mappingCount
    8113     17    1 java/util/concurrent/ConcurrentHashMap mappingCount
    8113     17    1 java/util/concurrent/ConcurrentHashMap mappingCount
~~~

列说明：

- Compiled：最近编译的方法执行的编译任务数。
- Size：最近编译的方法的字节代码的字节数。
- Type：最新编译方法的编译类型。
- Method：类名和方法名标识最近编译的方法。类名使用斜杠(/)而不是点(.)作为名称空格分隔符。方法名是指定类中的方法。这两个字段的格式与HotSpot -XX:+Printcompile选项一致。





















































































