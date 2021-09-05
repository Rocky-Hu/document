1. 在service类中注入自己本身，然后调用该类方法。

~~~java
@Service("userService")
public class UserServiceImpl implements UserService{
		
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserService userService;
	
	//默认情况下Spring的声明式事务对所有的运行时异常进行回滚
	@Transactional
	@Override
	public void addUser() {
		User user1 = new User();
		user1.setUserName("zhaoliu21");
		user1.setName("赵柳21");
		user1.setPassword("12323");
		user1.setSex(1);
		userMapper.insert(user1);
		userService.updateUser(user1);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void updateUser(User user) {	
		System.out.println("************updateUser  开始**************");
		User user1 = new User();
		user1.setId(46L);
		user1.setName("王武");
		userMapper.updateUserById(user1);
		System.out.println("************updateUser  结束**************");
	}
}
~~~

2.若是Springboot工程，则可以用注解开启cglib代理，开启exposeProxy=true，暴露代理对象

```
@EnableAspectJAutoProxy(exposeProxy=true)
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.bms.mapper")
public class Application {
	public static void main(String[] args) {
		ApplicationContext ac = SpringApplication.run(Application.class, args);
	}
}
```

或是用XML配置文件配置：

```
<aop:aspectj-autoproxy expose-proxy="true"/>
```

这样就可以在代码中调用了：

```
@Service("userService")
public class UserServiceImpl implements UserService{
		
	@Autowired
	private UserMapper userMapper;
	
	//默认情况下Spring的声明式事务对所有的运行时异常进行回滚
	@Transactional
	@Override
	public void addUser() {
		User user1 = new User();
		user1.setUserName("zhaoliu21");
		user1.setName("赵柳21");
		user1.setPassword("12323");
		user1.setSex(1);
		userMapper.insert(user1);
		((UserService)AopContext.currentProxy()).updateUser(user1);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void updateUser(User user) {	
		System.out.println("************updateUser  开始**************");
		User user1 = new User();
		user1.setId(46L);
		user1.setName("王武");
		userMapper.updateUserById(user1);
		System.out.println("************updateUser  结束**************");
	}
}
```

3.通过代码获取spring容器中的bean，然后调用：

```
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        assertApplicationContext();
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        assertApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> requiredType) {
        assertApplicationContext();
        return applicationContext.getBean(requiredType);
    }

    private static void assertApplicationContext() {
        if (SpringContextHolder.applicationContext == null) {
            throw new RuntimeException("applicaitonContext属性为null,请检查是否注入了SpringContextHolder!");
        }
    }

}
```

~~~
@Service("userService")
public class UserServiceImpl implements UserService{
		
	@Autowired
	private UserMapper userMapper;
	
	//默认情况下Spring的声明式事务对所有的运行时异常进行回滚
	@Transactional
	@Override
	public void addUser() {
		User user1 = new User();
		user1.setUserName("zhaoliu21");
		user1.setName("赵柳21");
		user1.setPassword("12323");
		user1.setSex(1);
		userMapper.insert(user1);
		SpringContextHolder.getBean(UserService.class).updateUser(user1);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void updateUser(User user) {	
		System.out.println("************updateUser  开始**************");
		User user1 = new User();
		user1.setId(46L);
		user1.setName("王武");
		userMapper.updateUserById(user1);
		System.out.println("************updateUser  结束**************");
	}
}
~~~

