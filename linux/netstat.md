https://en.wikipedia.org/wiki/Netstat#:~:text=Foreign%20Address%20%E2%80%93%20The%20IP%20address,the%20%2Dn%20parameter%20is%20specified

# 一、netstat命令说明

Netstat 是一款命令行工具，可用于列出系统上所有的网络套接字连接情况，包括 tcp, udp 以及 unix 套接字，另外它还能列出处于监听状态（即等待接入请求）的套接字。

~~~
NETSTAT [-a] [-b] [-e] [-f] [-n] [-o] [-p proto] [-r] [-s] [-x] [-t] [interval]

  -a            显示所有连接和侦听端口。
  -b            显示在创建每个连接或侦听端口时涉及的
                可执行程序。在某些情况下，已知可执行程序承载
                多个独立的组件，这些情况下，
                显示创建连接或侦听端口时
                涉及的组件序列。在此情况下，可执行程序的
                名称位于底部 [] 中，它调用的组件位于顶部，
                直至达到 TCP/IP。注意，此选项
                可能很耗时，并且在你没有足够
                权限时可能失败。
  -e            显示以太网统计信息。此选项可以与 -s 选项
                结合使用。
  -f            显示外部地址的完全限定
                域名(FQDN)。
  -n            以数字形式显示地址和端口号。
  -o            显示拥有的与每个连接关联的进程 ID。
  -p proto      显示 proto 指定的协议的连接；proto
                可以是下列任何一个: TCP、UDP、TCPv6 或 UDPv6。如果与 -s
                选项一起用来显示每个协议的统计信息，proto 可以是下列任何一个:
                IP、IPv6、ICMP、ICMPv6、TCP、TCPv6、UDP 或 UDPv6。
  -q            显示所有连接、侦听端口和绑定的
                非侦听 TCP 端口。绑定的非侦听端口
                 不一定与活动连接相关联。
  -r            显示路由表。
  -s            显示每个协议的统计信息。默认情况下，
                显示 IP、IPv6、ICMP、ICMPv6、TCP、TCPv6、UDP 和 UDPv6 的统计信息;
                -p 选项可用于指定默认的子网。
  -t            显示当前连接卸载状态。
  -x            显示 NetworkDirect 连接、侦听器和共享
                终结点。
  -y            显示所有连接的 TCP 连接模板。
                无法与其他选项结合使用。
  interval      重新显示选定的统计信息，各个显示间暂停的
                间隔秒数。按 CTRL+C 停止重新显示
                统计信息。如果省略，则 netstat 将打印当前的
                配置信息一次。
~~~

# 二、实用命令

## 2.1. 查看当前所有tcp端口

~~~verilog
[root@localhost redis]# netstat -ntlp
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name    
tcp        0      0 0.0.0.0:6379            0.0.0.0:*               LISTEN      5111/redis-server * 
tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN      1023/sshd           
tcp6       0      0 :::3306                 :::*                    LISTEN      1239/mysqld         
tcp6       0      0 :::6379                 :::*                    LISTEN      5111/redis-server * 
tcp6       0      0 :::22                   :::*                    LISTEN      1023/sshd 
~~~

**Netstat** provides statistics for the following:

- Proto – The name of the protocol (TCP or UDP).
- Local Address – The IP address of the local computer and the port number being used. The name of the local computer that corresponds to the IP address and the name of the port is shown unless the -n parameter is specified. An asterisk (*) is shown for the host if the server is listening on all interfaces. If the port is not yet established, the port number is shown as an asterisk.
- Foreign Address – The IP address and port number of the remote computer to which the socket is connected. The names that corresponds to the IP address and the port are shown unless the -n parameter is specified. If the port is not yet established, the port number is shown as an asterisk (*).
- State – Indicates the state of a TCP connection. The possible states are as follows: CLOSE_WAIT, CLOSED, ESTABLISHED, FIN_WAIT_1, FIN_WAIT_2, LAST_ACK, LISTEN, SYN_RECEIVED, SYN_SEND, and TIME_WAIT. For more information about the states of a TCP connection, see RFC 793.

## 2.2. 查看TCP连接数

~~~verilog
netstat -nat | grep -i "80" | wc -l
~~~

## 2.3. 根据进程号查看监听的端口

~~~verilog
[root@localhost bin]# netstat -nap|grep 42609
tcp6       0      0 :::8848                 :::*                    LISTEN      42609/java                                                                                               
udp6       0      0 :::51107                :::*                                42609/java                                                                                               
unix  2      [ ]         STREAM     CONNECTED     214923   42609/java
unix  2      [ ]         STREAM     CONNECTED     214739   42609/java
~~~

## 2.4. 查看端口占用情况

~~~
[root@localhost logs]# netstat -tunlp|grep 8080
tcp        0      0 127.0.0.1:8080          0.0.0.0:*               LISTEN      1743/puma 4.3.5.git
~~~

## 2.5. 列出time_wait的tcp连接

~~~
netstat -ant|grep -i time_wait
~~~

统计处于time_wait状态的连接数

~~~
netstat -ant|grep -i time_wait |wc -l
~~~



 

