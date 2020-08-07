# 一、Class方法解析

## 1.1. isAssignableFrom

方法声明：

~~~java
public native boolean isAssignableFrom(Class<?> cls);
~~~

方法释义：

> Determines if the class or interface represented by this Class object is either the same as, or is a superclass or superinterface of, the class or interface represented by the specified Class parater. If returns ture if so, otherwise it returns false. If this Class object represents a primitive type, this method returns ture if the specified Class paramter is exactly this Class object; otherwise it returns false.

判断当前Class对象（调用这个方法的Class对象）所代表的类或接口和参数Class对象所代表的的类或接口是否是相同的或者是有父子关系（当前Class对象为父，参数Class对象为子）。

