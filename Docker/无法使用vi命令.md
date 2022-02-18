~~~
apt-get update
apt-get install vim
~~~

实际在使用过程中，运行 apt-get update，然后执行 apt-get install -y vim，下载地址由于是海外地址，下载速度异常慢而且可能中断更新流程，所以做下面配置：

~~~
mv /etc/apt/sources.list /etc/apt/sources.list.bak
echo "deb http://mirrors.163.com/debian/ jessie main non-free contrib" >> /etc/apt/sources.list
echo "deb http://mirrors.163.com/debian/ jessie-proposed-updates main non-free contrib" >>/etc/apt/sources.list
echo "deb-src http://mirrors.163.com/debian/ jessie main non-free contrib" >>/etc/apt/sources.list
echo "deb-src http://mirrors.163.com/debian/ jessie-proposed-updates main non-free contrib" >>/etc/apt/sources.list
#更新安装源
apt-get update 
~~~

