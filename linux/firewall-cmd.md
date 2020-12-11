查看已经开放的端口：

~~~
firewall-cmd --list-ports  
~~~

开启端口：

~~~
firewall-cmd --zone=public --add-port=6379/tcp --permanent  
~~~

命令含义：

- zone #作用域

- add-port=80/tcp #添加端口，格式为：端口/通讯协议

- permanent #永久生效，没有此参数重启后失效

重启firewall：

~~~
firewall-cmd --reload  
~~~

停止firewall ：

~~~
systemctl stop firewalld.service  
~~~

禁止firewall开机启动：  

~~~
systemctl disable firewalld.service   
~~~

查看防火墙状态：

~~~
firewall-cmd --state
~~~



