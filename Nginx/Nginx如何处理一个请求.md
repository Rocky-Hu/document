# 基于名字的虚拟主机

Nginx首先选定由哪一个*虚拟主机*来处理请求。让我们从一个简单的配置（其中全部3个虚拟主机都在端口*：80上监听）开始：

~~~
server {
    listen      80;
    server_name example.org www.example.org;
    ...
}

server {
    listen      80;
    server_name example.net www.example.net;
    ...
}

server {
    listen      80;
    server_name example.com www.example.com;
    ...
}
~~~

在这个配置中，nginx仅仅检查请求的“Host”头以决定该请求应由哪个虚拟主机来处理。如果Host头没有匹配任意一个虚拟主机，或者请求中根本没有包含Host头，那nginx会将请求分发到定义在此端口上的默认虚拟主机。在以上配置中，第一个被列出的虚拟主机即nginx的默认虚拟主机——这是nginx的默认行为。而且，可以显式地设置某个主机为默认虚拟主机，即在"`listen`"指令中设置"`default_server`"参数：

~~~
server {
    listen      80 default_server;
    server_name example.net www.example.net;
    ...
}
~~~

> "`default_server`"参数从0.8.21版开始可用。在之前的版本中，应该使用"`default`"参数代替。

请注意"`default_server`"是监听端口的属性，而不是主机名的属性。后面会对此有更多介绍。

# 如何防止处理未定义主机名的请求

如果不允许请求中缺少“Host”头，可以定义如下主机，丢弃这些请求：

~~~
server {
    listen       80;
    server_name  "";
    return       444;
}
~~~

在这里，我们设置主机名为空字符串以匹配未定义“Host”头的请求，而且返回了一个nginx特有的，非http标准的返回码444，它可以用来关闭连接。

> 从0.8.48版本开始，这已成为主机名的默认设置，所以可以省略`server_name ""`。而之前的版本使用机器的*hostname*作为主机名的默认值。

# 基于域名和IP混合的虚拟主机

下面让我们来看一个复杂点的配置，在这个配置里，有几个虚拟主机在不同的地址上监听：

~~~
server {
    listen      192.168.1.1:80;
    server_name example.org www.example.org;
    ...
}

server {
    listen      192.168.1.1:80;
    server_name example.net www.example.net;
    ...
}

server {
    listen      192.168.1.2:80;
    server_name example.com www.example.com;
}
~~~

这个配置中，nginx首先测试请求的IP地址和端口是否匹配某个[server](https://tengine.taobao.org/nginx_docs/cn/docs/http/ngx_http_core_module.html#server)配置块中的[listen](https://tengine.taobao.org/nginx_docs/cn/docs/http/ngx_http_core_module.html#listen)指令配置。接着nginx继续测试请求的Host头是否匹配这个[server](https://tengine.taobao.org/nginx_docs/cn/docs/http/ngx_http_core_module.html#server)块中的某个[server_name](https://tengine.taobao.org/nginx_docs/cn/docs/http/ngx_http_core_module.html#server_name)的值。如果主机名没有找到，nginx将把这个请求交给默认虚拟主机处理。例如，一个从192.168.1.1:80端口收到的访问`www.example.com`的请求将被监听192.168.1.1:80端口的默认虚拟主机处理，本例中就是第一个服务器，因为这个端口上没有定义名为`www.example.com`的虚拟主机。

默认服务器是监听端口的属性，所以不同的监听端口可以设置不同的默认服务器：

~~~
server {
    listen      192.168.1.1:80;
    server_name example.org www.example.org;
    ...
}

server {
    listen      192.168.1.1:80 default_server;
    server_name example.net www.example.net;
    ...
}

server {
    listen      192.168.1.2:80 default_server;
    server_name example.com www.example.com;
    ...
}
~~~

