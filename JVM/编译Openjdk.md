http://hg.openjdk.java.net/

http://hg.openjdk.java.net/jdk8/jdk8/raw-file/tip/README-builds.html

# **安装 "Bootstrap JDK"**

~~~
[root@localhost ~]# vi /etc/profile

## jdk config
export JAVA_HOME=/usr/local/java/jdk1.8.0_251
export CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/
export PATH=$PATH:$JAVA_HOME/bin
~~~

# 安装Mercurial

~~~
[root@localhost ~]# yum install hg
~~~

# 下载OpenJDK

~~~
[root@localhost jdk]# hg clone http://hg.openjdk.java.net/jdk8u/jdk8u/
--- 目录 ---
[root@localhost jdk8u]# ls
ASSEMBLY_EXCEPTION  configure      LICENSE  Makefile  README-builds.html  THIRD_PARTY_README
common              get_source.sh  make     README    test
~~~

成功后，执行：

~~~
sh get_source.sh
~~~

# 安装依赖包

~~~
yum install alsa-lib-devel cups-devel libX* gcc gcc-c++ freetype-devel libstdc++-static ant make
~~~

# **编译配置**

~~~
chmod +x configure

# 执行configure脚本，看看缺少什么依赖项，根据错误提示安装即可，然后重复执行直到提示成功
./configure --with-debug-level=slowdebug --enable-debug-symbols --disable-zip-debug-info

# 参数说明
# --with-debug-level=slowdebug 设置编译级别为slowdebug，将会输出较多的调试信息
# --enable-debug-symbols 启用调试符号，将会生成调试信息文件
# --disable-zip-debug-info 禁用调试信息压缩，否则，调试信息默认会被压缩成"libjvm.diz"文件，调试时只能看到汇编代码，不能跟进源码
~~~

# **开始编译**

~~~
#清理 如果make失败了，需要先清理一下
make clean
#开始编译
make
~~~

# 测试

~~~
[root@localhost bin]# /opt/jdk/jdk8u/build/linux-x86_64-normal-server-slowdebug/jdk/bin/java -version

openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b10)
OpenJDK 64-Bit Server VM (build 25.292-b10, mixed mode)

# 确保"libjvm.debuginfo"文件存在，否则调试时将不能跟进源码
ls /opt/jdk/jdk8u/build/linux-x86_64-normal-server-slowdebug/jdk/lib/amd64/server
~~~

