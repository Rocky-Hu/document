Ribbon客户端端配置类。使用过原生的Ribbon框架可以知道，创建出一个ILoadBalancer需要下面这些组件：

- IClientConfig

  客户端配置信息

- IRule

  负载均衡策略

- IPing

  心跳反应

- ServerList

  服务列表

- ServerListUpdater

  服务列表更新器

- ServerListFilter

  服务列表过滤器

提供了这些信息才能创建出一个ILoadBalancer（负载均衡器）。

RibbonClientConfiguration配置类就是用于配置这些组件。

~~~
@Bean
@ConditionalOnMissingBean
public IClientConfig ribbonClientConfig() {
	DefaultClientConfigImpl config = new DefaultClientConfigImpl();
	config.loadProperties(this.name);
	config.set(CommonClientConfigKey.ConnectTimeout, DEFAULT_CONNECT_TIMEOUT);
	config.set(CommonClientConfigKey.ReadTimeout, DEFAULT_READ_TIMEOUT);
	config.set(CommonClientConfigKey.GZipPayload, DEFAULT_GZIP_PAYLOAD);
	return config;
}

@Bean
@ConditionalOnMissingBean
public IRule ribbonRule(IClientConfig config) {
	if (this.propertiesFactory.isSet(IRule.class, name)) {
		return this.propertiesFactory.get(IRule.class, config, name);
	}
	ZoneAvoidanceRule rule = new ZoneAvoidanceRule();
	rule.initWithNiwsConfig(config);
	return rule;
}

@Bean
@ConditionalOnMissingBean
public IPing ribbonPing(IClientConfig config) {
	if (this.propertiesFactory.isSet(IPing.class, name)) {
		return this.propertiesFactory.get(IPing.class, config, name);
	}
	return new DummyPing();
}

@Bean
@ConditionalOnMissingBean
@SuppressWarnings("unchecked")
public ServerList<Server> ribbonServerList(IClientConfig config) {
	if (this.propertiesFactory.isSet(ServerList.class, name)) {
		return this.propertiesFactory.get(ServerList.class, config, name);
	}
	ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
	serverList.initWithNiwsConfig(config);
	return serverList;
}

@Bean
@ConditionalOnMissingBean
public ServerListUpdater ribbonServerListUpdater(IClientConfig config) {
	return new PollingServerListUpdater(config);
}

@Bean
@ConditionalOnMissingBean
public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
		ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
		IRule rule, IPing ping, ServerListUpdater serverListUpdater) {
	if (this.propertiesFactory.isSet(ILoadBalancer.class, name)) {
		return this.propertiesFactory.get(ILoadBalancer.class, config, name);
	}
	return new ZoneAwareLoadBalancer<>(config, rule, ping, serverList,
			serverListFilter, serverListUpdater);
}

@Bean
@ConditionalOnMissingBean
@SuppressWarnings("unchecked")
public ServerListFilter<Server> ribbonServerListFilter(IClientConfig config) {
	if (this.propertiesFactory.isSet(ServerListFilter.class, name)) {
		return this.propertiesFactory.get(ServerListFilter.class, config, name);
	}
	ZonePreferenceServerListFilter filter = new ZonePreferenceServerListFilter();
	filter.initWithNiwsConfig(config);
	return filter;
}
~~~

