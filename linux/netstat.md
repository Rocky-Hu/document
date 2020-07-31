# 一、netstat命令说明

Netstat 是一款命令行工具，可用于列出系统上所有的网络套接字连接情况，包括 tcp, udp 以及 unix 套接字，另外它还能列出处于监听状态（即等待接入请求）的套接字。

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



