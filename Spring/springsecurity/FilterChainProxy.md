# 配置

~~~
@Override
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


    @Override
    public void configure(WebSecurity web) throws Exception {
//        登录页面配置
        web.ignoring().mvcMatchers("get", "/*.jpg");
        web.ignoring().mvcMatchers("post", "/api/**");
        web.ignoring().mvcMatchers("post", "/api/**/**");
        web.ignoring().mvcMatchers("post", "/permissionApproval/**");
        web.ignoring().mvcMatchers("post", "/craftApproval/**");
        web.ignoring().mvcMatchers("post", "/equipmentX5/**");
        web.ignoring().mvcMatchers("post", "/x5request/**");
        web.ignoring().mvcMatchers("/error");
        web.ignoring().mvcMatchers("get", "/actuator");
        web.ignoring().mvcMatchers("get", "/actuator/*");
//        swagger配置
        web.ignoring().mvcMatchers("get", "/swagger-ui.html", "/**.js", "/**.css", "/webjars/**", "/swagger-resources/**", "/v2/api-docs/**");
    }
~~~

# filterChains初始化原理

org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration#springSecurityFilterChain

->

org.springframework.security.config.annotation.web.builders.WebSecurity#performBuild

![]()![2](D:\company\document\images\springsecurity\2.png)

1的内容为（第二段配置）：

![](D:\company\document\images\springsecurity\3.png)

2的内容（第一段配置）：

![](D:\company\document\images\springsecurity\4.png)

filterChains的内容就是上面两个的组合。

我们知道SecurityFilterChain包含有很多的Filter，那么上面第1段，HttpSecurity构造的SecurityFilterChain中包含有哪些Filter呢？

# HttpSecurity创建的SecurityFilterChain包含有哪些Filter

过滤器的初始化执行方法为org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder#init。

~~~
private void init() throws Exception {
	Collection<SecurityConfigurer<O, B>> configurers = getConfigurers();

	for (SecurityConfigurer<O, B> configurer : configurers) {
		configurer.init((B) this);
	}

	for (SecurityConfigurer<O, B> configurer : configurersAddedInInitializing) {
		configurer.init((B) this);
	}
}
~~~

![](D:\company\document\images\springsecurity\5.png)
