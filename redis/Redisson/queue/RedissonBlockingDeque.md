~~~java
public class RedissonBlockingDequeApp extends RedissonApp {

    public static void main(String[] args) throws Exception {
        RedissonBlockingDequeApp app = new RedissonBlockingDequeApp();
        RBlockingDeque<Integer> deque = app.getRedissonClient().getBlockingDeque("redisson_blocking_deque");
        deque.putFirst(1);
        deque.putLast(2);
//        Integer firstValue = deque.takeFirst();
//        Integer lastValue = deque.takeLast();
        Integer firstValue = deque.pollFirst(10, TimeUnit.MINUTES);
        Integer lastValue = deque.pollLast(3, TimeUnit.MINUTES);
    }

}
~~~

