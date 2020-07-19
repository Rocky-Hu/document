在使用Spring框架的时候，对于自定义的bean有这样的需求：感知自己的创建信息和访问Spring框架对象。如何做到呢？

Spring提供了Aware接口，自定义的类只要实现了这个接口（具体功能的子接口），那么在Spring创建自定义类的实例的时候就会为其注入需要的信息。名曰：感知Spring容器。

# 一、常见Aware接口使用

Aware接口是一个标记接口，其子接口提供了具体的功能需求，根据需求继承相关的子接口。

## 1.1. BeanNameAware

Spring容器创建Bean，其中的一个信息就是Bean的name，想要知道创建出的Bean的名称，就可以通过实现这个接口，Spring会告知。

作用：在Bean中得到它在IOC容器中的Bean的实例的名字。

~~~java
public class MyBeanName implements BeanNameAware {

    @Override
    public void setBeanName(String name) {
        System.out.println(name);
    }

}

@Configuration
public class AwareConfig {

    @Bean(name = "myCustomBeanName")
    public MyBeanName getMyBeanName() {
        return new MyBeanName();
    }

}

public class AwareTest {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AwareConfig.class);
    }

}
~~~

输出结果：

~~~
myCustomBeanName
~~~

## 1.2. BeanFactoryAware

BeanFactory对象是Spring的框架对象，实现这个接口可以将BeanFactory对象的引用给你，供你使用。

作用：在Bean中得到Bean所在的IOC容器，从而直接在Bean中使用IOC容器的服务。

~~~java
public class MyBeanFactory implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void getMyBeanName() {
        MyBeanName myBeanName = beanFactory.getBean(MyBeanName.class);
        System.out.println(beanFactory.isSingleton("myCustomBeanName"));
    }

}
~~~

测试：

~~~java
MyBeanFactory myBeanFactory = context.getBean(MyBeanFactory.class);
myBeanFactory.getMyBeanName();
~~~

输出：

~~~
true
~~~

## 1.3. ApplicationContextAware

作用：在Bean中得到Bean所在的应用上下文，从而直接在Bean中使用上下文的服务。

~~~java
public class MyApplicationContext implements ApplicationContextAware {
    
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
}
~~~

## 1.4. MessageSourceAware

作用：在Bean中得到消息源。

~~~java
在资源目录下定义两个messsage文件：
/resources
	/messages
		/msg_zh_cn.properties
		/msg_en_us.properties

msg_zh_cn.properties
	l1=你好 {0}
msg_en_us.properties
  l1=Hello {0}

public class MyMessageSource implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void sayHello(String name) {
        System.out.println(messageSource.getMessage("l1", new Object[]{name},
                Locale.getDefault()));
    }

}

@Configuration
public class AwareConfig {

    @Bean
    public MyMessageSource getMyMessageSource() {
        return new MyMessageSource();
    }

    @Bean
    public MessageSource messageSource () {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/msg");
        return messageSource;
    }

}

public class AwareTest {

    public static void main(String[] args) {
      	Locale.setDefault(Locale.US);
      
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AwareConfig.class);
        MyMessageSource myMessageSource = context.getBean(MyMessageSource.class);
        myMessageSource.sayHello("rocky");
    }

}
~~~

执行结果：

~~~
Hello, rocky
~~~

# 二、自定义Aware

~~~java
public interface ActiveProfilesAware extends Aware {

    void setActiveProfiles(List<String> activeProfiles);

}

public class PersonService implements ActiveProfilesAware {

    private List<String> activeProfiles;

    @Override
    public void setActiveProfiles(List<String> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

}

public class ActiveProfilesPostProcessor implements BeanPostProcessor {

    private final Environment environment;

    @Autowired
    public ActiveProfilesPostProcessor(Environment environment) {
        this.environment = environment;
    }

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ActiveProfilesAware) {
            List<String> activeProfiles = Arrays.stream(environment.getActiveProfiles()).collect(Collectors.toList());
            ((ActiveProfilesAware) bean).setActiveProfiles(activeProfiles);
            return bean;
        }

        return bean;
    }

}
~~~

# 三、Aware接口执行原理

在上面的Aware接口使用的示例中，需要感知Spring容器的用户bean实现了Aware的子接口，子接口都有个特点就是都提供了setter方法，这个是给Bean设值常用的方法。给bean设置属性的方式有构造方法传递和setter方法传递的方式，setter方式的设置时机是在bean对象创造出来之后再调用setter方法进行设置。对于Spring容器来说，处理流程也是一样的，在实例化bean之后，会有个时机再调用bean的setter方法将需要的信息设置进来。







