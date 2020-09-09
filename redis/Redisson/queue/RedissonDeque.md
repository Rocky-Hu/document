~~~java
RDeque<Integer> deque = getRedissonClient().getDeque("redisson_deque");
deque.addFirst(1);// LPUSH
deque.addLast(2);// RPUSH
deque.removeFirst();// LPOP
deque.removeLast();// RPOP
~~~

