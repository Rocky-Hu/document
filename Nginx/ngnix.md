# 安装

~~~
yum -y install pcre-devel gcc gcc-c++ autoconf automake make zlib-devel openssl openssl-devel

wget -c http://nginx.org/download/nginx-1.16.1.tar.gz

tar -zxvf nginx-1.16.1.tar.gz

cd nginx-1.16.1 # 进入到解压目录

./configure --prefix=/usr/local/nginx

make && make install

cd /usr/local/nginx

./nginx -c /usr/local/nginx/conf/nginx.conf

vim /etc/profile
export NGINX_HOME=/usr/local/nginx
export PATH=$PATH:$NGINX_HOME/sbin
source /etc/profile

nginx -s reload

./nginx -s stop

ln -s /usr/local/nginx/sbin/nginx /usr/bin/nginx

~~~

# 下载rtmp模块

~~~
git clone https://github.com/arut/nginx-rtmp-module.git

cd /opt/nginx/（源码路径）
./configure --prefix=/usr/local/nginx --conf-path=/usr/local/nginx/nginx.conf --add-module=/opt/nginx-rtmp-module
~~~

https://cloud.tencent.com/developer/article/1525055



