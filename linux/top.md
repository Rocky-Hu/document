https://www.runoob.com/linux/linux-comm-top.html

# 功能

Linux top命令用于实时显示 process 的动态。

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

