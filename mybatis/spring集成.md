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

# 流程

org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration#sqlSessionFactory

->

org.mybatis.spring.SqlSessionFactoryBean#getObject

->

org.mybatis.spring.SqlSessionFactoryBean#afterPropertiesSet

->

org.mybatis.spring.SqlSessionFactoryBean#buildSqlSessionFactory

->

org.apache.ibatis.builder.xml.XMLMapperBuilder#parse

->

org.apache.ibatis.builder.xml.XMLMapperBuilder#configurationElement

->

org.apache.ibatis.builder.xml.XMLMapperBuilder#buildStatementFromContext(java.util.List<org.apache.ibatis.parsing.XNode>)

->

org.apache.ibatis.builder.xml.XMLMapperBuilder#buildStatementFromContext(java.util.List<org.apache.ibatis.parsing.XNode>, java.lang.String)

->

org.apache.ibatis.builder.xml.XMLStatementBuilder#parseStatementNode

