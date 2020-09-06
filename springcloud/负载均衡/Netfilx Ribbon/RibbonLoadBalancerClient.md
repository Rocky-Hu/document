Spring Cloud Common包定义了LoadBalancerClient接口，操作LoadBalancerClient即可以进行负载均衡。RibbonLoadBalancerClient是这个接口的实现，基于Ribbon框架提供负载均衡能力。

~~~
public ServiceInstance choose(String serviceId, Object hint) {
		Server server = getServer(getLoadBalancer(serviceId), hint);
		if (server == null) {
			return null;
		}
		return new RibbonServer(serviceId, server, isSecure(server, serviceId),
				serverIntrospector(serviceId).getMetadata(server));
	}
~~~

具体选择哪个服务实例交给Ribbon底层的ILoadBalancer实现。

