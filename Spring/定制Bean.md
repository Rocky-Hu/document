Spring框架提供了许多接口，您可以使用它们来定制bean的性质。包括：

- 生命周期回调
- ApplicationContextAware和BeanNameAware
- 其他Aware接口

# 一、生命周期回调

在Spring初始化bean完成后加入自己的定制逻辑可以使用初始化回调方式来操作；同样，在Spring销毁bean的时候可以使用销毁回调。

## 1.1. 初始化回调

容器在bean上设置了所有必需的属性后，就可以执行初始化工作。

### 1.1.1. 指定init-method

可以显示得指定初始化定制方法。

~~~java
public class ExampleBean {
  public void init() {
  // do some initialization work
  }
}
~~~

xml配置：

~~~xml
<bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
~~~

java config方式：

~~~java
@Configuration
public class Config {
    
    @Bean(initMethod = "init")
    public ExampleBean getExampleBean() {
        return new ExampleBean();
    }
    
}
~~~

### 1.1.2. 实现InitializingBean接口

~~~java
public class AnotherExampleBean implements InitializingBean {
  @Override
  public void afterPropertiesSet() {
  // do some initialization work
  }
}
~~~

### 1.1.3. 使用@PostConstruct注解

~~~java
public class FooService {
    @PostConstruct
    public void init() {
        // custom initialization logic
    }
}

@Configuration
public class ApplicationConfig {
    @Bean
    public FooService fooService() {
        return new FooService();
    }
}
        
~~~

## 1.2. 销毁回调

### 1.2.1. 指定destory-method

~~~java
public class ExampleBean {
  public void cleanup() {
  // do some destruction work (like releasing pooled connections)
  }
}
~~~

xml配置：

~~~xml
<bean id="exampleInitBean" class="examples.ExampleBean" destroy-method="cleanup"/>
~~~

java config方式：

~~~java
@Configuration
public class Config {
    
    @Bean(destroyMethod = "cleanup")
    public ExampleBean getExampleBean() {
        return new ExampleBean();
    }
    
}
~~~

### 1.2.2. 实现DisposableBean接口

~~~java
public class AnotherExampleBean implements DisposableBean {
  @Override
  public void destroy() {
  // do some destruction work (like releasing pooled connections)
  }
}
~~~

### 1.2.3. 使用@PreDestory注解

~~~java
public class FooService {
    @PreDestroy
    public void cleanup() {
       // destruction logic
    }
}

@Configuration
public class ApplicationConfig {
    @Bean
    public FooService fooService() {
        return new FooService();
    }
}
~~~

## 1.3. 指定默认的初始化和销毁方法

在项目中统一初始化和销毁方法的命名，比如说初始化方法命名为init()，销毁方法命名为destroy()。

统一命名之后，就不用在每个bean上配置init-method和destroy-method属性。而是使用默认配置。在<beans/>标签上指定默认的初始化和销毁方法。示例如下：

~~~xml
<beans default-init-method="init" default-destroy-method="destroy">

    <bean id="blogService" class="com.something.DefaultBlogService">
        <property name="blogDao" ref="blogDao" />
    </bean>

</beans>
~~~

> bean标签定义的init-method和destroy-method会覆盖默认设置

## 1.4. 组合生命周期机制

控制Bean的生命周期行为有三种方式：

- 实现InitializingBean和DisposableBean接口
- 自定义init()和destroy()方法
- 使用@PostConstruct和@PreDestroy注解

当这三种同时作用在一个bean上面时，调用顺序如下：

对于初始化回调：

1. 用@PostConstruct注释的方法；
2. 由InitializingBean回调接口定义的afterPropertiesSet()方法；
3. 定制配置的init()方法。

对于销毁回调：

1. 用@PreDestroy注释的方法；
2. 由DisposableBean回调接口定义的destroy()方法；
3. 定制配置的destroy()方法。

## 1.5. 启动(startup)和停止(shutdown)回调

### 1.5.1. Lifecycle接口

接口方法定义如下：

~~~java
public interface Lifecycle {
  void start();
  void stop();
  boolean isRunning();
}
~~~

这个接口有什么作用呢？

> The Lifecycle interface defines the essential methods for any object that has its own lifecycle requirements(such as starting and stoping some background process).
>
> Can be implemented by both components (typically a Spring bean defined in a Spring context) and containers  (typically a Spring ApplicationContext itself). Containers will propagate start/stop signals to all components that apply within each container, e.g. for a stop/restart scenario at runtime.

像Tomcat、服务端自定义的Server这样的组件有启动和停止之说，集成到Spring，负责启动和停止的代码就相当于一个Bean组件。那么集成后怎么启动和停止这种服务组件呢。使用Spring我们操作的是Spring容器，Spring容器启动时负责加载所有的bean组件。Spring容器本身也有启动和停止之说，那么最后的处理方式就是Spring容器启动触发自定义服务组件启动，反之亦然，这样就很好的将多个有启停需求的组件集成到Spring容器中。

Spring提供的Lifecycle接口就是一个规范，实现此接口然后重写启停方法以处理自己的启停要求。Spring启动或者停止时会识别实现了Lifecycle接口的组件，然后相应得调用start和stop操作。

示例：

~~~java
public class HelloLifeCycle implements Lifecycle {

    private volatile boolean running = false;

    public HelloLifeCycle() {
        System.out.println("HelloLifeCycle构造方法!!!");
    }

    @Override
    public void start() {
        System.out.println("lifecycle start");
        running = true;
    }

    @Override
    public void stop() {
        System.out.println("lifecycle stop");
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

}

public class LifecycleExample {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(LifecycleExampleConfiguration.class);
        applicationContext.start();
    }

}

~~~

输出：

~~~java
HelloLifeCycle构造方法!!!
lifecycle start
~~~

测试发现：需要调用applicationContext.start()才会触发HelloLifeCycle的start方法。没有调用applicationContext的stop方法，main方法执行结束，Spring容器停止，HelloLifeCycle的stop方法没被调用。

ApplicationContext同样实现了Lifecycle，调用start和stop通过级联调用的方式触发容器内其他实现了Lifecycle接口的组件执行。Lifecycle没有自动自动启动的语义。

### 1.5.2. LifecycleProcessor接口

上面说了Spring容器ApplicationContext同样实现了Lifecycle接口，它是整个实现了Lifecycle的组件的启动触发点。

ApplicationContext实现Lifecycle，方法重写逻辑如下：

~~~java
@Override
public void start() {
	getLifecycleProcessor().start();
	publishEvent(new ContextStartedEvent(this));
}

@Override
public void stop() {
	getLifecycleProcessor().stop();
	publishEvent(new ContextStoppedEvent(this));
}
~~~

这里就引入了这小节要讲的LifecycleProcessor接口。

LifecycleProcessor定义如下：

~~~java
public interface LifecycleProcessor extends Lifecycle {

	/**
	 * Notification of context refresh, e.g. for auto-starting components.
	 */
	void onRefresh();

	/**
	 * Notification of context close phase, e.g. for auto-stopping components.
	 */
	void onClose();

}
~~~

同样实现了Lifecycle接口，并新增两个方法，供容器刷新和关闭调用。

LifecycleProcessor默认的实现为org.springframework.context.support.DefaultLifecycleProcessor。

~~~java
/**
	 * Start all registered beans that implement {@link Lifecycle} and are <i>not</i>
	 * already running. Any bean that implements {@link SmartLifecycle} will be
	 * started within its 'phase', and all phases will be ordered from lowest to
	 * highest value. All beans that do not implement {@link SmartLifecycle} will be
	 * started in the default phase 0. A bean declared as a dependency of another bean
	 * will be started before the dependent bean regardless of the declared phase.
	 */
	@Override
	public void start() {
		startBeans(false);
		this.running = true;
	}
~~~

startBeans启动原理：获取所有实现了Lifecycle的bean，然后调用start方法。

> 这里会根据启动顺序做处理。

### 1.5.3. SmartLifecyle接口



# 二、ApplicationContextAware和BeanNameAware

参考相关的文章。

# 三、其他Aware接口

参考相关文章。







