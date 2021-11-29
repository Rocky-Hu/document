TrustManagerFactory类是用于管理信任材料的管理器工厂。

# 构建信任管理工厂

~~~java
public class TrustManagerFactoryDemo {

    public static void main(String[] args) throws Exception {
        // 实例化TrustManagerFactory对象
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        // 加载密钥库文件
        FileInputStream is = new FileInputStream("d:\\x.keystore");
        // 实例化KeyStore对象
        KeyStore ks = KeyStore.getInstance("JKS");
        // 加载密钥库
        ks.load(is, "password".toCharArray());
        // 关闭流
        is.close();
        // 初始化TrustManagerFactory对象
        trustManagerFactory.init(ks);
    }

}
~~~

