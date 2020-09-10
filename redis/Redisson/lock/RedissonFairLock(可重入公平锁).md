# 一、使用示例



# 二、实现原理

基于Redis的Redisson分布式可重入公平锁也是实现了`java.util.concurrent.locks.Lock`接口的一种`RLock`对象。同时还提供了[异步（Async）](http://static.javadoc.io/org.redisson/redisson/3.10.0/org/redisson/api/RLockAsync.html)、[反射式（Reactive）](http://static.javadoc.io/org.redisson/redisson/3.10.0/org/redisson/api/RLockReactive.html)和[RxJava2标准](http://static.javadoc.io/org.redisson/redisson/3.10.0/org/redisson/api/RLockRx.html)的接口。它保证了当多个Redisson客户端线程同时请求加锁时，优先分配给先发出请求的线程。所有请求线程会在一个队列中排队，当某个线程出现宕机时，Redisson会等待5秒后继续下一个线程，也就是说如果前面有5个线程都处于等待状态，那么后面的线程会等待至少25秒。

## 2.1. 加锁

执行的脚本和命令：

~~~verilog
1599727329.015284 [0 192.168.147.1:56115] "EVAL" "while true do local firstThreadId2 = redis.call('lindex', KEYS[2], 0);if firstThreadId2 == false then break;end;local timeout = tonumber(redis.call('zscore', KEYS[3], firstThreadId2));if timeout <= tonumber(ARGV[4]) then redis.call('zrem', KEYS[3], firstThreadId2);redis.call('lpop', KEYS[2]);else break;end;end;if (redis.call('exists', KEYS[1]) == 0) and ((redis.call('exists', KEYS[2]) == 0) or (redis.call('lindex', KEYS[2], 0) == ARGV[2])) then redis.call('lpop', KEYS[2]);redis.call('zrem', KEYS[3], ARGV[2]);local keys = redis.call('zrange', KEYS[3], 0, -1);for i = 1, #keys, 1 do redis.call('zincrby', KEYS[3], -tonumber(ARGV[3]), keys[i]);end;redis.call('hset', KEYS[1], ARGV[2], 1);redis.call('pexpire', KEYS[1], ARGV[1]);return nil;end;if redis.call('hexists', KEYS[1], ARGV[2]) == 1 then redis.call('hincrby', KEYS[1], ARGV[2],1);redis.call('pexpire', KEYS[1], ARGV[1]);return nil;end;local timeout = redis.call('zscore', KEYS[3], ARGV[2]);if timeout ~= false then return timeout - tonumber(ARGV[3]) - tonumber(ARGV[4]);end;local lastThreadId = redis.call('lindex', KEYS[2], -1);local ttl;if lastThreadId ~= false and lastThreadId ~= ARGV[2] then ttl = tonumber(redis.call('zscore', KEYS[3], lastThreadId)) - tonumber(ARGV[4]);else ttl = redis.call('pttl', KEYS[1]);end;local timeout = ttl + tonumber(ARGV[3]) + tonumber(ARGV[4]);if redis.call('zadd', KEYS[3], timeout, ARGV[2]) == 1 then redis.call('rpush', KEYS[2], ARGV[2]);end;return ttl;" "3" "redisson_fair_reentrant_lock" "redisson_lock_queue:{redisson_fair_reentrant_lock}" "redisson_lock_timeout:{redisson_fair_reentrant_lock}" "30000" "dcd015c1-9313-4475-a516-12b05240220f:1" "5000" "1599727333962"
1599727329.016188 [0 lua] "lindex" "redisson_lock_queue:{redisson_fair_reentrant_lock}" "0"
1599727329.016336 [0 lua] "exists" "redisson_fair_reentrant_lock"
1599727329.016351 [0 lua] "exists" "redisson_lock_queue:{redisson_fair_reentrant_lock}"
1599727329.016364 [0 lua] "lpop" "redisson_lock_queue:{redisson_fair_reentrant_lock}"
1599727329.016375 [0 lua] "zrem" "redisson_lock_timeout:{redisson_fair_reentrant_lock}" "dcd015c1-9313-4475-a516-12b05240220f:1"
1599727329.016405 [0 lua] "zrange" "redisson_lock_timeout:{redisson_fair_reentrant_lock}" "0" "-1"
1599727329.016472 [0 lua] "hset" "redisson_fair_reentrant_lock" "dcd015c1-9313-4475-a516-12b05240220f:1" "1"
1599727329.016499 [0 lua] "pexpire" "redisson_fair_reentrant_lock" "30000"
~~~

Redis生成的数据结构：

~~~verilog

~~~



## 2.2. 解锁

执行的脚本和命令：

~~~verilog
1599727329.028510 [0 192.168.147.1:56123] "EVAL" "while true do local firstThreadId2 = redis.call('lindex', KEYS[2], 0);if firstThreadId2 == false then break;end; local timeout = tonumber(redis.call('zscore', KEYS[3], firstThreadId2));if timeout <= tonumber(ARGV[4]) then redis.call('zrem', KEYS[3], firstThreadId2); redis.call('lpop', KEYS[2]); else break;end; end;if (redis.call('exists', KEYS[1]) == 0) then local nextThreadId = redis.call('lindex', KEYS[2], 0); if nextThreadId ~= false then redis.call('publish', KEYS[4] .. ':' .. nextThreadId, ARGV[1]); end; return 1; end;if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then return nil;end; local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); if (counter > 0) then redis.call('pexpire', KEYS[1], ARGV[2]); return 0; end; redis.call('del', KEYS[1]); local nextThreadId = redis.call('lindex', KEYS[2], 0); if nextThreadId ~= false then redis.call('publish', KEYS[4] .. ':' .. nextThreadId, ARGV[1]); end; return 1; " "4" "redisson_fair_reentrant_lock" "redisson_lock_queue:{redisson_fair_reentrant_lock}" "redisson_lock_timeout:{redisson_fair_reentrant_lock}" "redisson_lock__channel:{redisson_fair_reentrant_lock}" "0" "30000" "dcd015c1-9313-4475-a516-12b05240220f:1" "1599727334030"
1599727329.028791 [0 lua] "lindex" "redisson_lock_queue:{redisson_fair_reentrant_lock}" "0"
1599727329.028816 [0 lua] "exists" "redisson_fair_reentrant_lock"
1599727329.028831 [0 lua] "hexists" "redisson_fair_reentrant_lock" "dcd015c1-9313-4475-a516-12b05240220f:1"
1599727329.028860 [0 lua] "hincrby" "redisson_fair_reentrant_lock" "dcd015c1-9313-4475-a516-12b05240220f:1" "-1"
1599727329.028889 [0 lua] "del" "redisson_fair_reentrant_lock"
1599727329.028905 [0 lua] "lindex" "redisson_lock_queue:{redisson_fair_reentrant_lock}" "0"
~~~

