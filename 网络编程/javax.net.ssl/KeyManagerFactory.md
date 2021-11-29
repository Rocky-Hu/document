~~~
This class acts as a factory for key managers based on a source of key material. Each key manager manages a specific type of key material for use by secure sockets. The key material is based on a KeyStore and/or provider specific sources.
~~~

# 构建密钥管理工厂

~~~
public class KeyManagerFactoryDemo {

    public static void main(String[] args) throws Exception {
        // 实例化KeyManagerFactory对象
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        // 加载密钥库文件
        FileInputStream is = new FileInputStream("d:\\x.keystore");
        // 实例化KeyStore对象
        KeyStore ks = KeyStore.getInstance("JKS");
        // 加载密钥库
        ks.load(is, "password".toCharArray());
        // 关闭流
        is.close();
        // 初始化KeyManagerFactory对象
        keyManagerFactory.init(ks, "password".toCharArray());
    }

}
~~~

