https://man7.org/linux/man-pages/man1/iostat.1.html

~~~
[root@localhost /]# iostat
Linux 3.10.0-1160.45.1.el7.x86_64 (localhost.localdomain)       11/23/2021      _x86_64_        (1 CPU)

avg-cpu:  %user   %nice %system %iowait  %steal   %idle
           0.06    0.00    0.13    0.07    0.00   99.74

Device:            tps    kB_read/s    kB_wrtn/s    kB_read    kB_wrtn
sda               0.47        13.94         5.66     362591     147255
dm-0              0.44        13.47         5.58     350451     145206
dm-1              0.00         0.08         0.00       2204          0
~~~

IOSTAT 命令不可用，首先确认sysstat包是否安装，sysstat包中包括iostat,mpstat,sar,sa

[root@testhost ~]#yum install -y sysstat

安装完成后，再执行iostat命令。



解释：

avg-cpu段:
%user: 在用户级别运行所使用的CPU的百分比.
%nice: nice操作所使用的CPU的百分比.
%sys: 在系统级别(kernel)运行所使用CPU的百分比.
%iowait: CPU等待硬件I/O时,所占用CPU百分比.
%idle: CPU空闲时间的百分比.


Device段:
tps: 每秒钟发送到的I/O请求数.
Blk_read /s: 每秒读取的block数.
Blk_wrtn/s: 每秒写入的block数.
Blk_read:  读入的block总数.
Blk_wrtn:  写入的block总数