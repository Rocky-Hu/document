KeyGenerator类与KeyPairGenerator类相似，KeyGenerator类用来生成秘密密钥，我们称它为秘密密钥生成器。

> 该类提供秘密(对称)密钥生成器的功能。

~~~java
// 实例化密钥生成器
KeyGenerator kg = KeyGenerator.getInstance("DES");
// 初始化
kg.init(56);
// 生成秘密密钥
SecretKey secretKey = kg.generateKey();
// 获得密钥的二进制编码形式
byte[] b = secretKey.getEncoded();
~~~

