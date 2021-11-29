# 功能

将诊断命令请求发送到正在运行的Java虚拟机(JVM)。

~~~
Sends diagnostic command requests to a running Java Virtual Machine (JVM).
~~~

在JDK 1.7之后，新增了一个命令行工具jcmd。它是一个多功能工具，可以用来导出堆，查看java进程，导出线程信息，执行GC等。

# 使用

## 列出当前运行的所有虚拟机

~~~
[root@localhost logs]# jcmd -l
12609 mi-scms-ipc-client.jar
12642 sun.tools.jcmd.JCmd -l
~~~

## 列出该虚拟机支持的所有命令

~~~
[root@localhost logs]# jcmd 12609 help
12609:
The following commands are available:
JFR.stop
JFR.start
JFR.dump
JFR.check
VM.native_memory
VM.check_commercial_features
VM.unlock_commercial_features
ManagementAgent.stop
ManagementAgent.start_local
ManagementAgent.start
VM.classloader_stats
GC.rotate_log
Thread.print
GC.class_stats
GC.class_histogram
GC.heap_dump
GC.finalizer_info
GC.heap_info
GC.run_finalization
GC.run
VM.uptime
VM.dynlibs
VM.flags
VM.system_properties
VM.command_line
VM.version
help

For more information about a specific command use 'help <command>'.
~~~

## 查看虚拟机启动时间VM.uptime

~~~
[root@localhost logs]# jcmd 12609 VM.uptime
12609:
141.703 s
~~~

## 打印线程栈信息Thread.print

~~~
[root@localhost logs]# jcmd 12609 Thread.print
~~~

## 查看系统中类统计信息GC.class_histogram

~~~
[root@localhost logs]# jcmd 12609 GC.class_histogram

 num     #instances         #bytes  class name
----------------------------------------------
   1:         46672        3778248  [C
   2:         46629        1119096  java.lang.String
   3:         32469        1039008  java.util.concurrent.ConcurrentHashMap$Node
   4:          8291         917368  java.lang.Class
   5:          4790         817528  [B
   6:          7802         686576  java.lang.reflect.Method
~~~

## 导出堆信息GC.heap_dump

使用如下命令可以导出当前堆栈信息，这个命令功能和jmap -dump功能一样。

~~~
[root@localhost logs]# jcmd 12609 GC.heap_dump /tmp/dump.bin
~~~

## 获取系统Properties内容VM.system_properties

~~~
[root@localhost logs]# jcmd 12609 VM.system_properties
12609:
#Wed Nov 24 21:54:01 EST 2021
java.runtime.name=Java(TM) SE Runtime Environment
java.protocol.handler.pkgs=org.springframework.boot.loader
sun.boot.library.path=/usr/local/jdk1.8.0_202/jre/lib/amd64
java.vm.version=25.202-b08
java.vm.vendor=Oracle Corporation
java.vendor.url=http\://java.oracle.com/
path.separator=\:
java.vm.name=Java HotSpot(TM) 64-Bit Server VM
file.encoding.pkg=sun.io
user.country=US
sun.java.launcher=SUN_STANDARD
sun.os.patch.level=unknown
java.vm.specification.name=Java Virtual Machine Specification
user.dir=/usr/project/8020
PID=12609
java.runtime.version=1.8.0_202-b08
java.awt.graphicsenv=sun.awt.X11GraphicsEnvironment
java.endorsed.dirs=/usr/local/jdk1.8.0_202/jre/lib/endorsed
os.arch=amd64
java.io.tmpdir=/tmp
line.separator=\n
java.vm.specification.vendor=Oracle Corporation
os.name=Linux
sun.jnu.encoding=UTF-8
java.library.path=/usr/java/packages/lib/amd64\:/usr/lib64\:/lib64\:/lib\:/usr/lib
spring.beaninfo.ignore=true
sun.nio.ch.bugLevel=
java.specification.name=Java Platform API Specification
java.class.version=52.0
sun.management.compiler=HotSpot 64-Bit Tiered Compilers
os.version=3.10.0-1160.45.1.el7.x86_64
user.home=/root
user.timezone=America/New_York
catalina.useNaming=false
java.awt.printerjob=sun.print.PSPrinterJob
file.encoding=UTF-8
java.specification.version=1.8
catalina.home=/tmp/tomcat.8181114725896941758.8020
java.class.path=mi-scms-ipc-client.jar
user.name=root
java.vm.specification.version=1.8
sun.java.command=mi-scms-ipc-client.jar
java.home=/usr/local/jdk1.8.0_202/jre
sun.arch.data.model=64
user.language=en
java.specification.vendor=Oracle Corporation
awt.toolkit=sun.awt.X11.XToolkit
java.vm.info=mixed mode
java.version=1.8.0_202
java.ext.dirs=/usr/local/jdk1.8.0_202/jre/lib/ext\:/usr/java/packages/lib/ext
sun.boot.class.path=/usr/local/jdk1.8.0_202/jre/lib/resources.jar\:/usr/local/jdk1.8.0_202/jre/lib/rt.jar\:/usr/local/jdk1.8.0_202/jre/lib/sunrsasign.jar\:/usr/local/jdk1.8.0_202/jre/lib/jsse.jar\:/usr/local/jdk1.8.0_202/jre/lib/jce.jar\:/usr/local/jdk1.8.0_202/jre/lib/charsets.jar\:/usr/local/jdk1.8.0_202/jre/lib/jfr.jar\:/usr/local/jdk1.8.0_202/jre/classes
java.awt.headless=true
java.vendor=Oracle Corporation
catalina.base=/tmp/tomcat.8181114725896941758.8020
file.separator=/
java.vendor.url.bug=http\://bugreport.sun.com/bugreport/
sun.io.unicode.encoding=UnicodeLittle
sun.cpu.endian=little
sun.cpu.isalist=
~~~

## 获取启动参数VM.flags

~~~
[root@localhost logs]# jcmd 12609 VM.flags
12609:
-XX:CICompilerCount=2 -XX:CompressedClassSpaceSize=260046848 -XX:ConcGCThreads=1 -XX:+DisableExplicitGC -XX:G1HeapRegionSize=1048576 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/work/logs/applogs/ -XX:InitialHeapSize=2147483648 -XX:MarkStackSize=4194304 -XX:MaxHeapSize=2147483648 -XX:MaxMetaspaceSize=268435456 -XX:MaxNewSize=1287651328 -XX:MetaspaceSize=134217728 -XX:MinHeapDeltaBytes=1048576 -XX:NativeMemoryTracking=summary -XX:+PrintAdaptiveSizePolicy -XX:+PrintGC -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps -XX:+UseG1GC
~~~

## 获取所有性能相关数据PerfCounter.print

~~~
[root@localhost logs]# jcmd 12609 PerfCounter.print
~~~

## 获取本地内存统计

**First, we should enable the native memory tracking using yet another JVM tuning flag: \*-XX:NativeMemoryTracking=off|sumary|detail.\***

~~~
[root@localhost 8020]# jcmd 12609 VM.native_memory scale=MB
12609:

Native Memory Tracking:

Total: reserved=3524MB, committed=2264MB
-                 Java Heap (reserved=2048MB, committed=2048MB)
                            (mmap: reserved=2048MB, committed=2048MB)

-                     Class (reserved=1061MB, committed=42MB)
                            (classes #7749)
                            (malloc=1MB #9692)
                            (mmap: reserved=1060MB, committed=41MB)

-                    Thread (reserved=33MB, committed=33MB)
                            (thread #34)
                            (stack: reserved=33MB, committed=33MB)

-                      Code (reserved=245MB, committed=9MB)
                            (malloc=1MB #3299)
                            (mmap: reserved=244MB, committed=7MB)

-                        GC (reserved=117MB, committed=117MB)
                            (malloc=9MB #14164)
                            (mmap: reserved=108MB, committed=108MB)

-                  Internal (reserved=3MB, committed=3MB)
                            (malloc=3MB #13024)

-                    Symbol (reserved=10MB, committed=10MB)
                            (malloc=9MB #83737)
                            (arena=2MB #1)

-    Native Memory Tracking (reserved=2MB, committed=2MB)
                            (tracking overhead=2MB)

-                   Unknown (reserved=4MB, committed=0MB)
                            (mmap: reserved=4MB, committed=0MB)
~~~

