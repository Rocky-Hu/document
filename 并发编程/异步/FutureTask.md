# 一、FutureTask解析

## 1.1. 类说明

![](../../images/concurrent/FutureTask.png)

## 1.2. 任务状态

![](../../images/FutureTask状态.png)

可能的状态转移：

~~~
NEW -> COMPLETING -> NORMAL
NEW -> COMPLETING -> EXCEPTIONAL
NEW -> CANCELLED
NEW -> INTERRUPTING -> INTERRUPTED
~~~

## 1.3. 执行流程

![](../../images/concurrent/FutureTask_执行流程.png)