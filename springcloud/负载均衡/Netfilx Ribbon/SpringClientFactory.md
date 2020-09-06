这个类很重要，看这个类的注释：

~~~
A factory that creates client, load balancer and client configuration instances. It creates a Spring ApplicationContext per client name, and extracts the beans that it needs from there.
~~~

创建client，load balancer和客户端配置实例的工厂。它为每一个client创建一个Spring ApplicationContext，然后从这里来提取beans。

org.springframework.cloud.context.named.NamedContextFactory#createContext负责创建上下文对象，创建的是一个AnnotationConfigApplicationContext实例。

~~~
protected AnnotationConfigApplicationContext createContext(String name) {
	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	if (this.configurations.containsKey(name)) {
		for (Class<?> configuration : this.configurations.get(name)
				.getConfiguration()) {
			context.register(configuration);
		}
	}
	for (Map.Entry<String, C> entry : this.configurations.entrySet()) {
		if (entry.getKey().startsWith("default.")) {
			for (Class<?> configuration : entry.getValue().getConfiguration()) {
				context.register(configuration);
			}
		}
	}
	context.register(PropertyPlaceholderAutoConfiguration.class,
			this.defaultConfigType);
	context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(
			this.propertySourceName,
			Collections.<String, Object>singletonMap(this.propertyName, name)));
	if (this.parent != null) {
		// Uses Environment from parent as well as beans
		context.setParent(this.parent);
		// jdk11 issue
		// https://github.com/spring-cloud/spring-cloud-netflix/issues/3101
		context.setClassLoader(this.parent.getClassLoader());
	}
	context.setDisplayName(generateDisplayName(name));
	context.refresh();
	return context;
}
~~~

这里看到了上下文对象的refresh方法，进行bean解析和注册。上下文中定义的bean从何而来呢？回忆正常使用上下文对象，要么就是使用xml配置方式制定类路径下的配置文件，要么就是使用annotation方式指定@Configuration配置类。SpringClientFactory使用的是注解方式，在其构造方法中定义了配置类。

~~~
public SpringClientFactory() {
	super(RibbonClientConfiguration.class, NAMESPACE, "ribbon.client.name");
}
~~~

这个Configuration类就是RibbonClientConfiguration。







