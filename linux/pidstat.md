https://man7.org/linux/man-pages/man1/pidstat.1.html

# CPU使用率监控

~~~
[root@localhost /]# pidstat -p 1333 -u 1 3
Linux 3.10.0-1160.45.1.el7.x86_64 (localhost.localdomain)       11/23/2021      _x86_64_        (1 CPU)

04:24:53 AM   UID       PID    %usr %system  %guest    %CPU   CPU  Command
04:24:54 AM     0      1333    0.00    0.00    0.00    0.00     0  java
04:24:55 AM     0      1333    0.00    0.00    0.00    0.00     0  java
04:24:56 AM     0      1333    0.00    0.00    0.00    0.00     0  java
Average:        0      1333    0.00    0.00    0.00    0.00     -  java
~~~

线程：

~~~
[root@localhost /]# pidstat -p 1333 -u 1 3 -t
~~~

# I/O使用监控

~~~
[root@localhost /]# pidstat -p 1333 -d -t 1 3
~~~

# 内存监控

~~~
[root@localhost /]# pidstat -r -p 1333 1 5
Linux 3.10.0-1160.45.1.el7.x86_64 (localhost.localdomain)       11/23/2021      _x86_64_        (1 CPU)

04:31:07 AM   UID       PID  minflt/s  majflt/s     VSZ    RSS   %MEM  Command
04:31:08 AM     0      1333      0.00      0.00 2305340 149060  14.97  java
04:31:09 AM     0      1333      0.00      0.00 2305340 149060  14.97  java
04:31:10 AM     0      1333      0.00      0.00 2305340 149060  14.97  java
04:31:11 AM     0      1333      0.00      0.00 2305340 149060  14.97  java
04:31:12 AM     0      1333      0.00      0.00 2305340 149060  14.97  java
Average:        0      1333      0.00      0.00 2305340 149060  14.97  java
~~~

输出结果中各列含义如下：·minflt/s：该进程每秒错误（不需要从磁盘中调出内存页）的总数。

- majflt/s：该进程每秒错误（需要从磁盘中调出内存页）的总数。
- VSZ：该进程使用的虚拟内存大小，单位为KB。
- RSS：该进程占用的物理内存大小，单位为KB。
- %MEM：占用内存比率

