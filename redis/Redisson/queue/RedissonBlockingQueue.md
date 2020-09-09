~~~java
public class RedissonBlockingQueueApp extends RedissonApp {

    public static void main(String[] args) {

        RedissonBlockingQueueApp app = new RedissonBlockingQueueApp();

        RBlockingQueue<String> blockingQueue = app.getRedissonClient().getBlockingQueue("redisson_blocking_queue");
        blockingQueue.offer("a");// RPUSH

        String obj1 = blockingQueue.peek();//LINDEX
        String obj2 = blockingQueue.poll();//LPOP

        try {
            blockingQueue.put("b");//RPUSH
            blockingQueue.take();//BLPOP
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.getRedissonClient().shutdown();
    }

}
~~~

Redis命令：

http://doc.redisfans.com/list/blpop.html

