# openssl检查

~~~
[root@localhost sbin]# openssl version
OpenSSL 1.0.2k-fips  26 Jan 2017
~~~

## 安装（更新）

https://gist.github.com/fernandoaleman/5459173e24d59b45ae2cfc618e20fe06

# 编译nginx时启用https module

~~~
./configure --prefix=/usr/local/nginx --add-module=/opt/nginx-http-flv-module-1.2.9 --with-http_stub_status_module --with-http_ssl_module --with-debug 
~~~

# 已编译的nginx启用https module

~~~
编译开始根据如下：

1.示例：nginx的安装目录是/usr/local/nginx，源码包在/root/nginx-1.10.1目录下。

2.切换到源码包：

# cd /root/nginx-1.10.1

3.进行编译：

# ./configure --prefix=/usr/local/nginx --with-http_stub_status_module --with-http_ssl_module

4.配置完成后，运行命令：

# make

5.make命令执行后，不要进行make install，否则会覆盖安装。

6.备份原有已安装好的nginx：

# cp /usr/local/nginx/sbin/nginx /usr/local/nginx/sbin/nginx.bak

7.停止nginx状态：

# /usr/local/nginx/sbin/nginx -s stop

8.将编译好的nginx覆盖掉原有的nginx：

# cd /root/nginx-1.10.1/

# cp ./objs/nginx /usr/local/nginx/sbin/

9.提示是否覆盖，输入yes即可。

10.然后启动nginx：

# /usr/local/nginx/sbin/nginx

11.进入nginx/sbin目录下，通过命令查看模块是否已经加入成功：

# cd /usr/local/nginx/sbin/

# ./nginx -V
~~~

# 配置

http://nginx.org/en/docs/http/configuring_https_servers.html

http://nginx.org/en/docs/http/ngx_http_ssl_module.html

