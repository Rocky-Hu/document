首先大家知道，当我们使用 Spring Security，用户登录成功之后，有两种方式获取用户登录信息：

1. SecurityContextHolder.getContext().getAuthentication()
2. 在 Controller 的方法中，加入 Authentication 参数

这两种办法，都可以获取到当前登录用户信息。

这两种方式获取到的数据都是来自 SecurityContextHolder，SecurityContextHolder 中的数据，本质上是保存在 ThreadLocal 中，ThreadLocal 的特点是存在它里边的数据，哪个线程存的，哪个线程才能访问到。

这样就带来一个问题，当用户登录成功之后，将用户用户数据存在 SecurityContextHolder 中（thread1），当下一个请求来的时候（thread2），想从 SecurityContextHolder 中获取用户登录信息，却发现获取不到！为啥？因为它俩不是同一个 Thread。

但实际上，正常情况下，我们使用 Spring Security 登录成功后，以后每次都能够获取到登录用户信息，这又是怎么回事呢？

这我们就要引入 Spring Security 中的SecurityContextPersistenceFilter 了。

http://blog.itpub.net/69923331/viewspace-2695120/

