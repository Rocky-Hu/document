实现了DiscoveryClient的接口，会被收集，自动配置类org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration负责设置工作。

~~~java
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(SimpleDiscoveryClientAutoConfiguration.class)
public class CompositeDiscoveryClientAutoConfiguration {

	@Bean
	@Primary
	public CompositeDiscoveryClient compositeDiscoveryClient(
			List<DiscoveryClient> discoveryClients) {
		return new CompositeDiscoveryClient(discoveryClients);
	}

}
~~~

也就是实现了DiscoveryClient接口并且其实例注册到Spring容器中的组件，会被包装

到CompositeDiscoveryClient中。这个Bean用@Primary进行注解，那么在我们的应用代码中，使用：

~~~java
@Autowired
private DiscoveryClient discoveryClient;
~~~

这里DiscoveryClient的运行时类型就为CompositeDiscoveryClient。



