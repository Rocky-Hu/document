# session第一次是怎么设置的

org.springframework.session.web.http.SessionRepositoryFilter.SessionRepositoryRequestWrapper#setCurrentSession这个方法用来设置当前session，第一次调用流程如下：

> ```
> SecurityContextPersistenceFilter在这之前执行，也有getSession的操作，但是发现没有session的时候是不会创建session的。
> org.springframework.security.web.context.SecurityContextPersistenceFilter#doFilter
> ->
> org.springframework.security.web.context.HttpSessionSecurityContextRepository#loadContext
> ~~~
> // 忽略
> HttpSession httpSession = request.getSession(false);
> // 忽略
> ~~~
> ```

![]()![9](D:\company\document\images\springsecurity\9.png)

session处理入口：

![](D:\company\document\images\springsecurity\10.png)

org.springframework.security.web.savedrequest.HttpSessionRequestCache#saveRequest

![](D:\company\document\images\springsecurity\11.png)

这里开始调用request.getSession()方法，而这里的request对象是被spring session重新包装过的：

org.springframework.session.web.http.SessionRepositoryFilter#doFilterInternal

![](D:\company\document\images\springsecurity\12.png)

getSession()的执行链路如下：

org.springframework.session.web.http.SessionRepositoryFilter.SessionRepositoryRequestWrapper#getSession()

->

org.springframework.session.web.http.SessionRepositoryFilter.SessionRepositoryRequestWrapper#getSession(boolean)

![](D:\company\document\images\springsecurity\14.png)

->

org.springframework.session.jdbc.JdbcOperationsSessionRepository#createSession

~~~
@Override
public JdbcSession createSession() {
    JdbcSession session = new JdbcSession();
    if (this.defaultMaxInactiveInterval != null) {
    	session.setMaxInactiveInterval(Duration.ofSeconds(this.defaultMaxInactiveInterval));
    }
    return session;
}
~~~

![](D:\company\document\images\springsecurity\15.png)

# session是怎么保存到数据库的

![](D:\company\document\images\springsecurity\16.png)

SessionRepositoryFilter后续的过滤链执行完成后，SessionRepositoryFilter会有一个commit session的操作。

org.springframework.session.web.http.SessionRepositoryFilter.SessionRepositoryRequestWrapper#commitSession

![](D:\company\document\images\springsecurity\17.png)

![](D:\company\document\images\springsecurity\18.png)

## 保存后的内容

![](D:\company\document\images\springsecurity\19.png)

![](D:\company\document\images\springsecurity\20.png)

登录成功之后：

![](D:\company\document\images\springsecurity\21.png)

![](D:\company\document\images\springsecurity\22.png)

## principal_name是怎么设置进去的

![](D:\company\document\images\springsecurity\23.png)

从上面可以看到principal_name的值是从session中获取的。

org.springframework.session.jdbc.JdbcOperationsSessionRepository.JdbcSession#getPrincipalName

~~~
String getPrincipalName() {
	return PRINCIPAL_NAME_RESOLVER.resolvePrincipal(this);
}
~~~

org.springframework.session.jdbc.JdbcOperationsSessionRepository.PrincipalNameResolver#resolvePrincipal

![](D:\company\document\images\springsecurity\24.png)

红色标记处是跟Spring Security相关的。

~~~
private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
~~~

那么这个authentication对象是什么呢？

肯定是跟Spring Security相关的，是Spring Security负责放入到session中的。

设置的流程如下：

org.springframework.security.web.context.HttpSessionSecurityContextRepository.SaveToSessionResponseWrapper#saveContext

![](D:\company\document\images\springsecurity\25.png)