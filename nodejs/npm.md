# 一、安装和配置



# 二、命令

## 查看配置文件路径

~~~shell
C:\Users\87490> npm config get userconfig
C:\Users\87490\.npmrc
~~~

## 读取配置文件顺序

~~~
.npmrc 配置文件位置(读取顺序)

项目配置文件: /path/to/my/project/.npmrc
用户配置文件：$HOME/.npmrc
全局配置文件：$PREFIX/etc/npmrc 配置目录, 使用 npm config get prefix 获取$PREFIX
npm内嵌配置文件：/path/to/npm/npmrc 与npm同级别, 使用which npm获取 npm的路径
~~~

# 修改全局依赖包下载路径

