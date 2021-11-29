# 全称

Configuration Info for Java（Java配置信息工具）

# 功能

实时地查看和调整虚拟机各项参数。

~~~
[root@iZ2vcihouav0976j4p3w68Z ~]# jinfo
Usage:
    jinfo [option] <pid>
        (to connect to running process)
    jinfo [option] <executable <core>
        (to connect to a core file)
    jinfo [option] [server_id@]<remote server IP or hostname>
        (to connect to remote debug server)

where <option> is one of:
    -flag <name>         to print the value of the named VM flag
    -flag [+|-]<name>    to enable or disable the named VM flag
    -flag <name>=<value> to set the named VM flag to the given value
    -flags               to print VM flags
    -sysprops            to print Java system properties
    <no option>          to print both of the above
    -h | -help           to print this help message

~~~

# 使用

## 查看Eden区和Survivor比例

~~~
C:\Users\87490\Desktop> jinfo -flag SurvivorRatio 17924
-XX:SurvivorRatio=8
~~~

默认的，Edem : from : to = 8 :1:1。即： Eden = 8/10 的新生代空间大小，from = to = 1/10 的新生代空间大小。

## 查看新生代和老年代比例

~~~
C:\Users\87490\Desktop> jinfo -flag NewRatio 17924
-XX:NewRatio=2
~~~

**默认的，新生代 ( Young ) 与老年代 ( Old ) 的比例的值为 1:2 ( 该值可以通过参数 –XX:NewRatio 来指定 )**，即：新生代 ( Young ) = 1/3 的堆空间大小。老年代 ( Old ) = 2/3 的堆空间大小。

## 查看ThreadStackSize

~~~
[root@localhost ~]# jinfo -flag ThreadStackSize 8310
-XX:ThreadStackSize=1024
~~~

