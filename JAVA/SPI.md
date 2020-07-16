# 1. Java SPI 组件

SPI实现中有4个组件：

1. **Service Provider Interface**

   服务接口或抽象类。

2. **Service Providers**

   实际提供服务的实现类。

3. **SPI Configuration File**

   提供查找服务实现逻辑的特殊文件。文件名必须出现在META-INF/services目录中。文件名应该与服务提供程序接口的完全限定名完全相同。文件中的每一行都有一个实现服务类细节，同样是服务提供程序类的完全限定名。

4. **Service Provider**

   用于为服务提供者接口加载服务的Java SPI主类。ServiceLoader中有各种实用方法来获得特定的实现、迭代它们或再次重新加载服务。

# 2. Java SPI示例

## 2.1. Service Provider Interface

~~~~java
public interface MessageServiceProvider {

    void sendMessage(String message);
    
}
~~~~

## 2.2. Service Provider实现类

~~~java
public class EmailServiceProvider implements MessageServiceProvider {

    public void sendMessage(String message) {
        System.out.println("Sending Email with Message = "+message);
    }

}
~~~

~~~java
public class PushNotificationServiceProvider implements MessageServiceProvider {

    public void sendMessage(String message) {
        System.out.println("Sending Push Notification with Message = "+message);
    }

}
~~~

## 2.3. Service Provider Configuration file

配置文件必须在META-INF/services目录中创建。它的名称应该是“org.newbie.javaer.spi.MessageServiceProvider”。我们将在这个文件中指定这两个实现类。

~~~
org.newbie.javaer.spi.EmailServiceProvider
org.newbie.javaer.spi.PushNotificationServiceProvider
~~~

## 2.4. 用于加载服务的ServiceLoader示例

~~~java

import java.util.ServiceLoader;

public class ServiceLoaderTest {

    public static void main(String[] args) {
        ServiceLoader<MessageServiceProvider> serviceLoader = ServiceLoader.load(MessageServiceProvider.class);

        for (MessageServiceProvider service : serviceLoader) {
            service.sendMessage("Hello");
        }

        // using Java 8 forEach() method
        serviceLoader.forEach((service) -> service.sendMessage("Have a Nice Day!"));
    }

}
~~~

# 3. ServiceLoader实现原理

ServiceLoader加载接口实现类的原理如下：

- 从META-INF/services目录中加载指定接口全限定名的文件；
- 解析获取获取文件中的内容，每一行为接口实现类的全限定名；
- 根据解析出的接口实现类的全限定名，通过Class.forName方法进行类的加载；
- 通过Class实例的newInstance方法实例化出接口实现类的实例，然后放入到LinkedHashMap缓存中；
- 从LinkedHashMap缓存中获取实例进行使用。

# 4. 总结

Java SPI提供了一种在应用程序中动态配置和加载服务的简单方法。但是，它在很大程度上依赖于服务配置文件，文件中的任何更改都可能破坏应用程序。













