# 一、编码格式

同样的 X.509 证书，可能有不同的编码格式，目前有以下两种编码格式。

## 1.1. PEM - Privacy Enhanced Mail

打开看文本格式，以“-----BEGIN...”开头，“-----END...”结尾，内容是 BASE64 编码。

查看 PEM 格式证书的信息：

~~~
openssl x509 -in certificate.pem -text -noout 
~~~

Apache 和 UNIX 服务器偏向于使用这种编码格式。

## 1.2. **DER** - Distinguished Encoding Rules

打开看是二进制格式，不可读。

查看 DER 格式证书的信息：

~~~
openssl x509 -in certificate.der -inform der -text -noout 
~~~

Java 和 Windows 服务器偏向于使用这种编码格式。

# 二、相关的文件扩展名

这是比较误导人的地方，虽然我们已经知道有 PEM 和 DER 这两种编码格式，但文件扩展名并不一定就叫“PEM”或者“DER”，常见的扩展名除了 PEM 和 DER 还有以下这些，它们除了编码格式可能不同之外，内容也有差别，但大多数都能相互转换编码格式。

## 2.1. CRT

CRT 应该是 certificate 的三个字母，其实还是证书的意思。常见于 UNIX 系统，有可能是 PEM 编码，也有可能是 DER 编码，大多数应该是 PEM 编码，相信你已经知道怎么辨别。

## 2.2. CER

还是 certificate，还是证书。常见于 Windows 系统，同样的可能是 PEM 编码，也可能是 DER 编码，大多数应该是 DER 编码。

## 2.3. KEY

通常用来存放一个公钥或者私钥，并非 X.509 证书。编码同样的，可能是 PEM，也可能是 DER。

查看 KEY 的办法：

~~~
openssl rsa -in mykey.key -text -noout 
~~~

如果是 DER 格式的话，同理应该这样了：

~~~
openssl rsa -in mykey.key -text -noout -inform der 
~~~

## 2.4. CSR

Certificate，Signing Request，即证书签名请求。这个并不是证书，而是向权威证书颁发机构获得签名证书的申请，其核心内容是一个公钥（当然还附带了一些别的信息）。在生成这个申请的时候，同时也会生成一个私钥，私钥要自己保管好。做过 iOS APP 的朋友都应该知道是，怎么向苹果申请开发者证书的吧。

查看的办法：

~~~
openssl req -noout -text -in my.csr 
~~~

如果是 DER 格式的话照旧加上 -inform der，这里不写了。

## 2.5. **PFX/P12**

predecessor of PKCS#12，对 unix 服务器来说,一般 CRT 和 KEY 是分开存放在不同文件中的，但 Windows 的 IIS 则将它们存在一个 PFX 文件中，(因此这个文件包含了证书及私钥)这样会不会不安全？应该不会，PFX 通常会有一个“提取密码”，你想把里面的东西读取出来的话，它就要求你提供提取密码，PFX 使用的时 DER 编码，如何把 PFX 转换为 PEM 编码？

openssl pkcs12 -in for-iis.pfx -out for-iis.pem -nodes

这个时候会提示你输入提取代码，for-iis.pem 就是可读的文本。

生成 pfx 的命令类似这样：openssl pkcs12 -export -in certificate.crt -inkey privateKey.key -out certificate.pfx -certfile CACert.crt

其中 CACert.crt 是 CA（权威证书颁发机构）的根证书，有的话也通过 -certfile 参数一起带进去。这么看来，PFX 其实是个证书密钥库。

# 三、自己的理解

PEM 和 DER是不同的编码方式，PEM在 linux 上常用，DER 在 window 上常用，可以相互转换。

在 linux 上常用 crt 和 key 俩文件，在 window 常用 PFX，PFX 里边包含证书和私钥，可以用来生成，也可以通过证书和私钥合成 PFX，都是可以的。

