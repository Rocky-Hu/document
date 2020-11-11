#  一、使用示例

~~~java
public class RedissonRateLimiterExample extends RedissonApp {

    public static void main(String[] args) {
        RedissonRateLimiterExample example = new RedissonRateLimiterExample();
        RRateLimiter rateLimiter = example.getRedissonClient().getRateLimiter("redisson_ratelimiter");
        rateLimiter.trySetRate(RateType.PER_CLIENT, 1, 1, RateIntervalUnit.MINUTES);

        ExecutorService executorService= Executors.newFixedThreadPool(10);
        for (int i=0;i<10;i++){
            executorService.submit(()->{
                try{
                    rateLimiter.acquire();
                    System.out.println("线程"+Thread.currentThread().getId()+"进入数据区："+System.currentTimeMillis());
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }

    }

}
~~~

# 二、原理

Redisson的限流器采用的是**令牌桶算法**。

## 2.1. 初始化配置

~~~java
RRateLimiter rateLimiter = example.getRedissonClient().getRateLimiter("redisson_ratelimiter");
rateLimiter.trySetRate(RateType.PER_CLIENT, 1, 1, RateIntervalUnit.MINUTES);
~~~

上面设置的限流策略为：1分钟生成1个令牌。也就是说1分钟内只允许有1个请求。

### trySetRate

此方法的配置是：初始化RateLimiter的状态和存储配置到Redis服务器。

~~~java
boolean trySetRate(RateType mode, long rate, long rateInterval, RateIntervalUnit rateIntervalUnit);
~~~

- RateType

  这个参数表示ReateLimiter的有效范围。有两个取值：

  ~~~java
  /**
  * Total rate for all RateLimiter instances
  */
  OVERALL,
  
  /**
  * Total rate for all RateLimiter instances working with the same Redisson instance
  */
  PER_CLIENT
  ~~~

  OVERALL - 所有客户端加总限流

  PER_CLIENT - 每个客户端单独计算流量

其它几个参数的作用可以通过下面的实例来看：

~~~java
// 最大流速 = 每1秒钟产生10个令牌
rateLimiter.trySetRate(RateType.OVERALL, 10, 1, RateIntervalUnit.SECONDS);
~~~

- 返回值

  返回一个布尔值。取值逻辑就是hsetnx的设值返回逻辑。如果key已经设置过则返回false，反之返回true。

调用trySetRate方法执行的Lua脚本如下：

*ratelimiter_trysetrate.lua*

~~~lua
redis.call('hsetnx', KEYS[1], 'rate', ARGV[1]);
redis.call('hsetnx', KEYS[1], 'interval', ARGV[2]);
return redis.call('hsetnx', KEYS[1], 'type', ARGV[3]);
~~~

执行类似：

~~~lua
./redis-cli --eval /data/ratelimiter_trysetrate.lua redisson_ratelimiter , 1 60000 1
~~~

方法执行日志：

~~~verilog
1599722083.745978 [0 192.168.147.1:51222] "EVAL" "redis.call('hsetnx', KEYS[1], 'rate', ARGV[1]);redis.call('hsetnx', KEYS[1], 'interval', ARGV[2]);return redis.call('hsetnx', KEYS[1], 'type', ARGV[3]);" "1" "redisson_ratelimiter" "1" "60000" "1"
1599722083.746064 [0 lua] "hsetnx" "redisson_ratelimiter" "rate" "1"
1599722083.746085 [0 lua] "hsetnx" "redisson_ratelimiter" "interval" "60000"
1599722083.746104 [0 lua] "hsetnx" "redisson_ratelimiter" "type" "1"
~~~

Redis中生成的数据结构：

~~~verilog
127.0.0.1:6379> keys *
1) "redisson_ratelimiter"
127.0.0.1:6379> type redisson_ratelimiter
hash
127.0.0.1:6379> hgetall redisson_ratelimiter
1) "rate"
2) "1"
3) "interval"
4) "60000"
5) "type"
6) "1"
127.0.0.1:6379> 
~~~

## 2.2. 令牌获取

### acquire()

Acquires a permit from this RateLimiter, blocking until one is available. Acquires a permit, if one is available and returns immediately, reducing the number of available permits by one.

从速度限制器中获取一个令牌，可获得之前一直阻塞。获取令牌，如果可获取到，则立即返回，同时减少一个可用令牌。

调用acquire方法执行的Lua脚本如下：

~~~lua
--
-- Created by IntelliJ IDEA.
-- User: rocky.hu
-- Date: 2020/11/5
-- Time: 11:38
-- To change this template use File | Settings | File Templates.
--
-- KEYS[1]: RateLimiter的名称，也就是调用getRateLimiter方法是传递的名称
local rate = redis.call('hget', KEYS[1], 'rate');
local interval = redis.call('hget', KEYS[1], 'interval');
local type = redis.call('hget', KEYS[1], 'type');
assert(rate ~= false and interval ~= false and type ~= false, 'RateLimiter is not initialized')
-- KEYS[2]: 值名称，为 {KEYS[1]}:value
local valueName = KEYS[2];
-- KEYS[3]: type为1，对每个客户端单独限流。取值为{KEYS[1]}:value:代表客户端的UUID字符串
if type == '1' then
    valueName = KEYS[3];
end;
local currentValue = redis.call('get', valueName);
if currentValue ~= false then
    if tonumber(currentValue) < tonumber(ARGV[1]) then
        return redis.call('pttl', valueName);
    else
        redis.call('decrby', valueName, ARGV[1]);
        return nil;
    end;
else
    assert(tonumber(rate) >= tonumber(ARGV[1]), 'Requested permits amount could not exceed defined rate');
    redis.call('set', valueName, rate, 'px', interval);
    redis.call('decrby', valueName, ARGV[1]);
    return nil;
end;
~~~

执行的脚本和命令：

~~~verilog
1599722232.973651 [0 192.168.147.1:51220] "EVAL" "local rate = redis.call('hget', KEYS[1], 'rate');local interval = redis.call('hget', KEYS[1], 'interval');local type = redis.call('hget', KEYS[1], 'type');assert(rate ~= false and interval ~= false and type ~= false, 'RateLimiter is not initialized')local valueName = KEYS[2];if type == '1' then valueName = KEYS[3];end;local currentValue = redis.call('get', valueName); if currentValue ~= false then if tonumber(currentValue) < tonumber(ARGV[1]) then return redis.call('pttl', valueName); else redis.call('decrby', valueName, ARGV[1]); return nil; end; else assert(tonumber(rate) >= tonumber(ARGV[1]), 'Requested permits amount could not exceed defined rate'); redis.call('set', valueName, rate, 'px', interval); redis.call('decrby', valueName, ARGV[1]); return nil; end;" "3" "redisson_ratelimiter" "{redisson_ratelimiter}:value" "{redisson_ratelimiter}:value:b1af180d-99f1-4862-a702-5b5876538f8e" "1"
1599722232.974037 [0 lua] "hget" "redisson_ratelimiter" "rate"
1599722232.974053 [0 lua] "hget" "redisson_ratelimiter" "interval"
1599722232.974067 [0 lua] "hget" "redisson_ratelimiter" "type"
1599722232.974213 [0 lua] "get" "{redisson_ratelimiter}:value:b1af180d-99f1-4862-a702-5b5876538f8e"
1599722232.974238 [0 lua] "set" "{redisson_ratelimiter}:value:b1af180d-99f1-4862-a702-5b5876538f8e" "1" "px" "60000"
1599722232.974265 [0 lua] "decrby" "{redisson_ratelimiter}:value:b1af180d-99f1-4862-a702-5b5876538f8e" "1"
~~~

Redis生成的数据结构：

~~~verilog
127.0.0.1:6379> keys *
1) "redisson_ratelimiter"
2) "{redisson_ratelimiter}:value:b1af180d-99f1-4862-a702-5b5876538f8e"
127.0.0.1:6379> type {redisson_ratelimiter}:value:b1af180d-99f1-4862-a702-5b5876538f8e
string
127.0.0.1:6379> get {redisson_ratelimiter}:value:b1af180d-99f1-4862-a702-5b5876538f8e
"0"
127.0.0.1:6379> ttl {redisson_ratelimiter}:value:b1af180d-99f1-4862-a702-5b5876538f8e
(integer) 30
127.0.0.1:6379> 
~~~

令牌已经用完，再次：

~~~java
rateLimiter.acquire();
~~~

执行的脚本和命令：

~~~verilog
1599722627.682919 [0 192.168.147.1:51767] "EVAL" "local rate = redis.call('hget', KEYS[1], 'rate');local interval = redis.call('hget', KEYS[1], 'interval');local type = redis.call('hget', KEYS[1], 'type');assert(rate ~= false and interval ~= false and type ~= false, 'RateLimiter is not initialized')local valueName = KEYS[2];if type == '1' then valueName = KEYS[3];end;local currentValue = redis.call('get', valueName); if currentValue ~= false then if tonumber(currentValue) < tonumber(ARGV[1]) then return redis.call('pttl', valueName); else redis.call('decrby', valueName, ARGV[1]); return nil; end; else assert(tonumber(rate) >= tonumber(ARGV[1]), 'Requested permits amount could not exceed defined rate'); redis.call('set', valueName, rate, 'px', interval); redis.call('decrby', valueName, ARGV[1]); return nil; end;" "3" "redisson_ratelimiter" "{redisson_ratelimiter}:value" "{redisson_ratelimiter}:value:211f247a-2e16-4668-bcda-be0f92926e96" "1"
1599722627.683200 [0 lua] "hget" "redisson_ratelimiter" "rate"
1599722627.683223 [0 lua] "hget" "redisson_ratelimiter" "interval"
1599722627.683239 [0 lua] "hget" "redisson_ratelimiter" "type"
1599722627.683254 [0 lua] "get" "{redisson_ratelimiter}:value:211f247a-2e16-4668-bcda-be0f92926e96"
1599722627.683282 [0 lua] "pttl" "{redisson_ratelimiter}:value:211f247a-2e16-4668-bcda-be0f92926e96"
~~~

# 三、版本变化

上面是3.12.3版本的代码。

