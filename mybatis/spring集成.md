org.mybatis.spring.mapper.MapperScannerConfigurer

org.mybatis.spring.annotation.MapperScan

->

org.mybatis.spring.annotation.MapperScannerRegistrar

->

org.mybatis.spring.mapper.MapperScannerConfigurer

->

org.mybatis.spring.mapper.ClassPathMapperScanner#doScan

# MapperFactoryBean中的sqlSessionTemplate是如何创建的

通过此方法创建：

~~~java
org.mybatis.spring.support.SqlSessionDaoSupport#setSqlSessionFactory
~~~

在处理MapperFactoryBean bean定义的时候，如果没明确指定sqlSessionFactoryBeanName和sqlSessionFactory，则开启自定注入：

~~~java
if (!explicitFactoryUsed) {
	LOGGER.debug(() -> "Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
	definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
}
~~~

