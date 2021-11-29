~~~
yum install libunwind-devel
https://yum-info.contradodigital.com/view-package/epel/libunwind-devel/
~~~

~~~
The simplest way to compile this package is:

  1. `cd' to the directory containing the package's source code and type
     `./configure' to configure the package for your system.  If you're
     using `csh' on an old version of System V, you might need to type
     `sh ./configure' instead to prevent `csh' from trying to execute
     `configure' itself.

     Running `configure' takes awhile.  While running, it prints some
     messages telling which features it is checking for.
     
     ./configure  --prefix=/data/google-perftools/gperftools-2.5  #自定义安装目录

  2. Type `make' to compile the package.

  3. Optionally, type `make check' to run any self-tests that come with
     the package.

  4. Type `make install' to install the programs and any data files and
     documentation.

  5. You can remove the program binaries and object files from the
     source code directory by typing `make clean'.  To also remove the
     files that `configure' created (so you can compile the package for
     a different kind of computer), type `make distclean'.  There is
     also a `make maintainer-clean' target, but that is intended mainly
     for the package's developers.  If you use it, you may have to get
     all sorts of other programs in order to regenerate files that came
     with the distribution.
~~~

# 安装**libunwind**

~~~
不建议版本>0.99据说有问题，这个需要FQ
# wgethttp://download.savannah.gnu.org/releases/libunwind/libunwind-0.99.tar.gz
# tar -xzvf libunwind-0.99.tar.gz
# cd libunwind-0.99
# ./configure  --prefix=/data0/java/deploy/google-perftools/local/libunwind
# make && make install
~~~

## 安装**gperftools**

~~~
# wget https://github.com/gperftools/gperftools/releases/download/gperftools-2.5/gperftools-2.5.tar.gz
# tar -xzvf gperftools-2.5.tar.gz
# cd gperftools-2.5
# ./configure --prefix=/data0/java/deploy/google-perftools/local/gperftools-2.5/
# make && make install
~~~

# **使配置生效**

~~~
# vim /etc/ld.so.conf.d/usr_local_lib.conf

新增以下内容按i
/data0/java/deploy/google-perftools/local/libunwind/lib
按esc再:wq! #保存退出
# /sbin/ldconfig  #执行此命令，使libunwind生效。 需要sudo权限
~~~

# **加入环境变量**

~~~
export LD_PRELOAD=/data/google-perftools/gperftools-2.9.1/lib/libtcmalloc.so
export HEAPPROFILE=/data/google-perftools/heap/hprof
~~~

# 运行Java程序

~~~
export LD_PRELOAD=/data/google-perftools/gperftools-2.9.1/lib/libtcmalloc.so
export HEAPPROFILE=/data/google-perftools/heap/hprof
java -Xms2048M -Xmx2048M -XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=256M -XX:NativeMemoryTracking=detail -verbose:gc -Xloggc:/home/work/logs/applogs/gc.log.%p -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintAdaptiveSizePolicy -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/work/logs/applogs/ -XX:+UseG1GC -jar mi-scms-ipc-client.jar 
~~~

# 分析函数调用

~~~
[root@localhost bin]# pprof --text  /usr/local/jdk1.8.0_202/bin/java /data/google-perftools/heap/*.heap
~~~



