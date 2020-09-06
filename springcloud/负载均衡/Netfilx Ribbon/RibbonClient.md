要明白这个注解的作用，需先看SpringClientFactory类和RibbonClientConfiguration类。

RibbonClientConfiguration类定义了Ribbon负载均衡相关组件。然后SpringClientFactory类通过这个配置文件来创建上下文，解析和注册组件。SpringClientFactory默认使用RibbonClientConfiguration这个配置。但是如果我们需要定制Ribbon，那么就需要修改Ribbon的组件定义方式，也就是说RibbonClientConfiguration的默认配置不满足我们的需要。这时候RobbinClient注解就派上了用场。看下这个注解的注释：

~~~
Declarative configuration for a ribbon client. Add this annotation to any <code>@Configuration</code> and then inject a {@link SpringClientFactory} to access the client that is created.
~~~

RibbonClient可以指定具体的配置类，然后使用ImportBeanDefinitionRegistrar来处理。

~~~
@Import(RibbonClientConfigurationRegistrar.class)
~~~

RibbonClientConfigurationRegistrar这个类的主要作用就是获取RobbinClient类的configuration配置，然后将其包装为RibbonClientSpecification组件：

org.springframework.cloud.netflix.ribbon.RibbonClientConfigurationRegistrar#registerClientConfiguration

~~~
private void registerClientConfiguration(BeanDefinitionRegistry registry, Object name,
		Object configuration) {
	BeanDefinitionBuilder builder = BeanDefinitionBuilder
			.genericBeanDefinition(RibbonClientSpecification.class);
	builder.addConstructorArgValue(name);
	builder.addConstructorArgValue(configuration);
	registry.registerBeanDefinition(name + ".RibbonClientSpecification",
			builder.getBeanDefinition());
}
~~~

为什么要包装成RibbonClientSpecification呢？规范化处理，因为配置类要被加载到SpringClientFactory中，做一个统一规范处理。在RibbonAutoConfiguration中会收集这些RibbonClientSpecification，然后传递给SpringClientFactory。

~~~~
@Autowired(required = false)
private List<RibbonClientSpecification> configurations = new ArrayList<>();
	
@Bean
public SpringClientFactory springClientFactory() {
	SpringClientFactory factory = new SpringClientFactory();
	factory.setConfigurations(this.configurations);
	return factory;
}	
~~~~

org.springframework.cloud.context.named.NamedContextFactory#setConfigurations

~~~
public void setConfigurations(List<C> configurations) {
	for (C client : configurations) {
		this.configurations.put(client.getName(), client);
	}
}
~~~

其实也就是相当于拉取其他关于Ribbon的定制化配置，然后用于Ribbon组件的注册。







