继承WebSecurityConfigurerAdapter然后做如下配置：

~~~
protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(miLiaoAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(casAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests().antMatchers("/loginPage", "/sts", "/importData").permitAll()
                .anyRequest().authenticated().and()
//                关闭跨域检查
                .csrf().disable().cors().and()
//                配置session 管理
                .sessionManagement().and()
//                禁止匿名登录
                .anonymous().disable()
//                退出配置
                .logout().addLogoutHandler(logoutHandler()).logoutSuccessHandler(new ScmsLogoutSuccessHandler(casProperties, miLiaoProperties, securityProperties, envProperties)).and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).accessDeniedHandler(new ScmsAccessDeniedHandler());
    }
~~~

这段配置的作用是什么呢？

执行流程如下：

~~~
@EnableWebSecurity
->
org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration#springSecurityFilterChain
->
org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder#init
->
org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#init
->
org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#getHttp
->
重写的configure(HttpSecurity http)方法
~~~

这个configure方法主要是配置HttpSecurity。

那HttpSecurity是什么？它是一个securityFilterChainBuilder用来创建SecurityFilterChain实例。

# filters中的过滤器是怎么加进来的

第一个过滤器：

org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#getHttp

![](D:\company\document\images\springsecurity\6.png)

第二个和第三个过滤器：

~~~
 http.addFilterBefore(miLiaoAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(casAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
~~~

第四个过滤器：

org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer#configure

~~~
@Override
public void configure(H http) throws Exception {
    AuthenticationEntryPoint entryPoint = getAuthenticationEntryPoint(http);
    ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter(
    entryPoint, getRequestCache(http));
    AccessDeniedHandler deniedHandler = getAccessDeniedHandler(http);
    exceptionTranslationFilter.setAccessDeniedHandler(deniedHandler);
    exceptionTranslationFilter = postProcess(exceptionTranslationFilter);
    http.addFilter(exceptionTranslationFilter);
}
~~~

第五个过滤器：

org.springframework.security.config.annotation.web.configurers.HeadersConfigurer#configure

~~~
@Override
public void configure(H http) throws Exception {
    HeaderWriterFilter headersFilter = createHeaderWriterFilter();
    http.addFilter(headersFilter);
}
~~~

第六个过滤器：

org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer#configure

~~~
@Override
	public void configure(H http) throws Exception {
		SecurityContextRepository securityContextRepository = http
				.getSharedObject(SecurityContextRepository.class);
		SessionManagementFilter sessionManagementFilter = new SessionManagementFilter(
				securityContextRepository, getSessionAuthenticationStrategy(http));
		if (this.sessionAuthenticationErrorUrl != null) {
			sessionManagementFilter.setAuthenticationFailureHandler(
					new SimpleUrlAuthenticationFailureHandler(
							this.sessionAuthenticationErrorUrl));
		}
		InvalidSessionStrategy strategy = getInvalidSessionStrategy();
		if (strategy != null) {
			sessionManagementFilter.setInvalidSessionStrategy(strategy);
		}
		AuthenticationFailureHandler failureHandler = getSessionAuthenticationFailureHandler();
		if (failureHandler != null) {
			sessionManagementFilter.setAuthenticationFailureHandler(failureHandler);
		}
		AuthenticationTrustResolver trustResolver = http
				.getSharedObject(AuthenticationTrustResolver.class);
		if (trustResolver != null) {
			sessionManagementFilter.setTrustResolver(trustResolver);
		}
		sessionManagementFilter = postProcess(sessionManagementFilter);

		http.addFilter(sessionManagementFilter);
		if (isConcurrentSessionControlEnabled()) {
			ConcurrentSessionFilter concurrentSessionFilter = createConccurencyFilter(http);

			concurrentSessionFilter = postProcess(concurrentSessionFilter);
			http.addFilter(concurrentSessionFilter);
		}
	}
~~~

第7个过滤器：

org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer#configure

~~~
public void configure(H http) throws Exception {

		SecurityContextRepository securityContextRepository = http
				.getSharedObject(SecurityContextRepository.class);
		if (securityContextRepository == null) {
			securityContextRepository = new HttpSessionSecurityContextRepository();
		}
		SecurityContextPersistenceFilter securityContextFilter = new SecurityContextPersistenceFilter(
				securityContextRepository);
		SessionManagementConfigurer<?> sessionManagement = http
				.getConfigurer(SessionManagementConfigurer.class);
		SessionCreationPolicy sessionCreationPolicy = sessionManagement == null ? null
				: sessionManagement.getSessionCreationPolicy();
		if (SessionCreationPolicy.ALWAYS == sessionCreationPolicy) {
			securityContextFilter.setForceEagerSessionCreation(true);
		}
		securityContextFilter = postProcess(securityContextFilter);
		http.addFilter(securityContextFilter);
	}
~~~

第8个过滤器：

org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer#configure

~~~
@Override
	public void configure(H http) throws Exception {
		RequestCache requestCache = getRequestCache(http);
		RequestCacheAwareFilter requestCacheFilter = new RequestCacheAwareFilter(
				requestCache);
		requestCacheFilter = postProcess(requestCacheFilter);
		http.addFilter(requestCacheFilter);
	}
~~~

第9个过滤器：

org.springframework.security.config.annotation.web.configurers.ServletApiConfigurer#configure

~~~
@Override
	@SuppressWarnings("unchecked")
	public void configure(H http) throws Exception {
		securityContextRequestFilter.setAuthenticationManager(http
				.getSharedObject(AuthenticationManager.class));
		ExceptionHandlingConfigurer<H> exceptionConf = http
				.getConfigurer(ExceptionHandlingConfigurer.class);
		AuthenticationEntryPoint authenticationEntryPoint = exceptionConf == null ? null
				: exceptionConf.getAuthenticationEntryPoint(http);
		securityContextRequestFilter
				.setAuthenticationEntryPoint(authenticationEntryPoint);
		LogoutConfigurer<H> logoutConf = http.getConfigurer(LogoutConfigurer.class);
		List<LogoutHandler> logoutHandlers = logoutConf == null ? null : logoutConf
				.getLogoutHandlers();
		securityContextRequestFilter.setLogoutHandlers(logoutHandlers);
		AuthenticationTrustResolver trustResolver = http
				.getSharedObject(AuthenticationTrustResolver.class);
		if (trustResolver != null) {
			securityContextRequestFilter.setTrustResolver(trustResolver);
		}
		ApplicationContext context = http.getSharedObject(ApplicationContext.class);
		if (context != null) {
			String[] grantedAuthorityDefaultsBeanNames = context.getBeanNamesForType(GrantedAuthorityDefaults.class);
			if (grantedAuthorityDefaultsBeanNames.length == 1) {
				GrantedAuthorityDefaults grantedAuthorityDefaults = context.getBean(grantedAuthorityDefaultsBeanNames[0], GrantedAuthorityDefaults.class);
				securityContextRequestFilter.setRolePrefix(grantedAuthorityDefaults.getRolePrefix());
			}
		}
		securityContextRequestFilter = postProcess(securityContextRequestFilter);
		http.addFilter(securityContextRequestFilter);
	}
~~~

第10个过滤器：

org.springframework.security.config.annotation.web.configurers.LogoutConfigurer#configure

~~~
@Override
	public void configure(H http) throws Exception {
		LogoutFilter logoutFilter = createLogoutFilter(http);
		http.addFilter(logoutFilter);
	}
~~~

第11个过滤器：

org.springframework.security.config.annotation.web.configurers.AbstractInterceptUrlConfigurer#configure

~~~
@Override
	public void configure(H http) throws Exception {
		FilterInvocationSecurityMetadataSource metadataSource = createMetadataSource(http);
		if (metadataSource == null) {
			return;
		}
		FilterSecurityInterceptor securityInterceptor = createFilterSecurityInterceptor(
				http, metadataSource, http.getSharedObject(AuthenticationManager.class));
		if (filterSecurityInterceptorOncePerRequest != null) {
			securityInterceptor
					.setObserveOncePerRequest(filterSecurityInterceptorOncePerRequest);
		}
		securityInterceptor = postProcess(securityInterceptor);
		http.addFilter(securityInterceptor);
		http.setSharedObject(FilterSecurityInterceptor.class, securityInterceptor);
	}
~~~

第12个过滤器器：

org.springframework.security.config.annotation.web.configurers.CorsConfigurer#configure

~~~
@Override
	public void configure(H http) throws Exception {
		ApplicationContext context = http.getSharedObject(ApplicationContext.class);

		CorsFilter corsFilter = getCorsFilter(context);
		if (corsFilter == null) {
			throw new IllegalStateException(
					"Please configure either a " + CORS_FILTER_BEAN_NAME + " bean or a "
							+ CORS_CONFIGURATION_SOURCE_BEAN_NAME + "bean.");
		}
		http.addFilter(corsFilter);
	}
~~~

上面的配置HttpSecurity的过滤器链如下：

![](D:\company\document\images\springsecurity\7.png)
