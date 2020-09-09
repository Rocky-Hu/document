# 一、使用

~~~java
public class RedissonBloomFilterApp extends RedissonApp {

    public static void main(String[] args) {
        RedissonBloomFilterApp app = new RedissonBloomFilterApp();
        RBloomFilter<Integer> bloomFilter = app.getRedissonClient().getBloomFilter("redisson_bloomfilter");
        // 初始化布隆过滤器，预计统计元素数量为55000000，期望误差率为0.03
        bloomFilter.tryInit(55000000L, 0.03);

        bloomFilter.add(3);
        bloomFilter.add(5);
        bloomFilter.add(10);

        System.out.println(bloomFilter.contains(5));
    }

}
~~~

操作日志：

~~~verilog
1599643288.598704 [0 192.168.147.1:49613] "EVAL" "local size = redis.call('hget', KEYS[1], 'size');local hashIterations = redis.call('hget', KEYS[1], 'hashIterations');assert(size == false and hashIterations == false, 'Bloom filter config has been changed')" "1" "{redisson_bloomfilter}:config" "401414246" "5"
1599643288.598828 [0 lua] "hget" "{redisson_bloomfilter}:config" "size"
1599643288.598930 [0 lua] "hget" "{redisson_bloomfilter}:config" "hashIterations"
1599643288.598950 [0 192.168.147.1:49613] "HMSET" "{redisson_bloomfilter}:config" "size" "401414246" "hashIterations" "5" "expectedInsertions" "55000000" "falseProbability" "0.03"
1599643416.637835 [0 127.0.0.1:47072] "keys" "*"
1599643443.207216 [0 127.0.0.1:47072] "hgetall" "{redisson_bloomfilter}:config"
1599643455.525735 [0 192.168.147.1:49607] "EVAL" "local size = redis.call('hget', KEYS[1], 'size');local hashIterations = redis.call('hget', KEYS[1], 'hashIterations');assert(size == ARGV[1] and hashIterations == ARGV[2], 'Bloom filter config has been changed')" "1" "{redisson_bloomfilter}:config" "401414246" "5"
1599643455.526065 [0 lua] "hget" "{redisson_bloomfilter}:config" "size"
1599643455.526091 [0 lua] "hget" "{redisson_bloomfilter}:config" "hashIterations"
1599643455.526133 [0 192.168.147.1:49607] "SETBIT" "redisson_bloomfilter" "111639132" "1"
1599643455.749484 [0 192.168.147.1:49607] "SETBIT" "redisson_bloomfilter" "324608111" "1"
1599643456.099556 [0 192.168.147.1:49607] "SETBIT" "redisson_bloomfilter" "76034083" "1"
1599643456.099574 [0 192.168.147.1:49607] "SETBIT" "redisson_bloomfilter" "247801976" "1"
1599643456.099581 [0 192.168.147.1:49607] "SETBIT" "redisson_bloomfilter" "400642194" "1"
1599643528.947324 [0 192.168.147.1:49617] "EVAL" "local size = redis.call('hget', KEYS[1], 'size');local hashIterations = redis.call('hget', KEYS[1], 'hashIterations');assert(size == ARGV[1] and hashIterations == ARGV[2], 'Bloom filter config has been changed')" "1" "{redisson_bloomfilter}:config" "401414246" "5"
1599643528.947439 [0 lua] "hget" "{redisson_bloomfilter}:config" "size"
1599643528.947461 [0 lua] "hget" "{redisson_bloomfilter}:config" "hashIterations"
1599643528.947487 [0 192.168.147.1:49617] "SETBIT" "redisson_bloomfilter" "282490158" "1"
1599643528.947558 [0 192.168.147.1:49617] "SETBIT" "redisson_bloomfilter" "194054641" "1"
1599643528.947577 [0 192.168.147.1:49617] "SETBIT" "redisson_bloomfilter" "116331639" "1"
1599643528.947593 [0 192.168.147.1:49617] "SETBIT" "redisson_bloomfilter" "27896122" "1"
1599643528.947608 [0 192.168.147.1:49617] "SETBIT" "redisson_bloomfilter" "351587366" "1"
1599643529.831621 [0 192.168.147.1:49609] "EVAL" "local size = redis.call('hget', KEYS[1], 'size');local hashIterations = redis.call('hget', KEYS[1], 'hashIterations');assert(size == ARGV[1] and hashIterations == ARGV[2], 'Bloom filter config has been changed')" "1" "{redisson_bloomfilter}:config" "401414246" "5"
1599643529.831677 [0 lua] "hget" "{redisson_bloomfilter}:config" "size"
1599643529.831687 [0 lua] "hget" "{redisson_bloomfilter}:config" "hashIterations"
1599643529.831699 [0 192.168.147.1:49609] "SETBIT" "redisson_bloomfilter" "172064087" "1"
1599643529.831708 [0 192.168.147.1:49609] "SETBIT" "redisson_bloomfilter" "132786254" "1"
1599643529.831715 [0 192.168.147.1:49609] "SETBIT" "redisson_bloomfilter" "346051427" "1"
1599643529.831721 [0 192.168.147.1:49609] "SETBIT" "redisson_bloomfilter" "306773594" "1"
1599643529.831727 [0 192.168.147.1:49609] "SETBIT" "redisson_bloomfilter" "118624521" "1"
1599643683.521482 [0 192.168.147.1:49620] "EVAL" "local size = redis.call('hget', KEYS[1], 'size');local hashIterations = redis.call('hget', KEYS[1], 'hashIterations');assert(size == ARGV[1] and hashIterations == ARGV[2], 'Bloom filter config has been changed')" "1" "{redisson_bloomfilter}:config" "401414246" "5"
1599643683.521609 [0 lua] "hget" "{redisson_bloomfilter}:config" "size"
1599643683.521628 [0 lua] "hget" "{redisson_bloomfilter}:config" "hashIterations"
1599643683.521696 [0 192.168.147.1:49620] "GETBIT" "redisson_bloomfilter" "282490158"
1599643683.521708 [0 192.168.147.1:49620] "GETBIT" "redisson_bloomfilter" "194054641"
1599643683.521715 [0 192.168.147.1:49620] "GETBIT" "redisson_bloomfilter" "116331639"
1599643683.521721 [0 192.168.147.1:49620] "GETBIT" "redisson_bloomfilter" "27896122"
1599643683.521728 [0 192.168.147.1:49620] "GETBIT" "redisson_bloomfilter" "351587366"
~~~

# 二、原理

Redis产生两种数据结构：

~~~verilog
127.0.0.1:6379> keys *
1) "{redisson_bloomfilter}:config"
2) "redisson_bloomfilter"
127.0.0.1:6379> type {redisson_bloomfilter}:config
hash
127.0.0.1:6379> hgetall {redisson_bloomfilter}:config
1) "size"
2) "401414246"
3) "hashIterations"
4) "5"
5) "expectedInsertions"
6) "55000000"
7) "falseProbability"
8) "0.03"
127.0.0.1:6379> type redisson_bloomfilter
string
127.0.0.1:6379> get redisson_bloomfilter
^C
[root@localhost ~]# redis-cli
127.0.0.1:6379> strlen redisson_bloomfilter
(integer) 50080275
127.0.0.1:6379> 
~~~



