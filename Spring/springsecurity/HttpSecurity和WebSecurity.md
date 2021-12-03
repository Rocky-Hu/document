# WebSecurity

继承WebSecurityConfigurerAdapter，然后做下面的配置：

~~~
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

这个代码的效果如下：

![](D:\company\document\images\springsecurity\1.png)

这里创建了很多SecurityFilterChain的实例。
