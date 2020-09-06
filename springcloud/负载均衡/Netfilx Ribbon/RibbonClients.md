直接看注释：

~~~
Convenience annotation that allows user to combine multiple <code>@RibbonClient</code> annotations on a single class (including in Java 7).
~~~

再看源码：

~~~
@Configuration(proxyBeanMethods = false)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
@Import(RibbonClientConfigurationRegistrar.class)
public @interface RibbonClients {

	RibbonClient[] value() default {};

	Class<?>[] defaultConfiguration() default {};

}
~~~

它的作用就是讲多个@RibbonClient配置合并起来写。

