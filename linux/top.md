https://man7.org/linux/man-pages/man1/top.1.html

https://www.jianshu.com/p/3f19d4fc4538

# 功能

Linux top命令用于实时显示 process 的动态。

~~~
display Linux processes
~~~

# 语法

~~~
top [-] [d delay] [q] [c] [S] [s] [i] [n] [b]
~~~

参数说明：

- d : 改变显示的更新速度，或是在交谈式指令列( interactive command)按 s
- q : 没有任何延迟的显示速度，如果使用者是有 superuser 的权限，则 top 将会以最高的优先序执行
- c : 切换显示模式，共有两种模式，一是只显示执行档的名称，另一种是显示完整的路径与名称S : 累积模式，会将己完成或消失的子行程 ( dead child process ) 的 CPU time 累积起来
- s : 安全模式，将交谈式指令取消, 避免潜在的危机
- i : 不显示任何闲置 (idle) 或无用 (zombie) 的行程
- n : 更新的次数，完成后将会退出 top
- b : 批次档模式，搭配 "n" 参数一起使用，可以用来将 top 的结果输出到档案内

# 使用

通过在 top 视图下按键盘的1，查看cpu的核数为12核。

top 命令显示的是程序占用的cpu的总数，12核cpu最高占用率可达1200%，top视图显示的是把所有使用率加起来的总数。

查看 pid 9907 进程的资源占用情况（-d 指定刷新间隔单位s）

~~~
top -p 9907 -d 5
~~~

查看 elasticsearch 进程所有活跃的线程消耗情况

~~~
top -p 9907 -H -d 5
~~~

# 说明

~~~
top - 09:41:12 up 3 days, 12:51,  0 users,  load average: 0.00, 0.00, 0.00
Tasks:   3 total,   1 running,   2 sleeping,   0 stopped,   0 zombie
%Cpu(s):  0.0 us,  0.0 sy,  0.0 ni,100.0 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
KiB Mem :  8388608 total,  4122412 free,  4253216 used,    12980 buff/cache
KiB Swap:        0 total,        0 free,        0 used.  4135392 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND                                                                                                                                         
    1 root      20   0 9134124 4.045g  17344 S   0.3 50.6 142:58.05 java                                                                                                                                            
30276 root      20   0   15208   3360   2960 S   0.0  0.0   0:00.03 bash                                                                                                                                            
30297 root      20   0   57428   4128   3556 R   0.0  0.0   0:00.04 top   
~~~

统计信息区前五行是系统整体的统计信息。第一行是任务队列信息，同 uptime 命令的执行结果。其内容如下：

~~~
01:06:48    当前时间
up 1:22    系统运行时间，格式为时:分
1 user    当前登录用户数
load average: 0.06, 0.60, 0.48    系统负载，即任务队列的平均长度。三个数值分别为 1分钟、5分钟、15分钟前到现在的平均值。
~~~

第二、三行为进程和CPU的信息。当有多个CPU时，这些内容可能会超过两行。内容如下：

```
total 进程总数
running 正在运行的进程数
sleeping 睡眠的进程数
stopped 停止的进程数
zombie 僵尸进程数
Cpu(s): 
0.3% us 用户空间占用CPU百分比
1.0% sy 内核空间占用CPU百分比
0.0% ni 用户进程空间内改变过优先级的进程占用CPU百分比
98.7% id 空闲CPU百分比
0.0% wa 等待输入输出的CPU时间百分比
0.0%hi：硬件CPU中断占用百分比
0.0%si：软中断占用百分比
0.0%st：虚拟机占用百分比
```

最后两行为内存信息。内容如下：

~~~
Mem:
191272k total    物理内存总量
173656k used    使用的物理内存总量
17616k free    空闲内存总量
22052k buffers    用作内核缓存的内存量
Swap: 
192772k total    交换区总量
0k used    使用的交换区总量
192772k free    空闲交换区总量
123988k cached    缓冲的交换区总量,内存中的内容被换出到交换区，而后又被换入到内存，但使用过的交换区尚未被覆盖，该数值即为这些内容已存在于内存中的交换区的大小,相应的内存再次被换出时可不必再对交换区写入。
~~~

进程信息区统计信息区域的下方显示了各个进程的详细信息。首先来认识一下各列的含义。

Here is a list that explains what each column means.

- **PID**: A process’s process ID number.
- **USER**: The process’s owner.
- **PR**: The process’s priority. The lower the number, the higher the priority.
- **NI**: The nice value of the process, which affects its priority.
- **VIRT**: How much virtual memory the process is using.
- **RES**: How much physical RAM the process is using, measured in kilobytes.
- **SHR**: How much shared memory the process is using.
- **S**: The current status of the process (zombied, sleeping, running, uninterruptedly sleeping, or traced).
- **%CPU**: The percentage of the processor time used by the process.
- **%MEM**: The percentage of physical RAM used by the process.
- **TIME+**: How much processor time the process has used.
- **COMMAND**: The name of the command that started the process.

~~~
序号  列名    含义
a    PID     进程id
b    PPID    父进程id
c    RUSER   Real user name
d    UID     进程所有者的用户id
e    USER    进程所有者的用户名
f    GROUP   进程所有者的组名
g    TTY     启动进程的终端名。不是从终端启动的进程则显示为 ?
h    PR      优先级
i    NI      nice值。负值表示高优先级，正值表示低优先级
j    P       最后使用的CPU，仅在多CPU环境下有意义
k    %CPU    上次更新到现在的CPU时间占用百分比
l    TIME    进程使用的CPU时间总计，单位秒
m    TIME+   进程使用的CPU时间总计，单位1/100秒
n    %MEM    进程使用的物理内存百分比
o    VIRT    进程使用的虚拟内存总量，单位kb。VIRT=SWAP+RES
p    SWAP    进程使用的虚拟内存中，被换出的大小，单位kb。
q    RES     进程使用的、未被换出的物理内存大小，单位kb。RES=CODE+DATA
r    CODE    可执行代码占用的物理内存大小，单位kb
s    DATA    可执行代码以外的部分(数据段+栈)占用的物理内存大小，单位kb
t    SHR     共享内存大小，单位kb
u    nFLT    页面错误次数
v    nDRT    最后一次写入到现在，被修改过的页面数。
w    S       进程状态(D=不可中断的睡眠状态,R=运行,S=睡眠,T=跟踪/停止,Z=僵尸进程)
x    COMMAND 命令名/命令行
y    WCHAN   若该进程在睡眠，则显示睡眠中的系统函数名
z    Flags   任务标志，参考 sched.h
~~~

默认情况下仅显示比较重要的 PID、USER、PR、NI、VIRT、RES、SHR、S、%CPU、%MEM、TIME+、COMMAND 列。可以通过下面的快捷键来更改显示内容。 

更改显示内容通过 **f** 键可以选择显示的内容。按 f 键之后会显示列的列表，按 a-z 即可显示或隐藏对应的列，最后按回车键确定。
按 **o** 键可以改变列的显示顺序。按小写的 a-z 可以将相应的列向右移动，而大写的 A-Z 可以将相应的列向左移动。最后按回车键确定。
按大写的 **F** 或 **O** 键，然后按 a-z 可以将进程按照相应的列进行排序。而大写的 **R** 键可以将当前的排序倒转。

## The difference between VIRT, RES and SHR:

- **VIRT** stands for the virtual size of a process, which is the sum of memory it is actually using, memory it has mapped into itself (for instance the video card's RAM for the X server), files on disk that have been mapped into it (most notably shared libraries), and memory shared with other processes. **VIRT** represents how much memory the program is able to access at the present moment.
- **RES** stands for the resident size, which is an accurate representation of how much actual physical memory a process is consuming. (This also corresponds directly to the **%MEM** column)
- **SHR** indicates how much of the **VIRT** size is actually sharable memory or libraries. In the case of libraries, it does not necessarily mean that the entire library is resident. For example, if a program only uses a few functions in a library, the whole library is mapped and will be counted in **VIRT** and **SHR**, but only the parts of the library file containing the functions being used will actually be loaded in and be counted under **RES**.

