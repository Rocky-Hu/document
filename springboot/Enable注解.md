# 实现原理

借助于@Import注解，运行时导入其他bean定义。

## 第一种：通过@Import导入ImportBeanDefinitionRegistrar注册额外的bean

~~~
@Import(FeignClientsRegistrar.class)
public @interface EnableFeignClients {

}
~~~

## 第二种：通过@Import导入定义了标记Bean的Configuration类

~~~
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EurekaServerMarkerConfiguration.class)
public @interface EnableEurekaServer {

}

@Configuration(proxyBeanMethods = false)
public class EurekaServerMarkerConfiguration {

	@Bean
	public Marker eurekaServerMarkerBean() {
		return new Marker();
	}

	class Marker {

	}

}

~~~

定义了一个标记Bean，自动配置化类中如果发现有这个bean存在，则进行相关联的bean的实例化。

~~~
@Configuration(proxyBeanMethods = false)
@Import(EurekaServerInitializerConfiguration.class)
@ConditionalOnBean(EurekaServerMarkerConfiguration.Marker.class)
@EnableConfigurationProperties({ EurekaDashboardProperties.class,
		InstanceRegistryProperties.class })
@PropertySource("classpath:/eureka/server.properties")
public class EurekaServerAutoConfiguration implements WebMvcConfigurer {

}
~~~

## 第三种：通过@Import导入ImportSelector

~~~
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AsyncConfigurationSelector.class)
public @interface EnableAsync {
}
~~~



