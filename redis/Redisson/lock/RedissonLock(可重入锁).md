# 一、使用示例

~~~java
public class RedissonLockExample extends RedissonApp {

    public static void main(String[] args) {
        RedissonLockExample example = new RedissonLockExample();
        RLock lock = example.getRedissonClient().getLock("redisson_reentrant_lock");
        lock.lock();
        lock.unlock();

        lock.lock(100, TimeUnit.SECONDS);
        lock.unlock();

        try {
            lock.tryLock(100, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        lock.unlock();
    }

}
~~~

# 二、实现原理

## 2.1. 初始化

~~~java
RLock lock = example.getRedissonClient().getLock("redisson_reentrant_lock");
~~~

## 2.2. 获取锁

~~~java
lock.lock();
~~~

执行的脚本和命令：

~~~verilog
1599725823.011342 [0 192.168.147.1:54488] "EVAL" "if (redis.call('exists', KEYS[1]) == 0) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; return redis.call('pttl', KEYS[1]);" "1" "redisson_reentrant_lock" "30000" "c1a43c75-fee2-4393-b37b-1b1733518ea4:1"
1599725823.011535 [0 lua] "exists" "redisson_reentrant_lock"
1599725823.011568 [0 lua] "hincrby" "redisson_reentrant_lock" "c1a43c75-fee2-4393-b37b-1b1733518ea4:1" "1"
1599725823.011586 [0 lua] "pexpire" "redisson_reentrant_lock" "30000"
~~~

Redis生成的数据结构：

~~~verilog
127.0.0.1:6379> keys *
1) "redisson_reentrant_lock"
127.0.0.1:6379> type redisson_reentrant_lock
hash
127.0.0.1:6379> hgetall redisson_reentrant_lock
1) "a5308c3b-bd44-4c31-af92-ca3c4ed38184:1"
2) "1"
127.0.0.1:6379> pttl redisson_reentrant_lock
(integer) -2
127.0.0.1:6379> 
~~~

用map数据结构表示锁，其中有个field记录获取锁的次数。

### 指定租约

~~~java
lock.lock(100, TimeUnit.SECONDS);
~~~

执行的脚本和命令：

~~~verilog
1599726689.853463 [0 192.168.147.1:55506] "EVAL" "if (redis.call('exists', KEYS[1]) == 0) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; return redis.call('pttl', KEYS[1]);" "1" "redisson_reentrant_lock" "100000" "17a52fb5-bb55-4a2a-ae87-f2397c70b74e:1"
1599726689.853596 [0 lua] "exists" "redisson_reentrant_lock"
1599726689.853616 [0 lua] "hincrby" "redisson_reentrant_lock" "17a52fb5-bb55-4a2a-ae87-f2397c70b74e:1" "1"
1599726689.853723 [0 lua] "pexpire" "redisson_reentrant_lock" "100000"
~~~

## 2.3. 释放锁

从上面可以看到，默认

~~~
lock.unlock();
~~~

执行脚本和命令：

~~~verilog
1599726451.991593 [0 192.168.147.1:55232] "EVAL" "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then return nil;end; local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); if (counter > 0) then redis.call('pexpire', KEYS[1], ARGV[2]); return 0; else redis.call('del', KEYS[1]); redis.call('publish', KEYS[2], ARGV[1]); return 1; end; return nil;" "2" "redisson_reentrant_lock" "redisson_lock__channel:{redisson_reentrant_lock}" "0" "30000" "68800283-19b2-4c88-a46c-28ceceb04e52:1"
1599726451.991740 [0 lua] "hexists" "redisson_reentrant_lock" "68800283-19b2-4c88-a46c-28ceceb04e52:1"
1599726451.991772 [0 lua] "hincrby" "redisson_reentrant_lock" "68800283-19b2-4c88-a46c-28ceceb04e52:1" "-1"
1599726451.991797 [0 lua] "del" "redisson_reentrant_lock"
1599726451.991810 [0 lua] "publish" "redisson_lock__channel:{redisson_reentrant_lock}" "0"
~~~

