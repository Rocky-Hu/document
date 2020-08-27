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

Spring代码中对于Aware接口的执行有两种方式：

- 初始化Bean的时候直接进行方法调用 -> setXXXX
- BeanPostProcessor -> Object postProcessBeforeInitialization(Object bean, String beanName)

## 3.1. 直接方法调用

初始化bean的入口为：org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)

方法如下：

~~~java
protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
		if (System.getSecurityManager() != null) {
			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
				invokeAwareMethods(beanName, bean);
				return null;
			}, getAccessControlContext());
		}
		else {
			invokeAwareMethods(beanName, bean);
		}

		Object wrappedBean = bean;
		if (mbd == null || !mbd.isSynthetic()) {
			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
		}

		try {
			invokeInitMethods(beanName, wrappedBean, mbd);
		}
		catch (Throwable ex) {
			throw new BeanCreationException(
					(mbd != null ? mbd.getResourceDescription() : null),
					beanName, "Invocation of init method failed", ex);
		}
		if (mbd == null || !mbd.isSynthetic()) {
			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
		}

		return wrappedBean;
	}
~~~

invokeAwareMethods就是直接调用Aware接口方法的入口。

~~~java
private void invokeAwareMethods(final String beanName, final Object bean) {
		if (bean instanceof Aware) {
			if (bean instanceof BeanNameAware) {
				((BeanNameAware) bean).setBeanName(beanName);
			}
			if (bean instanceof BeanClassLoaderAware) {
				ClassLoader bcl = getBeanClassLoader();
				if (bcl != null) {
					((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
				}
			}
			if (bean instanceof BeanFactoryAware) {
				((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
			}
		}
	}
~~~

它处理的三种具体的Aware类型：BeanNameAware、BeanClassLoaderAware和BeanFactoryAware。

## 3.2. BeanPostProcessor处理Aware接口

上面initializeBean方法有这样的一行：

~~~java
Object wrappedBean = bean;
if (mbd == null || !mbd.isSynthetic()) {
	wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
}

~~~

BeanPostProcessor进行初始化前处理。

ApplicationContextAwareProcessor就是用来处理非直接方法调用的Aware接口实现。

~~~java
private void invokeAwareInterfaces(Object bean) {
		if (bean instanceof EnvironmentAware) {
			((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
		}
		if (bean instanceof EmbeddedValueResolverAware) {
			((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
		}
		if (bean instanceof ResourceLoaderAware) {
			((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
		}
		if (bean instanceof ApplicationEventPublisherAware) {
			((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
		}
		if (bean instanceof MessageSourceAware) {
			((MessageSourceAware) bean).setMessageSource(this.applicationContext);
		}
		if (bean instanceof ApplicationContextAware) {
			((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
		}
	}
~~~



