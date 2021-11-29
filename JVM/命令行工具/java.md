# 1. 查看ThreadStackSize

~~~sh
[root@localhost ~]# java -XX:+PrintFlagsFinal -version | grep ThreadStackSize
     intx CompilerThreadStackSize                   = 0                                   {pd product}
     intx ThreadStackSize                           = 1024                                {pd product}
     intx VMThreadStackSize                         = 1024                                {pd product}
java version "1.8.0_202"
Java(TM) SE Runtime Environment (build 1.8.0_202-b08)
Java HotSpot(TM) 64-Bit Server VM (build 25.202-b08, mixed mode)
~~~

# 2. 打印所有的JVM参数

~~~sh
[root@localhost applogs]# java -XX:+PrintFlagsFinal -version
~~~

