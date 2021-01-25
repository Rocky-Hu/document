~~~java
@Bean(name = "commonDataSource")
@ConfigurationProperties(prefix = "spring.datasource")
public DataSource commonDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
}
~~~

实例注册成功后，会进行属性注入，处理的方法为：org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor#bind

