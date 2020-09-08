~~~java
RQueue<Integer> queue = getRedissonClient().getQueue("redisson_queue");
queue.offer(1);// RPUSH
queue.poll();// LPOP
~~~

