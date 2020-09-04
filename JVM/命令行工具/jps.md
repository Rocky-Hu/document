# 全称

JVM Process Status（虚拟机进程状况工具）

# 功能

列出正在运行的虚拟机进程，并显示虚拟机执行祝列名称以及这些进程的本地虚拟机唯一ID（Local Virtual Machine Identifier, LVMID）。

# 参数

## -q

只输出LVMID，省略主类的名称。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jps
23686 kapp.jar
27500 Jps
[root@iZ2vcihouav0976j4p3w68Z ~]# jps -q
23686
27517
[root@iZ2vcihouav0976j4p3w68Z ~]# 
~~~

## -m

输出虚拟机进程启动时传递给主类main()函数的参数。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jps -m
23686 kapp.jar
27614 Jps -m
~~~

## -l

输出主类的全名，如果进程执行的是Jar包，输出Jar路径。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jps -l
23686 /project/kapp/kapp.jar
27670 sun.tools.jps.Jps
~~~

## -v

输出虚拟机进程启动时的JVM参数。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jps -v
27713 Jps -Denv.class.path=$:CLASSPATH:/usr/local/jdk1.8.0_251/lib/ -Dapplication.home=/usr/local/jdk1.8.0_251 -Xms8m
23686 kapp.jar -Dsun.misc.URLClassPath.disableJarChecking=true
~~~

































































