# 一、从RejectedExecutionHandler中获取任务对象

The problem is when you submit task with submit method TreadPoolExecutor (actually AbstractExecutorService) wrap it to FutureTask. After that you receive FutureTask not your Runnable. You can call execute not submit:

```java
sExecutorService.execute(yourTask);
```

I don't think there is a way to get your task from FutureTask. You can only call run for it. So if you want to call submit and you need to call run - just do not convert to MyTask:

```java
FutureTask myTask = (FutureTask) runnable;
myTask.run();
Object result = myTask.get();
```

Other way if you want to access you MyTask object can be creating of MyFutureTask extends FutureTask, that will allow get your object:

```java
public MyFutureTask<V> extends FutureTask<V> {

    private Runnable myTask;

    public MyFutureTask(Runnable runnable, V result) {
        super(runnable, result);
        this.myTask = runnable;
    }

    public Runnable getMyTask() {
        return myTask;
    }        
}
```

Also you need to extends ThreadPoolExecutor and redefine newTaskFor method that is responsible for Runnable to FutureTask wraping:

```java
public class MyThreadPoolExecutor extends ThreadPoolExecutor {

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new MyFutureTask(task, value);            
    }
}
```

After that you can use MyThreadPoolExecutor as a ThreadPoolExecutor and in rejecting task processing:

```java
MyFutureTask myFutureTask = (MyFutureTask) runnable;
MyTask myTask = (MyTask) myFutureTask.getMyTask();
// Now you can do what you want with you task:
myTask.doSomthing();    
```

https://stackoverflow.com/questions/10931396/what-is-the-runnable-object-passed-in-java-thread-pool-rejectedexecutionhandler