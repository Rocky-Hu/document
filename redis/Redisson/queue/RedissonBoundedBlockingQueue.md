# 一、使用示例

~~~java
public class RedissonBoundedBlockingQueueApp extends RedissonApp {

    public static void main(String[] args) {
        RedissonBoundedBlockingQueueApp app = new RedissonBoundedBlockingQueueApp();
        RBoundedBlockingQueue<Integer> queue = app.getRedissonClient().getBoundedBlockingQueue("redisson_bounded_blocking_queue");
        queue.trySetCapacity(2);

        queue.offer(1);
        queue.offer(2);
//        queue.offer(3);

//        queue.add(1);
//        queue.add(2);
//        queue.add(3);

//        try {
//            queue.put(1);
//            queue.put(2);
//            queue.put(3);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        queue.element();
        queue.peek();

//        queue.remove();

//        queue.poll();

        try {
            queue.take();
        } catch (Exception ex) {
           ex.printStackTrace();
        }

        app.getRedissonClient().shutdown();

    }

}
~~~

# 二、实现原理

## 2.1. 有界队列初始化

~~~java
RBoundedBlockingQueue<Integer> queue = app.getRedissonClient().getBoundedBlockingQueue("redisson_bounded_blocking_queue");
queue.trySetCapacity(2);
~~~

执行上面代码进行有界队列初始化，并且设置其容量。会发送lua脚本到Redis服务器创建一个string类型的数据结构，值为队列的容量：

~~~verilog
127.0.0.1:6379> keys *
1) "redisson_bqs:{redisson_bounded_blocking_queue}"
127.0.0.1:6379> type redisson_bqs:{redisson_bounded_blocking_queue}
string
127.0.0.1:6379> get redisson_bqs:{redisson_bounded_blocking_queue}
"2"
127.0.0.1:6379> 
~~~

容量设置代码如下：

~~~java
@Override
public RFuture<Boolean> trySetCapacityAsync(int capacity) {
    String channelName = RedissonSemaphore.getChannelName(getSemaphoreName());
    return commandExecutor.evalWriteAsync(getName(), LongCodec.INSTANCE, RedisCommands.EVAL_BOOLEAN,
            "local value = redis.call('get', KEYS[1]); " +
            "if (value == false) then "
                + "redis.call('set', KEYS[1], ARGV[1]); "
                + "redis.call('publish', KEYS[2], ARGV[1]); "
                + "return 1;"
            + "end;"
            + "return 0;",
            Arrays.<Object>asList(getSemaphoreName(), channelName), capacity);
}
~~~

初始化执行的命令如下：

~~~verilog
1599628203.557224 [0 192.168.147.1:51742] "EVAL" "local value = redis.call('get', KEYS[1]); if (value == false) then redis.call('set', KEYS[1], ARGV[1]); redis.call('publish', KEYS[2], ARGV[1]); return 1;end;return 0;" "2" "redisson_bqs:{redisson_bounded_blocking_queue}" "redisson_sc:redisson_bqs:{redisson_bounded_blocking_queue}" "2"
1599628203.558435 [0 lua] "get" "redisson_bqs:{redisson_bounded_blocking_queue}"
1599628203.558628 [0 lua] "set" "redisson_bqs:{redisson_bounded_blocking_queue}" "2"
1599628203.558655 [0 lua] "publish" "redisson_sc:redisson_bqs:{redisson_bounded_blocking_queue}" "2"
~~~

## 2.2. 添加元素

~~~java
queue.offer();
~~~

执行的命令脚本为：

~~~verilog
1599628563.648534 [0 192.168.147.1:52138] "EVAL" "local value = redis.call('get', KEYS[1]); assert(value ~= false, 'Capacity of queue ' .. KEYS[1] .. ' has not been set'); if (tonumber(value) >= tonumber(ARGV[1])) then redis.call('decrby', KEYS[1], ARGV[1]); redis.call('rpush', KEYS[2], unpack(ARGV, 2, #ARGV));return 1; end; return 0;" "2" "redisson_bqs:{redisson_bounded_blocking_queue}" "redisson_bounded_blocking_queue" "1" "\xf7\x01"
1599628563.648607 [0 lua] "get" "redisson_bqs:{redisson_bounded_blocking_queue}"
1599628563.648624 [0 lua] "decrby" "redisson_bqs:{redisson_bounded_blocking_queue}" "1"
1599628563.648636 [0 lua] "rpush" "redisson_bounded_blocking_queue" "\xf7\x01"
~~~

动作为：get(获取容量)->decrby(减容量)->rpush(存元素)。

> queue.add();// 和java的Queue接口定义一样，如果查询出队列容量为0，则表示队列已满，调用此方法会抛出异常。

~~~java
queue.put()
~~~

阻塞队列进行put，如果当前队列容量为0，则put操作被阻塞。

Redisson内部使用Redis的发布/订阅功能实现。在初始化阻塞队列，设置容量时，查看上面的日志可以看到：

~~~verilog
1599628203.558655 [0 lua] "publish" "redisson_sc:redisson_bqs:{redisson_bounded_blocking_queue}" "2"
~~~

这里指定的频道发送一条消息，put()操作检查队列容量，如果队列容量为0，则执行订阅操作：

~~~verilog
1599630125.132274 [0 192.168.147.1:53257] "SUBSCRIBE" "redisson_sc:redisson_bqs:{redisson_bounded_blocking_queue}"
~~~

## 2.3. 获取元素

~~~java
queue.element();
queue.peek();
~~~

两个操作的执行的命令都是：

~~~verilog
1599631373.350223 [0 192.168.147.1:54161] "LINDEX" "redisson_bounded_blocking_queue" "0"
~~~

## 2.4. 删除元素(包括获取并删除)

~~~java
queue.remove();
~~~

执行的脚本：

~~~verilog
1599631827.568774 [0 lua] "lpop" "redisson_bounded_blocking_queue"
1599631827.568789 [0 lua] "incrby" "redisson_bqs:{redisson_bounded_blocking_queue}" "1"
1599631827.568809 [0 lua] "publish" "redisson_sc:redisson_bqs:{redisson_bounded_blocking_queue}" "1"
~~~

~~~java
queue.poll();
~~~

执行的脚本：

~~~verilog
1599631959.995585 [0 192.168.147.1:54626] "EVAL" "local res = redis.call('lpop', KEYS[1]);if res ~= false then local value = redis.call('incrby', KEYS[2], ARGV[1]); redis.call('publish', KEYS[3], value); end;return res;" "3" "redisson_bounded_blocking_queue" "redisson_bqs:{redisson_bounded_blocking_queue}" "redisson_sc:redisson_bqs:{redisson_bounded_blocking_queue}" "1"
1599631959.995685 [0 lua] "lpop" "redisson_bounded_blocking_queue"
1599631959.995700 [0 lua] "incrby" "redisson_bqs:{redisson_bounded_blocking_queue}" "1"
1599631959.995714 [0 lua] "publish" "redisson_sc:redisson_bqs:{redisson_bounded_blocking_queue}" "2"
~~~

~~~java
queue.take();
~~~

执行的脚本（无元素情况）：

~~~verilog
1599632015.779998 [0 192.168.147.1:54642] "BLPOP" "redisson_bounded_blocking_queue" "0"
~~~

执行的脚本（有元素情况）：

~~~verilog
1599632271.292880 [0 192.168.147.1:55101] "BLPOP" "redisson_bounded_blocking_queue" "0"
1599632271.306895 [0 192.168.147.1:55106] "EVAL" "local value = redis.call('incrby', KEYS[1], ARGV[1]); redis.call('publish', KEYS[2], value); " "2" "redisson_bqs:{redisson_bounded_blocking_queue}" "redisson_sc:redisson_bqs:{redisson_bounded_blocking_queue}" "1"
1599632271.306996 [0 lua] "incrby" "redisson_bqs:{redisson_bounded_blocking_queue}" "1"
1599632271.307026 [0 lua] "publish" "redisson_sc:redisson_bqs:{redisson_bounded_blocking_queue}" "1"
~~~





