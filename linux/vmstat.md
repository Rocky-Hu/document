~~~
vmstat - Report virtual memory statistics
~~~

vmstat也是一款功能比较齐全的性能监测工具，它可以统计CPU、内存及swap的使用情况等信息。

~~~
rocs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----
 r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st
 3  0      0 4117780      0  12572    0    0  3417 218493   72  114  1  0 99  0  0
~~~

https://man7.org/linux/man-pages/man8/vmstat.8.html

# FIELD DESCRIPTION FOR VM MODE

~~~
Procs
    r: The number of runnable processes (running or waiting for run time).
    b: The number of processes blocked waiting for I/O to complete.

Memory
    These are affected by the --unit option.
    swpd: the amount of swap memory used.
    free: the amount of idle memory.
    buff: the amount of memory used as buffers.
    cache: the amount of memory used as cache.
    inact: the amount of inactive memory.  (-a option)
    active: the amount of active memory.  (-a option)

Swap
    These are affected by the --unit option.
    si: Amount of memory swapped in from disk (/s).
    so: Amount of memory swapped to disk (/s).

IO
    bi: Blocks received from a block device (blocks/s).
    bo: Blocks sent to a block device (blocks/s).

System
    in: The number of interrupts per second, including the clock.
    cs: The number of context switches per second.

CPU
    These are percentages of total CPU time.
    us: Time spent running non-kernel code.  (user time, including nice time)
    sy: Time spent running kernel code.  (system time)
    id: Time spent idle.  Prior to Linux 2.5.41, this includes IO-wait time.
    wa: Time spent waiting for IO.  Prior to Linux 2.5.41, included in idle.
    st: Time stolen from a virtual machine.  Prior to Linux 2.6.11, unknown.
~~~

