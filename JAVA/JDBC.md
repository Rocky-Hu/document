# 一、JDBC驱动连接可配置参数

https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html

## 1.1. 网络(Netwokring)参数

### 1.1.1. connectTimeout

> Timeout for socket connect (in milliseconds), with 0 being no timeout. Only works on JDK-1.4 or newer. Default to '0'.

### 1.1.2. socketTimeout

> Timeout (in milliseconds) on network socket operation (0, the default means no timeout).

socket operation包括哪些呢？

java.net.SocketOptions中有定义（SO_TIMEOUT参数描述）：

Set a timeout on blocking Socket operations:

- ServerSocket.accept()
- SocketInputStream.read()
- DatagramSocket.receive()

##  1.2. Statements参数

### 1.2.1. queryTimeoutKillsConnection

> If the timeout given in Statement.setQueryTimeout() expires, should the driver forcibly abort the Connection instead of attempting to abort the query?
>
> Default: false

## 1.3. 性能扩展（Performance Extensions）

### 1.3.1. enableQueryTimeouts

> When enabled, query timeouts set via Statement.setQueryTimeout() use a shared java.util.Timer instance for scheduling. Even if the timeout doesn't expire before the query is processed, there will be memory used by the TimerTask for the given timeout which won't be reclaimed until the time the timeout would have expired if it hadn't been cancelled by the driver. High-load environments might want to consider disabling this functionality.
>
> Default: true
>
> Since version: 5.0.6

# 二、java.sql.Connection

## 2.1. connectTimeout

数据库连接参数中可以指定connectTimeout参数，这个参数的作用是什么呢？有下面的一段测试：

~~~java
 @Test
 public void testConnectTimeout() throws Exception {
 		String connectString = "jdbc:mysql://localhost:3306/db_test?connectTimeout=1";
 		Connection connection = DriverManager.getConnection(connectString, "root", "root");
 }
~~~

执行结果：

~~~
The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
	at com.mysql.jdbc.Util.handleNewInstance(Util.java:425)
	at com.mysql.jdbc.SQLError.createCommunicationsException(SQLError.java:990)
	at com.mysql.jdbc.MysqlIO.<init>(MysqlIO.java:342)
	at com.mysql.jdbc.ConnectionImpl.coreConnect(ConnectionImpl.java:2197)
	at com.mysql.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:2230)
	at com.mysql.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:2025)
	at com.mysql.jdbc.ConnectionImpl.<init>(ConnectionImpl.java:778)
	at com.mysql.jdbc.JDBC4Connection.<init>(JDBC4Connection.java:47)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
	at com.mysql.jdbc.Util.handleNewInstance(Util.java:425)
	at com.mysql.jdbc.ConnectionImpl.getInstance(ConnectionImpl.java:386)
	at com.mysql.jdbc.NonRegisteringDriver.connect(NonRegisteringDriver.java:330)
	at java.sql.DriverManager.getConnection(DriverManager.java:664)
	at java.sql.DriverManager.getConnection(DriverManager.java:247)
	at org.newbie.javaer.jdbc.ConnectionExample.main(ConnectionExample.java:10)
Caused by: java.net.SocketTimeoutException
	at java.net.SocksSocketImpl.remainingMillis(SocksSocketImpl.java:111)
	at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392)
	at java.net.Socket.connect(Socket.java:589)
	at com.mysql.jdbc.StandardSocketFactory.connect(StandardSocketFactory.java:211)
	at com.mysql.jdbc.MysqlIO.<init>(MysqlIO.java:301)
	... 15 more
~~~

现在来看看connectTimeout参数是如何起作用的。

### 2.1.1. 读取连接字符串中的connectTimeout参数

~~~java
com.mysql.jdbc.StandardSocketFactory#connect

//------
String connectTimeoutStr = props.getProperty("connectTimeout");

int connectTimeout = 0;

if (connectTimeoutStr != null) {
    try {
        connectTimeout = Integer.parseInt(connectTimeoutStr);
    } catch (NumberFormatException nfe) {
        throw new SocketException("Illegal value '" + connectTimeoutStr + "' for connectTimeout");
    }
}
//------
~~~

### 2.1.2. 计算实际的connectTimeout

这个步骤的作用是什么呢？不是已经设置了connectTimeout吗，这个不是实际的吗？

要弄清楚这个，得先看DriverManager中的一个超时时间设置：

~~~java
private static volatile int loginTimeout = 0;
~~~

登录超时时间，这个属性的描述如下：

> Maximum time in seconds that a driver can wait when attempting to log in to a database.

客户端尝试连接数据库，可以认为成功登录数据库就算连接成功，这个动作可以有时间限制，比如说网络问题或者数据库服务器本身的问题，使得无法正常登录，那么客户端就可以设置一个超时时间，当超时时间到了，就取消登录动作，以免客户端在这种情况下长时间阻塞。

DriverManager的loginTimeout参数了解了，现在来看看MySQL JDBC驱动代码中的计算实际的timeout操作：

~~~java
// com.mysql.jdbc.StandardSocketFactory

/** The remaining login time in milliseconds. Initial value set from defined DriverManager.setLoginTimeout() */
protected int loginTimeoutCountdown = DriverManager.getLoginTimeout() * 1000;

/**
 * Validates the connection/socket timeout that must really be used.
 * 
 * @param expectedTimeout
 *            The timeout to validate.
 * @return The timeout to be used.
 */
protected int getRealTimeout(int expectedTimeout) {
    if (this.loginTimeoutCountdown > 0 && (expectedTimeout == 0 || expectedTimeout > this.loginTimeoutCountdown)) {
        return this.loginTimeoutCountdown;
    }
    return expectedTimeout;
}
~~~

这里的计算逻辑就是，设置了connectTimeout，还要看看DriverManager是否设置了loginTimeout。如果loginTimeout设置了，而connectTimeout比这个loginTimeout还要大的话，那么就以loginTimeout为主。其实就是一句话：实际的连接超时时间取connectTimeout和loginTimeout（大于0）中最小的。

### 2.1.3. 计算连接终止（deadline）时间和剩余终止（remaining deadline）时间

首先考虑一下，通过上面的两步操作得到了connectTimeout，这个时间的起点怎么定？客户端连接数据库服务器，表现在代码层面就是方法的执行，在这个角度上来看，调用连接方法我们就可以认为连接就开始了，所以这个connectTimeout的起点就是从执行连接方法起。

~~~java
java.net.SocksSocketImpl#connect

//------
final long deadlineMillis;

if (timeout == 0) {
    deadlineMillis = 0L;
} else {
    long finish = System.currentTimeMillis() + timeout;
    deadlineMillis = finish < 0 ? Long.MAX_VALUE : finish;
}
//------
~~~

连接终止时间=执行到当前方法的当前时间+connectTimeout。

那剩余终止时间又是什么呢？

可以这样说吧，调用java.net.SocksSocketImpl#connect方法还不算真正的发起请求，代码执行到super.connect(epoint, remainingMillis(deadlineMillis));这里才算是发起真正的连接请求，之所以提出剩余终止时间，就是把客户端代码的执行时间也算上去了，终止时间减去代码执行时间才是网络请求的超时时间。

剩余终止时间的计算如下：

~~~java
java.net.SocksSocketImpl#remainingMillis

//------
private static int remainingMillis(long deadlineMillis) throws IOException {
    if (deadlineMillis == 0L)
        return 0;

    final long remaining = deadlineMillis - System.currentTimeMillis();
    if (remaining > 0)
        return (int) remaining;

    throw new SocketTimeoutException();
}
//------
~~~

### 2.1.4. 应用connectTimeout

客户端连接数据库服务器，涉及到网络通信，最终使用的还是Socket。connectTimeout最终会成为Socket的超时时间属性。

> 有关Socket的超时时间知识请参阅其他资料。

~~~java
java.net.AbstractPlainSocketImpl#connect(java.net.SocketAddress, int)
->
java.net.AbstractPlainSocketImpl#connectToAddress
->
java.net.AbstractPlainSocketImpl#socketConnect
->
java.net.PlainSocketImpl#socketConnect
  
native void socketConnect(InetAddress address, int port, int timeout)
        throws IOException;
~~~

回到上面的那个例子，上面我们设置的connectionTimeout为1毫秒，出错点在：

~~~java
java.net.SocksSocketImpl.remainingMillis(SocksSocketImpl.java:111)
~~~

这里还没有使用Socket发送网络请求，执行到真正发送网络请求之前代码的执行时间就超过了1ms。

> MySQL服务器也有connect_timeout设置，和这里的不是一样的，一个是客户端设置一个是服务端设置。

### 2.1.5. 类比手写Socket

首先要确定一点，我们使用很少的代码来获取数据库连接Connection对象，然后通过这个对象来操作数据库，Connection的获取底层还是通过Socket来操作，上面的参数设置类似于下面代码的设置：

~~~java
Socket socket = new Socket();
try {
   // 设置connect timeout为2000毫秒
   socket.connect(new InetSocketAddress("www.xx.com", 8080), 2000);
} catch (IOException e) {
   e.printStackTrace();
}
~~~

连接超时抛出异常：java.net.ConnectException: Connection timed out: connect。

## 2.2. socketTimeout

### 2.2.1. socketTimeout的读取和设置

com.mysql.jdbc.ConnectionPropertiesImpl类中有socketTimeout属性：

~~~java
private IntegerConnectionProperty socketTimeout = new IntegerConnectionProperty("socketTimeout", 0, 0, Integer.MAX_VALUE,
            Messages.getString("ConnectionProperties.socketTimeout"), "3.0.1", CONNECTION_AND_AUTH_CATEGORY, 10);
~~~

连接字符串中的socketTimeout配置会最终设置到这个属性上，使用的方法是反射。

~~~java
com.mysql.jdbc.ConnectionPropertiesImpl#initializeProperties

//-----
for (int i = 0; i < numPropertiesToSet; i++) {
    java.lang.reflect.Field propertyField = PROPERTY_LIST.get(i);

    try {
        ConnectionProperty propToSet = (ConnectionProperty) propertyField.get(this);

        propToSet.initializeFrom(infoCopy, getExceptionInterceptor());
    } catch (IllegalAccessException iae) {
        throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unableToInitDriverProperties") + iae.toString(),
                SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
    }
}
//------
~~~

### 2.2.2. socketTimeout的应用

这个流程比较复杂，截取比较重要的片段：

~~~java
->com.mysql.jdbc.ConnectionImpl#coreConnect
this.io = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), getProxy(), getSocketTimeout(),
                this.largeRowSizeThreshold.getValueAsInt());
~~~

这里有getSocketTimeout()的获取，com.mysql.jdbc.ConnectionImpl是com.mysql.jdbc.ConnectionPropertiesImpl的子类，上面已经分析了com.mysql.jdbc.ConnectionPropertiesImpl有socketTimeout的属性，并且这个属性的值就是连接字符串中的socketTimeout，所以这里getSocketTimeout获取的就是连接字符串中的参数值。接着：

~~~java
-> com.mysql.jdbc.MysqlIO#MysqlIO

//------
if (socketTimeout != 0) {
    try {
        this.mysqlConnection.setSoTimeout(socketTimeout);
    } catch (Exception ex) {
        /* Ignore if the platform does not support it */
    }
}
//------
~~~

这个方法的最终设置是：

~~~java
java.net.PlainSocketImpl#socketSetOption0

native void socketSetOption0(int cmd, boolean on, Object value)
        throws SocketException;
~~~

这里cmd的值为java.net.SocketOptions#SO_TIMEOUT，value值为socketTimeout的值。

~~~java
/** Set a timeout on blocking Socket operations:
* <PRE>
* ServerSocket.accept();
* SocketInputStream.read();
* DatagramSocket.receive();
* </PRE>
*
* <P> The option must be set prior to entering a blocking
* operation to take effect.  If the timeout expires and the
* operation would continue to block,
* <B>java.io.InterruptedIOException</B> is raised.  The Socket is
* not closed in this case.
*
* <P> Valid for all sockets: SocketImpl, DatagramSocketImpl
*
* @see Socket#setSoTimeout
* @see ServerSocket#setSoTimeout
* @see DatagramSocket#setSoTimeout
*/
@Native public final static int SO_TIMEOUT = 0x1006;
~~~

也就是说socketTimeout最终应用到了Socket的SO_TIMETOUT参数中。

Socket中这个参数的作用是什么呢？

~~~java
Enable/disable SO_TIMEOUT with the specified timeout, in milliseconds. With this option set to a non-zero timeout,
a read() call on the InputStream associated with this Socket will block for only this amount of time. If the timeout expires, 
a java.net.SocketTimeoutException is raised, though the Socket is still valid. The option must be enabled prior to entering the blocking operation to have effect.
The timeout must be > 0. A timeout of zero is interpreted as an infinite timeout.
~~~

通俗解释：

设置socket调用InputStream读数据的超时时间，以毫秒为单位，如果超过这个时候，会抛出java.net.SocketTimeoutException。

当输入流的read方法被阻塞时，如果设置timeout（timeout的单位是毫秒），那么系统在等待了timeout毫秒后会抛出一个InterruptedIOException例外。在抛出例外后，输入流并未关闭，你可以继续通过read方法读取数据。

当底层的Socket实现不支持SO_TIMEOUT选项时，这两个方法将抛出SocketException例外。不能将timeout设为负数，否则setSoTimeout方法将抛出IllegalArgumentException例外。

### 2.2.3. 类比手写Socket

~~~java
try {
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress("localhost", 8080));
    // 设置so timeout 为2000毫秒
    socket.setSoTimeout(2000);
    InputStream in = socket.getInputStream();
    in.read();
} catch (IOException e) {
    e.printStackTrace();
} 
~~~

读取超时抛出异常：java.net.SocketTimeoutException: Read timed out。

## 2.3. network timeout

java.sql.Connection接口中有以下两个方法：

~~~java
void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException;
int getNetworkTimeout() throws SQLException;
~~~

### 2.3.1. getNetworkTimeout

直接看源码：

~~~java
com.mysql.jdbc.ConnectionImpl#getNetworkTimeout

public int getNetworkTimeout() throws SQLException {
    synchronized (getConnectionMutex()) {
        checkClosed();
        return getSocketTimeout();
    }
}

com.mysql.jdbc.ConnectionPropertiesImpl#getSocketTimeout
  
public int getSocketTimeout() {
  return this.socketTimeout.getValueAsInt();
}
~~~

是不是很熟悉了，就是获取的socketTimeout，也就是连接字符串中的socketTimeout设置。

~~~java
public class ConnectionExample {

    public static void main(String[] args) throws Exception {
        String connectionString = "jdbc:mysql://localhost:3306/db_test";

        Connection connection = DriverManager.getConnection(connectionString, "root", "root");
        System.out.println(connection.getNetworkTimeout());
    }

}
~~~

输出结果：

~~~
0
~~~

修改一下，将connectionString加上socketTimeout参数：

~~~
 String connectionString = "jdbc:mysql://localhost:3306/db_test?socketTimeout=2000";
~~~

输出结果：

~~~
2000
~~~

### 2.3.2. setNetworkTimeout

直接看源码：

~~~java
****com.mysql.jdbc.ConnectionImpl#setNetworkTimeout****

public void setNetworkTimeout(Executor executor, final int milliseconds) throws SQLException {
    synchronized (getConnectionMutex()) {
        SecurityManager sec = System.getSecurityManager();

        if (sec != null) {
            sec.checkPermission(SET_NETWORK_TIMEOUT_PERM);
        }

        if (executor == null) {
            throw SQLError.createSQLException("Executor can not be null", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }

        checkClosed();
        executor.execute(new NetworkTimeoutSetter(this, this.io, milliseconds));
    }
}

****com.mysql.jdbc.ConnectionImpl.NetworkTimeoutSetter#run****

public void run() {
    try {
        ConnectionImpl conn = this.connImplRef.get();
        if (conn != null) {
            synchronized (conn.getConnectionMutex()) {
                conn.setSocketTimeout(this.milliseconds); // for re-connects
                MysqlIO io = this.mysqlIoRef.get();
                if (io != null) {
                    io.setSocketTimeout(this.milliseconds);
                }
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}
~~~

最终设置的还是socketTimeout。

## 2.4. read only

java.sql.Connection接口有下面的方法：

~~~java
void setReadOnly(boolean readOnly) throws SQLException;
~~~

该方法的作用为：

> Puts this connection in read-only mode as a hint to the driver to enable database optimizations.

作用是设置为只读模式，告诉驱动启用数据库优化。

上面的说明还不够具体，通过具体的驱动实现类来看看设置的作用。

com.mysql.jdbc.ConnectionImpl#setReadOnly

~~~java
public void setReadOnly(boolean readOnlyFlag) throws SQLException {
    checkClosed();

    setReadOnlyInternal(readOnlyFlag);
}
~~~

com.mysql.jdbc.ConnectionImpl#setReadOnlyInternal

~~~java
public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
    // note this this is safe even inside a transaction
    if (getReadOnlyPropagatesToServer() && versionMeetsMinimum(5, 6, 5)) {
        if (!getUseLocalSessionState() || (readOnlyFlag != this.readOnly)) {
            execSQL(null, "set session transaction " + (readOnlyFlag ? "read only" : "read write"), -1, null, DEFAULT_RESULT_SET_TYPE,
                    DEFAULT_RESULT_SET_CONCURRENCY, false, this.database, null, false);
        }
    }

    this.readOnly = readOnlyFlag;
}
~~~

MySQL JDBC驱动的实现中，当设置为read only模式，会执行下面的命令：

~~~sql
set session transaction read only
~~~

将当前会话的事务设置为只读模式。只读事务有什么作用呢？在MySQL的官方文档中有说明，请查阅。

## 3.1. query timeout

java.sql.Statement方法中有setQueryTimeout方法来设置query超时时间。API对于这个方法的作用如下：

> Sets the number of seconds the driver will wait for a Statement object execute to the given number of seconds. 
>
> By default there is no limit on the amout of time allowed for a running statement to complete. If the limit is exceeded, an SQLTimeoutException is thrown.
>
> A JDBC driver must apply this limit to execute, executeQuery and executeUpdate methods.

### 3.1.1. CancelTask

query timeout的控制是在JDBC Driver中实现的，客户端代码中实现了超时逻辑。在执行sql语句之前，会创建一个timeout task，这个任务是个延迟执行的异步任务，负责在Statement执行超时时执行逻辑，delay的数值就是query timeout。

#### CancelTask执行逻辑

timeout task任务到底执行的是什么呢？一个Statement执行超时后，该采取什么样的行动？CancelTask为timeout task的实现，当执行超时，会根据配置执行如下的动作：

- 连接字符串中设置了queryTimeoutKillsConnection=true，则向数据库服务器发送KILL CONNECTION指令。
- queryTimeoutKillsConnection默认为false，执行的动作是向服务器KILL QUEREY指令。

#### CancelTask取消

CancelTask是个延迟执行的任务，在执行sql语句之前创建，如果Statement执行完毕在延迟时间之前，那么就会取消掉这个任务，这个任务是不会启动的，反之任务就会启动。

### 3.1.2. enableQueryTimeouts

连接字符串中关于query timeout有一个参数enableQueryTimeouts。默认值为true。enableQueryTimeouts读取后会通过反射设置到com.mysql.jdbc.ConnectionPropertiesImpl#enableQueryTimeouts属性中，它的作用是控制是否执行CancelTask。

~~~java
com.mysql.jdbc.PreparedStatement#executeInternal

if (locallyScopedConnection.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && locallyScopedConnection.versionMeetsMinimum(5, 0, 0)) {
                        timeoutTask = new CancelTask(this);
                        locallyScopedConnection.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
                    }
~~~

### 3.1.3. 超时控制执行原理图

![](..\images\Statement超时控制原理.png)

# 四、java.sql.ResultSet

























