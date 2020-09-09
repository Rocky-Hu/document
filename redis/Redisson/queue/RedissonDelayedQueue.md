

# 一、使用

~~~java
public class RedissonDelayedQueueApp extends RedissonApp{

    public static void main(String[] args) {
        RedissonDelayedQueueApp app = new RedissonDelayedQueueApp();
        RQueue<Integer> distinationQueue = app.getRedissonClient().getQueue("redisson_delayed_queue");
        RDelayedQueue<Integer> delayedQueue = app.getRedissonClient().getDelayedQueue(distinationQueue);
        // 10秒钟以后将消息发送到指定队列
        delayedQueue.offer(1, 50, TimeUnit.SECONDS);

        app.getRedissonClient().shutdown();
    }

}
~~~

# 二、实现原理

日志：

~~~verilog
1599640542.601944 [0 192.168.147.1:63701] "SUBSCRIBE" "redisson_delay_queue_channel:{redisson_delayed_queue}"
1599640542.642461 [0 192.168.147.1:63682] "EVAL" "local expiredValues = redis.call('zrangebyscore', KEYS[2], 0, ARGV[1], 'limit', 0, ARGV[2]); if #expiredValues > 0 then for i, v in ipairs(expiredValues) do local randomId, value = struct.unpack('dLc0', v);redis.call('rpush', KEYS[1], value);redis.call('lrem', KEYS[3], 1, v);end; redis.call('zrem', KEYS[2], unpack(expiredValues));end; local v = redis.call('zrange', KEYS[2], 0, 0, 'WITHSCORES'); if v[1] ~= nil then return v[2]; end return nil;" "3" "redisson_delayed_queue" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "redisson_delay_queue:{redisson_delayed_queue}" "1599640547695" "100"
1599640542.642665 [0 lua] "zrangebyscore" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "0" "1599640547695" "limit" "0" "100"
1599640542.642705 [0 lua] "zrange" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "0" "0" "WITHSCORES"
1599640542.685550 [0 192.168.147.1:63687] "EVAL" "local value = struct.pack('dLc0', tonumber(ARGV[2]), string.len(ARGV[3]), ARGV[3]);redis.call('zadd', KEYS[2], ARGV[1], value);redis.call('rpush', KEYS[3], value);local v = redis.call('zrange', KEYS[2], 0, 0); if v[1] == value then redis.call('publish', KEYS[4], ARGV[1]); end;" "4" "redisson_delayed_queue" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "redisson_delay_queue:{redisson_delayed_queue}" "redisson_delay_queue_channel:{redisson_delayed_queue}" "1599640597716" "8614935665155531800" "\xf7\x01"
1599640542.685651 [0 lua] "zadd" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "1599640597716" "\x19\x1c\xfd\x95\x99\xe3\xddC\x02\x00\x00\x00\x00\x00\x00\x00\xf7\x01"
1599640542.685677 [0 lua] "rpush" "redisson_delay_queue:{redisson_delayed_queue}" "\x19\x1c\xfd\x95\x99\xe3\xddC\x02\x00\x00\x00\x00\x00\x00\x00\xf7\x01"
1599640542.685694 [0 lua] "zrange" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "0" "0"
1599640542.685706 [0 lua] "publish" "redisson_delay_queue_channel:{redisson_delayed_queue}" "1599640597716"
1599640592.711556 [0 192.168.147.1:63689] "EVAL" "local expiredValues = redis.call('zrangebyscore', KEYS[2], 0, ARGV[1], 'limit', 0, ARGV[2]); if #expiredValues > 0 then for i, v in ipairs(expiredValues) do local randomId, value = struct.unpack('dLc0', v);redis.call('rpush', KEYS[1], value);redis.call('lrem', KEYS[3], 1, v);end; redis.call('zrem', KEYS[2], unpack(expiredValues));end; local v = redis.call('zrange', KEYS[2], 0, 0, 'WITHSCORES'); if v[1] ~= nil then return v[2]; end return nil;" "3" "redisson_delayed_queue" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "redisson_delay_queue:{redisson_delayed_queue}" "1599640597778" "100"
1599640592.711880 [0 lua] "zrangebyscore" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "0" "1599640597778" "limit" "0" "100"
1599640592.711924 [0 lua] "rpush" "redisson_delayed_queue" "\xf7\x01"
1599640592.711941 [0 lua] "lrem" "redisson_delay_queue:{redisson_delayed_queue}" "1" "\x19\x1c\xfd\x95\x99\xe3\xddC\x02\x00\x00\x00\x00\x00\x00\x00\xf7\x01"
1599640592.711965 [0 lua] "zrem" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "\x19\x1c\xfd\x95\x99\xe3\xddC\x02\x00\x00\x00\x00\x00\x00\x00\xf7\x01"
1599640592.711988 [0 lua] "zrange" "redisson_delay_queue_timeout:{redisson_delayed_queue}" "0" "0" "WITHSCORES"
~~~

实现原理：

超时时间作为分数存入到zset中，Redisson内部起一个定时任务（TimerTask），时间到期从zset中查询出（移除）已过期的元素然后存入到目标队列中。



