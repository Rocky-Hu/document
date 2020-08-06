面向对象编程中类是一等公民，而面向切面编程中切面是一等公民。切面是功能的模块化。

# 一、AOP概念

![](..\images\AOP概念.jpeg)

## 1.1. 切面（Aspect）

跨多个类的关注点的模块化。事务管理是企业Java应用程序中横切关注的一个很好的例子。

## 1.2. 连接点（Join Point）

程序执行过程中的操作点，例如方法的执行或异常的处理。在Spring AOP中，连接点始终代表方法的执行。

## 1.3. 通知（Advice）

切面在特定连接点上采取的操作。不同类型的建议包括“around”、“before”和"after"的建议。许多AOP框架，包括Spring，将通知建模为拦截器，并维护围绕连接点的拦截器链。

> Spring解释：action to take at a joinpoint

## 1.4. 切入点（Pointcut）

匹配连接点的谓词。通知与切入点表达式相关联，并在与切入点匹配的任何连接点上运行(例如，执行具有特定名称的方法)。连接点与切入点表达式匹配的概念是AOP的核心，Spring默认使用AspectJ切入点表达式语言。

## 1.5. 引入（Introduction）

代表类型声明其他方法或字段。Spring AOP允许您将新的接口(和相应的实现)引入任何被建议的对象。例如，您可以使用一个introduction来让一个bean实现一个IsModified接口，以简化缓存。

## 1.6. 目标对象（Target object）

被一个或多个切面通知的对象。也称为“被通知对象”。因为Spring AOP是通过使用运行时代理来实现的，所以这个对象总是一个代理对象。

## 1.7. AOP代理（AOP proxy）

为了实现切面契约(通知方法执行等)而由AOP框架创建的对象。在Spring框架中，AOP代理是JDK动态代理或CGLIB代理。

##  1.8. 织入（Weaving）

将切面与其他应用程序类型或对象链接以创建通知的对象。这可以在编译时(例如，使用AspectJ编译器)、加载时或运行时完成。与其他纯Java AOP框架一样，Spring AOP在运行时执行编织。

# 二、Spring AOP应用

## 2.1. AOP代理

Spring默认使用JDK代理。这样任何实现接口的类都可以被代理。同时，Spring也可以使用CGLIB代理，当一个业务对象没有实现任何接口的时候就使用CGLIB代理。也可以强制使用CGLIB代理。

![](..\images\Spring AOP代理.png)

###  **查看JDK生成的代理类**

~~~java
-Dsun.misc.ProxyGenerator.saveGeneratedFiles=true
或 
System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
~~~

### **查看CGLIB生成的代理类**

~~~java
System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\class");  
~~~

## 2.2. AspectJ支持

可以用@Aspect注解将一个普通的Java类定义为切面。Spring使用AspectJ提供了的类库来解析和匹配与AspectJ一样的注解。但是，注意的是在AOP运行时还是使用的是Spring AOP，而不需要依赖于AspectJ的编译器和解析器。

### 2.2.1. 启用AspectJ支持

启用AspectJ就是启用某些组件以识别AspectJ风格的注解配置。

#### 2.2.1.1. 基于Java Configuration

~~~java
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {
}
~~~

#### 2.2.1.2. 基于XML配置

~~~xml
<aop:aspectj-autoproxy/>
~~~

### 2.2.2. 定义Aspect（切面）

通过启用了AsjectJ的支持。将@Aspect注释的类交给Spring托管，以被Spring识别到用于配置Spring AOP。

定义一个切面：

~~~java
@Aspect
public class NotVeryUsefulAspect {
}
~~~

将@Aspect定义的类托管到Spring。

注解方式：

~~~java
@Component
@Aspect
public class NotVeryUsefulAspect {
}
~~~

xml配置方式：

~~~xml
<bean id="myAspect" class="org.xyz.NotVeryUsefulAspect">
  <!-- configure properties of the aspect here -->
</bean>
~~~

> 注意：只有@Aspect标记的类是不会被Spring自动扫描到的，需要像其他组件一样明确地交于Spring托管。

### 2.2.3. 定义Pointcut

Pointcut决定了感兴趣的Join Point。Spring只支持方法执行连接点。所以可以认为Pointcut匹配方法的执行。

Pointcut声明有两部分：

- **签名**：由名称和任何参数组成
- **切入点表达式**：确切地确定我们感兴趣的方法

在AspectJ风格的AOP中：

- 签名：普通的方法定义
- 切入点表达式：通过使用@Pointcut注释来表示切入点表达式

示例：

~~~java
@Pointcut("execution(* transfer(..))") // the pointcut expression
private void anyOldTransfer() {} // the pointcut signature
~~~

#### 2.2.3.1. 支持的Pointcut指示符

Spring AOP支持在切入点表达式中使用以下AspectJ切入点指示符(PCD)：

##### 2.2.3.1.1. execution

###### 描述

Spring AOP连接点简单的来说就是方法的执行。而描述方法的元素有：可见性修饰符、返回值类型、声明方法的类、方法名、方法参数和抛出的异常类型。这个指示符就详细的描述这些元素的匹配情况。这是使用Spring AOP首选的Pointcut指示符。

###### 语法

~~~java
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern)
                throws-pattern?)
~~~

> 这里问号表示当前项可以有也可以没有

其中各项的语义如下：

- modifiers-pattern：方法的可见性，如public，protected；
- ret-type-pattern：方法的返回值类型，如int，void等；
- declaring-type-pattern：方法所在类的全路径名，如com.spring.Aspect；
- name-pattern：方法名，如buisinessService()；
- param-pattern：方法的参数，如java.lang.String；
- throws-pattern：方法抛出的异常类型，如java.lang.Exception；

###### 示例

- 执行任意的public方法

  ~~~java
   execution(public * *(..))
  ~~~

- 执行任意以set开头的方法

  ~~~java
   execution(* set*(..))
  ~~~

- 执行AccountService接口下的任意方法

  ~~~java
  execution(* com.xyz.service.AccountService.*(..))
  ~~~

- 执行service包下的任意类的任意方法

  ~~~java
  execution(* com.xyz.service.*.*(..))
  ~~~

- 执行service包及其子包下的任意方法

  ~~~java
  execution(* com.xyz.service..*.*(..))
  ~~~

##### 2.2.3.1.2. within

###### 描述

within的中文翻译为：在...之内，在...里面。这个指示符的含义就是匹配在within表达式指定的范围的连接点（方法）。

###### 语法

within表达式的粒度为类，其参数为全路径的类名（可使用通配符），表示匹配当前表达式的所有类都将被当前方法环绕。如下是within表达式的语法：

```java
within(declaring-type-pattern)
```

###### 示例

如下示例表示匹配com.spring.service.BusinessObject中的所有方法：

```java
within(com.spring.service.BusinessObject)
```

within表达式路径和类名都可以使用通配符进行匹配，比如如下表达式将匹配com.spring.service包下的所有类，不包括子包中的类：

```java
within(com.spring.service.*)
```

如下表达式表示匹配com.spring.service包及子包下的所有类：

~~~java
within(com.spring.service..*)
~~~

##### 2.2.3.1.3. this

###### 描述

Spring AOP中连接点即可认为是方法的执行，而方法的宿主是对象。Spring AOP是基于代理的，分为代理对象和目标对象。this指示符指示代理对象是指定的类型的连接点（方法）被匹配。

> Spring AOP是基于代理的，this和target有区别：this指的是代理对象，而target指的是目标对象（被代理的对象）。由于Spring AOP基于代理的这种特性，目标对象内部的方法调用是不会被拦截的。对于JDK代理，代理上调用public方法才会被拦截。对于CGLIB代理，调用public和protected方法会被拦截。

###### 语法

~~~java
this(declaring-type-pattern)
~~~

###### 示例

- 实现了AccountService接口的代理对象内的任意方法

  ~~~java
   this(com.xyz.service.AccountService)
  ~~~

##### 2.2.3.1.4. target

###### 描述

target指示符指示目标对象是指定类型的连接点（方法）被匹配。

###### 语法

~~~java
target(declaring-type-pattern)
~~~

###### 示例

- 实现了AccountService接口的目标对象内的任意方法

  ~~~java
  target(com.xyz.service.AccountService)
  ~~~

##### 2.2.3.1.5. args

###### 描述

表示具有指定参数签名的连接点（方法）被匹配。

###### 语法

args表达式的作用是匹配指定参数类型和指定参数数量的方法，无论其类路径或者是方法名是什么。这里需要注意的是，args指定的参数必须是全路径的。如下是args表达式的语法：

~~~java
args(param-pattern)
~~~

###### 示例

如下示例表示匹配所有只有一个参数，并且参数类型是java.lang.String类型的方法：

```java
args(java.lang.String)
```

也可以使用通配符，但这里通配符只能使用..，而不能使用*。如下是使用通配符的实例，该切点表达式将匹配第一个参数为java.lang.String，最后一个参数为java.lang.Integer，并且中间可以有任意个数和类型参数的方法：

~~~java
args(java.lang.String,..,java.lang.Integer)
~~~

##### 2.2.3.1.6. @target

###### 描述

指示正在执行的目标对象的类具有指定的注解类型。

###### 语法

~~~java
@target(declaring-type-pattern)
~~~

###### 示例

- 正在执行的目标对象的类具有Transactional注解

  ~~~java
   @target(org.springframework.transaction.annotation.Transactional)
  ~~~

##### 2.2.3.1.7. @args

###### 描述

指示方法运行时参数为指定的注解类型。

###### 语法

~~~java
@args(param-pattern)
~~~

###### 示例

- 方法有一个参数，并且运行时传递的类型为@Classified

  ~~~java
  @args(com.xyz.security.Classified)
  ~~~

##### 2.2.3.1.8. @within

###### 描述

指示类具有指定的注解类型。

###### 语法

~~~
@within(declaring-type-pattern)
~~~

###### 示例

- 目标对象的声明类有@Transactional注解

  ~~~java
   @within(org.springframework.transaction.annotation.Transactional)
  ~~~

##### 2.2.3.1.9. @annotation

###### 描述

指示方法具有指定的注解。

###### 语法

~~~
@annotation(declaring-type-pattern)
~~~

###### 示例

- 正在执行的方法上有@Transactional注解

  ~~~java
  @annotation(org.springframework.transaction.annotation.Transactional)
  ~~~

#### 2.2.3.2. 组合Pointcut表达式

可用通过使用&&、||和!来组合Pointcut表达式，甚至可以通过名称来引用Pointcut表达式。

~~~java
@Pointcut("execution(public * *(..))")
private void anyPublicOperation() {} ①
  
@Pointcut("within(com.xyz.someapp.trading..*)")
private void inTrading() {} ②
  
@Pointcut("anyPublicOperation() && inTrading()")
private void tradingOperation() {} ③
~~~

#### 2.3.3.3. 共享通用的Pointcut表达式

~~~java
@Aspect
public class SystemArchitecture {
  
  /**
  * A join point is in the web layer if the method is defined
  * in a type in the com.xyz.someapp.web package or any sub-package
  * under that.
  */
	@Pointcut("within(com.xyz.someapp.web..*)")
  public void inWebLayer() {}
  
  /**
  * A join point is in the service layer if the method is defined
  * in a type in the com.xyz.someapp.service package or any sub-package
  * under that.
  */
  @Pointcut("within(com.xyz.someapp.service..*)")
  public void inServiceLayer() {}
  
  /**
  * A join point is in the data access layer if the method is defined
  * in a type in the com.xyz.someapp.dao package or any sub-package
  * under that.
  */
  @Pointcut("within(com.xyz.someapp.dao..*)")
  public void inDataAccessLayer() {}
  
  /**
  * A business service is the execution of any method defined on a service
  * interface. This definition assumes that interfaces are placed in the
  * "service" package, and that implementation types are in sub-packages.
  *
  * If you group service interfaces by functional area (for example,
  * in packages com.xyz.someapp.abc.service and com.xyz.someapp.def.service) then
  * the pointcut expression "execution(* com.xyz.someapp..service.*.*(..))"
  * could be used instead.
  *
  * Alternatively, you can write the expression using the 'bean'
  * PCD, like so "bean(*Service)". (This assumes that you have
  * named your Spring service beans in a consistent fashion.)
  */
  @Pointcut("execution(* com.xyz.someapp..service.*.*(..))")
  public void businessService() {}
  
  /**
  * A data access operation is the execution of any method defined on a
  * dao interface. This definition assumes that interfaces are placed in the
  * "dao" package, and that implementation types are in sub-packages.
  */
  @Pointcut("execution(* com.xyz.someapp.dao.*.*(..))")
  public void dataAccessOperation() {}
  
}
~~~

引用：

~~~xml
<aop:config>
  <aop:advisor
  pointcut="com.xyz.someapp.SystemArchitecture.businessService()"
  advice-ref="tx-advice"/>
</aop:config>
<tx:advice id="tx-advice">
  <tx:attributes>
  <tx:method name="*" propagation="REQUIRED"/>
  </tx:attributes>
</tx:advice>
~~~

### 2.2.4. 定义Advice

通知与切入点表达式关联，并在切入点匹配的方法执行之前、之后或环绕运行。切入点表达式可以是对命名切入点的简单引用，也可以是在适当位置声明的切入点表达式。

#### 2.2.4.1. Before Advice

使用@Before注解。

~~~java
@Aspect
public class BeforeExample {
  
  @Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
  public void doAccessCheck() {
  // ...
  }
  
}
~~~

如果使用in-place切入点表达式，上面的例子重写为：

~~~java
@Aspect
public class BeforeExample {
  
  @Before("execution(* com.xyz.myapp.dao.*.*(..))")
  public void doAccessCheck() {
  	// ...
  }
  
}
~~~

#### 2.2.4.2. After Returning Advice

After Returning Advice在当匹配的方法执行正常返回时运行。可以使用@AfterReturning注解来声明。

~~~java
@Aspect
public class AfterReturningExample {
  
  @AfterReturning("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
  public void doAccessCheck() {
  	// ...
  }
  
}
~~~

有时候，您需要在通知正文中访问返回的实际值。您可以使用绑定返回值的@AfterReturning形式来获得访问，如下面的示例所示：

~~~java
@Aspect
public class AfterReturningExample {
  
  @AfterReturning( pointcut="com.xyz.myapp.SystemArchitecture.dataAccessOperation()",
  			returning="retVal")
  public void doAccessCheck(Object retVal) {
  	// ...
  }
  
}
~~~

返回属性中使用的名称必须与通知方法中的参数名称对应。当一个方法执行返回时，返回值作为相应的参数值传递给通知方法。return子句还将匹配仅限于那些返回指定类型值的方法执行(在本例中为Object，它匹配任何返回值)。

#### 2.2.4.3. After Throwing Advice

After Throwing Advice在当匹配的方法执行通过抛出异常退出时运行。你可以使用@AfterThrowing注解来声明它，如下面的例子所示：

~~~java
@Aspect
public class AfterThrowingExample {
  
  @AfterThrowing("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
  public void doRecoveryActions() {
 		// ...
  }
  
}
~~~

通常，您希望仅在抛出给定类型的异常时才运行通知，而且您还经常需要访问通知主体中抛出的异常。可以使用抛出属性来限制匹配(如果需要，可以使用Throwable作为异常类型)，并将抛出的异常绑定到一个通知参数。下面的例子展示了如何做到这一点：

~~~java
@Aspect
public class AfterThrowingExample {
  
  @AfterThrowing( pointcut="com.xyz.myapp.SystemArchitecture.dataAccessOperation()", throwing="ex")
  public void doRecoveryActions(DataAccessException ex) {
  	// ...
  }
  
}
~~~

抛出属性中使用的名称必须与通知方法中的参数名称对应。当方法执行通过抛出异常而退出时，异常将作为相应的参数值传递给通知方法。抛出子句还限制只匹配抛出指定类型异常(在上面的例子中异常的类型是DataAccessException)。

#### 2.2.4.4. After (Finally) Advice

当匹配的方法执行退出时，通知运行。它是使用@After注释声明的。After advice必须准备好处理正常和异常返回条件。它通常用于释放资源和类似的目的。下面的例子展示了如何使用after finally advice：

~~~java
@Aspect
public class AfterFinallyExample {
  
  @After("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
  public void doReleaseLock() {
  	// ...
  }
  
}
~~~

#### 2.2.4.5. Around Advice

Around通知是使用@Around注释声明的。通知的第一个参数方法的类型必须为ProceedingJoinPoint。在通知的主体中，调用
ProceedingJoinPoint的proceed()来触发底层方法执行。proceed方法也可以传入一个Object[]。数组中的值用作方法执行时的需求参数。

~~~java
@Aspect
public class AroundExample {
  
  @Around("com.xyz.myapp.SystemArchitecture.businessService()")
  public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
  	// start stopwatch
  	Object retVal = pjp.proceed();
  	// stop stopwatch
  	return retVal;
  }
  
}
~~~

proceed()的返回值就是被调用方法的返回值。proceed()方法可以调用一次、多次或者不调用。

#### 2.2.4.6. Advice参数

##### 2.2.4.6.1. 访问当前的JointPoint

advice方法可能定义参数，第一个参数类型是org.aspectj.lang.JointPoint。JointPoint提供了一些有用的方法：

- getArgs()：返回方法参数；
- getThis()：返回代理对象；
- getTarget()：返回目标对象；
- getSignature()：返回被通知的方法的签名
- toString()：打印被通知的方法的有作用的描述

##### 2.2.4.6.2. 传递参数给Advice

使用args绑定形式。

~~~java
@Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation() && args(account,..)")
public void validateAccount(Account account) {
  // ...
}
~~~

也可以这样：

~~~java
@Pointcut("com.xyz.myapp.SystemArchitecture.dataAccessOperation() &&args(account,..)")
private void accountDataAccessOperation(Account account) {}

@Before("accountDataAccessOperation(account)")
public void validateAccount(Account account) {
  // ...
}
~~~

##### 2.2.4.6.3 Advice排序

当不同切面的advice都想在相同的join point上运行时，除非你指定，不然执行的顺序是不明确地。你可以通过指定优先级来控制顺序。切面类通过实现org.springframework.core.Ordered接口或者用@Order注解来指定优先级。给定两个切面类，Ordered.getValue()返回值小的切面具有高优先级。

当同一个切面的两个advice需要运行在相同的join point，这个顺序是未知的。

### 2.2.5. Introductions

使被通知的对象实现一个新的接口，并提供该接口的实现。这种方法是通过让被通知的对象实现新接口的方式来提供功能的增强。

通过使用 @DeclareParents注解来定义引入增强。

~~~java
@Aspect
public class UsageTracking {
  @DeclareParents(value="com.xzy.myapp.service.*+", defaultImpl=DefaultUsageTracked.class)
  public static UsageTracked mixin;
    
  @Before("com.xyz.myapp.SystemArchitecture.businessService() && this(usageTracked)")
  public void recordUsage(UsageTracked usageTracked) {
  	usageTracked.incrementUseCount();
  }
    
}
~~~

## 2.3. 基于Schema的AOP支持

### 2.3.1. 声明Aspect

~~~xml
<aop:config>
  <aop:aspect id="myAspect" ref="aBean">
  ...
  </aop:aspect>
</aop:config>

<bean id="aBean" class="...">
  ...
</bean>
~~~

### 2.3.2. 声明Pointcut

~~~xml
<aop:config>
  <aop:pointcut id="businessService" expression="execution(* com.xyz.myapp.service.*.*(..))"/>
</aop:config>
~~~

#### 2.3.2.1. 传参

~~~xml
<aop:config>
  <aop:aspect id="myAspect" ref="aBean">
      
  	<aop:pointcut id="businessService" expression="execution(* com.xyz.myapp.service.*.*(..)) && this(service)"/>
      
  	<aop:before pointcut-ref="businessService" method="monitor"/>
  ...
  </aop:aspect>
    
</aop:config>
~~~

#### 2.3.2.2. 组合

使用关键字and、or和not。

~~~xml
<aop:config>
  <aop:aspect id="myAspect" ref="aBean">
      
  	<aop:pointcut id="businessService" expression="execution(* com.xyz.myapp.service.*.*(..)) and
this(service)"/>
  	<aop:before pointcut-ref="businessService" method="monitor"/>
  ...
  </aop:aspect>
</aop:config>
~~~

### 2.3.3. 声明Advice

#### Before Advice

~~~xml
<aop:aspect id="beforeExample" ref="aBean">
  <aop:before pointcut-ref="dataAccessOperation" method="doAccessCheck"/>
  ...
</aop:aspect>
~~~

~~~xml
<aop:aspect id="beforeExample" ref="aBean">
  <aop:before pointcut="execution(* com.xyz.myapp.dao.*.*(..))" method="doAccessCheck"/>
  ...
</aop:aspect>
~~~

#### After Returning Advice

~~~xml
<aop:aspect id="afterReturningExample" ref="aBean">
  <aop:after-returning pointcut-ref="dataAccessOperation" method="doAccessCheck"/>
  ...
</aop:aspect>
~~~

~~~xml
<aop:aspect id="afterReturningExample" ref="aBean">
  <aop:after-returning pointcut-ref="dataAccessOperation" returning="retVal" method="doAccessCheck"/>
  ...
</aop:aspect>
~~~

#### After Throwing Advice

~~~xml
<aop:aspect id="afterThrowingExample" ref="aBean">
  <aop:after-throwing pointcut-ref="dataAccessOperation" method="doRecoveryActions"/>
  ...
</aop:aspect>
~~~

~~~xml
<aop:aspect id="afterThrowingExample" ref="aBean">
  <aop:after-throwing pointcut-ref="dataAccessOperation" throwing="dataAccessEx" method="doRecoveryActions"/>
  ...
</aop:aspect>
~~~

#### After (Finally) Advice

~~~xml
<aop:aspect id="afterFinallyExample" ref="aBean">
  <aop:after pointcut-ref="dataAccessOperation" method="doReleaseLock"/>
  ...
</aop:aspect>
~~~

#### Around Advice

~~~xml
<aop:aspect id="aroundExample" ref="aBean">
  <aop:around pointcut-ref="businessService" method="doBasicProfiling"/>
  ...
</aop:aspect>
~~~

~~~java
public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
  // start stopwatch
  Object retVal = pjp.proceed();
  // stop stopwatch
  return retVal;
}
~~~

#### Advice Parameters

~~~xml
<aop:before
  pointcut="com.xyz.lib.Pointcuts.anyPublicMethod() and @annotation(auditable)"
  method="audit"
  arg-names="auditable"/>
~~~

#### Advice Ordering

实现Ordered接口或使用@Order注解。

### 2.3.4.  Introductions

~~~xml
<aop:aspect id="usageTrackerAspect" ref="usageTracking">
  <aop:declare-parents
  types-matching="com.xzy.myapp.service.*+"
  implement-interface="com.xyz.myapp.service.tracking.UsageTracked"
  default-impl="com.xyz.myapp.service.tracking.DefaultUsageTracked"/>
  <aop:before
  pointcut="com.xyz.myapp.SystemArchitecture.businessService()
  and this(usageTracked)"
  method="recordUsage"/>
</aop:aspect>
~~~

### 2.3.5. Advisors

~~~xml
<aop:config>
  <aop:pointcut id="businessService"
  expression="execution(* com.xyz.myapp.service.*.*(..))"/>
  <aop:advisor
  pointcut-ref="businessService"
  advice-ref="tx-advice"/>
</aop:config>
<tx:advice id="tx-advice">
  <tx:attributes>
  <tx:method name="*" propagation="REQUIRED"/>
  </tx:attributes>
</tx:advice>
~~~

# 三、Spring AOP API

## 3.1. Pointcut

## 3.2. Advice 

![](../images/AOP Advice.jpeg)

## 3.3. Advisor

## 3.4. AopProxy

Spring AOP的代理对象是委托这个接口的实现生成的。这个接口有两个实现：基于JDK和基于CGLIB。

![](../images/AopProxy.png)

### 3.4.1. JdkDynamicAopProxy

![](../images/JdkDynamicAopProxy.png)

~~~java
@Override
public Object getProxy() {
    return getProxy(ClassUtils.getDefaultClassLoader());
}

@Override
public Object getProxy(@Nullable ClassLoader classLoader) {
    if (logger.isTraceEnabled()) {
        logger.trace("Creating JDK dynamic proxy: " + this.advised.getTargetSource());
    }
    Class<?>[] proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised, true);
    findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
    return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
}
~~~

熟悉的JDK动态代理方式，注意最后一个参数是this，这个参数是InvocationHandler类型的，目标对象执行，拦截逻辑的处理就是封装在InvocationHandler中的，也就是说JdkDynamicAopProxy的作用有：

1. 基于JDK代理动态代理生成目标对象的代理对象；
2. 作为一个InvocationHandler，处理代理拦截功能。

### 3.4.2. ObjenesisCglibAopProxy

> Spring 4后默认使用

![](../images/ObjenesisCglibAopProxy.png)

~~~java
protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
    enhancer.setInterceptDuringConstruction(false);
    enhancer.setCallbacks(callbacks);
    return (this.constructorArgs != null && this.constructorArgTypes != null ?
            enhancer.create(this.constructorArgTypes, this.constructorArgs) :
            enhancer.create());
}
~~~

## 3.5. AopProxyFactory

AopProxyFactory的作用是根据Spring AOP的配置来生成AopProxy对象。

~~~java
public interface AopProxyFactory {

	/**
	 * Create an {@link AopProxy} for the given AOP configuration.
	 * @param config the AOP configuration in the form of an
	 * AdvisedSupport object
	 * @return the corresponding AOP proxy
	 * @throws AopConfigException if the configuration is invalid
	 */
	AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException;

}
~~~

默认的实现为DefaultAopProxyFactory。主要的逻辑是根据AOP配置来决定创建JdkDynamicAopProxy或者ObjenesisCglibAopProxy。

~~~java
@Override
public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
    if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
        Class<?> targetClass = config.getTargetClass();
        if (targetClass == null) {
            throw new AopConfigException("TargetSource cannot determine target class: " +
                                         "Either an interface or a target is required for proxy creation.");
        }
        if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
            return new JdkDynamicAopProxy(config);
        }
        return new ObjenesisCglibAopProxy(config);
    }
    else {
        return new JdkDynamicAopProxy(config);
    }
}
~~~

## 3.6. 使用ProxyFactoryBean来创建AOP代理

### 3.6.1. 基础信息

![](../images/ProxyFactoryBean.png)

### 3.6.2. 属性

ProxyFactoryBean包含了创建AOP代理需要的一些配置属性：

- 指定想代理的目标
- 指定是否使用CGLIB

### 3.6.3. 基于JDK和CGLIB代理

目标对象的类未实现任何接口，那么创建的是CGLIB代理；即使proxyTargetClass被设置为false。

如果目标类实现了一个或多个接口，创建的代理类型则基于ProxyFactoryBean的配置。proxyTargetClass被设置为true，则创建CGLIB代理。

### 3.6.4. 内部创建代理流程

![](../images/ProxyFactoryBean方式创建代理流程.png)

## 3.7. 使用ProxyFactory以编程方式创建AOP代理

不依赖于Spring IOC容器创建代理。

~~~java
ProxyFactory factory = new ProxyFactory(myBusinessInterfaceImpl);
factory.addAdvice(myMethodInterceptor);
factory.addAdvisor(myAdvisor);
MyBusinessInterface tb = (MyBusinessInterface) factory.getProxy();
~~~

内部创建流程：

![](../images/ProxyFactory创建代理流程.png)

## 3.8. 操纵Advised对象

所有的AOP代理都可以被转换为org.springframework.aop.framework.Advised类型。Advised中包含了很多方法，可以通过这些方法来操纵AOP代理对象。

~~~java
Advisor[] getAdvisors();
void addAdvice(Advice advice) throws AopConfigException;
void addAdvice(int pos, Advice advice) throws AopConfigException;
void addAdvisor(Advisor advisor) throws AopConfigException;
void addAdvisor(int pos, Advisor advisor) throws AopConfigException;
int indexOf(Advisor advisor);
boolean removeAdvisor(Advisor advisor) throws AopConfigException;
void removeAdvisor(int index) throws AopConfigException;
boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException;
boolean isFrozen();
~~~

默认情况下，即使代理已经创建出来，也可以添加或者移除advisors或interceptors。

~~~java
Advised advised = (Advised) myObject;
Advisor[] advisors = advised.getAdvisors();
int oldAdvisorCount = advisors.length;
System.out.println(oldAdvisorCount + " advisors");
// Add an advice like an interceptor without a pointcut
// Will match all proxied methods
// Can use for interceptors, before, after returning or throws advice
advised.addAdvice(new DebugInterceptor());
// Add selective advice using a pointcut
advised.addAdvisor(new DefaultPointcutAdvisor(mySpecialPointcut, myAdvice));
assertEquals("Added two advisors", oldAdvisorCount + 2, advised.getAdvisors().length);
~~~

## 3.9. 自动代理（auto-proxy）能力

Spring使用内置"bean post processor"提供了自动代理的能力，不需要使用ProxyFactoryBean。

有两种方式来实施：

- 通过使用自动代理创建器引用当前上下文中的特定bean。
- 基于类级别的元信息

### 3.9.1. BeanNameAutoProxyCreator

规则：代理名称与字面值或通配符匹配的bean。

~~~xml
<bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
  <property name="beanNames" value="jdk*,onlyJdk"/>
  <property name="interceptorNames">
      <list>
      	<value>myInterceptor</value>
      </list>
  </property>
</bean>
~~~

### 3.9.2. DefaultAdvisorAutoProxyCreator

DefaultAdvisorAutoProxyCreator是一个更通用和非常强大的自动代理创建器。

使用这个自动创建其需要：

- 指定一个DefaultAdvisorAutoProxyCreator bean定义
- 在相同或相关联的上下文中指定一定数量的advisors。注意，这里只能是advisors，而不能是advices或者interceptors。因为要根据pointcut来选择需要被代理的bean。

~~~xml
<bean
class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"/>
<bean
class="org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor">
  <property name="transactionInterceptor" ref="transactionInterceptor"/>
</bean>
<bean id="customAdvisor" class="com.mycompany.MyAdvisor"/>
<bean id="businessObject1" class="com.mycompany.BusinessObject1">
  <!-- Properties omitted -->
</bean>
<bean id="businessObject2" class="com.mycompany.BusinessObject2"/>
~~~

DefaultAdvisorAutoProxyCreator会自动评估包含在advisor中的pointcut来确定业务对象的哪些方法可以被通知。

## 3.10. 使用TargetSource实现

# 四、Spring  AOP执行原理

## 4.1. 基于XML的配置

## 4.2. AspectJ风格配置

