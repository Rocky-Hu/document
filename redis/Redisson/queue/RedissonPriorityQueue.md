~~~java
public class RedissonPriorityQueueApp extends RedissonApp {

    public static void main(String[] args) {

        RedissonPriorityQueueApp app = new RedissonPriorityQueueApp();

        RPriorityQueue<Integer> rPriorityQueue = app.getRedissonClient().getPriorityQueue("redisson_priority_queue");
        rPriorityQueue.add(3);
        rPriorityQueue.add(1);
        rPriorityQueue.add(2);

        //rPriorityQueue.poll();

        app.getRedissonClient().shutdown();
    }

}
~~~

脚本命令日志：

~~~verilog
1599634839.814703 [0 192.168.147.1:58266] "LLEN" "redisson_priority_queue"
1599634840.838940 [0 192.168.147.1:58270] "LLEN" "redisson_priority_queue"
1599634846.447631 [0 192.168.147.1:58269] "EVAL" "if (redis.call('exists', KEYS[1]) == 0) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; return redis.call('pttl', KEYS[1]);" "1" "redisson_sortedset_lock:{redisson_priority_queue}" "30000" "43908a60-9fda-46da-bb73-1833250a4a86:1"
1599634846.447791 [0 lua] "exists" "redisson_sortedset_lock:{redisson_priority_queue}"
1599634846.447818 [0 lua] "hincrby" "redisson_sortedset_lock:{redisson_priority_queue}" "43908a60-9fda-46da-bb73-1833250a4a86:1" "1"
1599634846.447853 [0 lua] "pexpire" "redisson_sortedset_lock:{redisson_priority_queue}" "30000"
1599634846.466304 [0 192.168.147.1:58262] "GET" "{redisson_priority_queue}:redisson_sortedset_comparator"
1599634846.473610 [0 192.168.147.1:58263] "LLEN" "redisson_priority_queue"
1599634846.508189 [0 192.168.147.1:58267] "EVAL" "local len = redis.call('llen', KEYS[1]);if tonumber(ARGV[1]) < len then local pivot = redis.call('lindex', KEYS[1], ARGV[1]);redis.call('linsert', KEYS[1], 'before', pivot, ARGV[2]);return;end;redis.call('rpush', KEYS[1], ARGV[2]);" "1" "redisson_priority_queue" "0" "\xf7\x03"
1599634846.508250 [0 lua] "llen" "redisson_priority_queue"
1599634846.508262 [0 lua] "rpush" "redisson_priority_queue" "\xf7\x03"
1599634846.514376 [0 192.168.147.1:58265] "EVAL" "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then return nil;end; local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); if (counter > 0) then redis.call('pexpire', KEYS[1], ARGV[2]); return 0; else redis.call('del', KEYS[1]); redis.call('publish', KEYS[2], ARGV[1]); return 1; end; return nil;" "2" "redisson_sortedset_lock:{redisson_priority_queue}" "redisson_lock__channel:redisson_sortedset_lock:{redisson_priority_queue}" "0" "30000" "43908a60-9fda-46da-bb73-1833250a4a86:1"
1599634846.514569 [0 lua] "hexists" "redisson_sortedset_lock:{redisson_priority_queue}" "43908a60-9fda-46da-bb73-1833250a4a86:1"
1599634846.514609 [0 lua] "hincrby" "redisson_sortedset_lock:{redisson_priority_queue}" "43908a60-9fda-46da-bb73-1833250a4a86:1" "-1"
1599634846.514644 [0 lua] "del" "redisson_sortedset_lock:{redisson_priority_queue}"
1599634846.514669 [0 lua] "publish" "redisson_lock__channel:redisson_sortedset_lock:{redisson_priority_queue}" "0"
1599634847.555381 [0 192.168.147.1:58261] "LLEN" "redisson_priority_queue"
1599634848.568055 [0 192.168.147.1:58268] "LLEN" "redisson_priority_queue"
1599634858.664327 [0 192.168.147.1:58273] "EVAL" "if (redis.call('exists', KEYS[1]) == 0) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; return redis.call('pttl', KEYS[1]);" "1" "redisson_sortedset_lock:{redisson_priority_queue}" "30000" "43908a60-9fda-46da-bb73-1833250a4a86:1"
1599634858.664482 [0 lua] "exists" "redisson_sortedset_lock:{redisson_priority_queue}"
1599634858.664511 [0 lua] "hincrby" "redisson_sortedset_lock:{redisson_priority_queue}" "43908a60-9fda-46da-bb73-1833250a4a86:1" "1"
1599634858.664548 [0 lua] "pexpire" "redisson_sortedset_lock:{redisson_priority_queue}" "30000"
1599634858.672601 [0 192.168.147.1:58264] "GET" "{redisson_priority_queue}:redisson_sortedset_comparator"
1599634858.681320 [0 192.168.147.1:58274] "LLEN" "redisson_priority_queue"
1599634858.689677 [0 192.168.147.1:58275] "LINDEX" "redisson_priority_queue" "0"
1599634858.706168 [0 192.168.147.1:58277] "EVAL" "local len = redis.call('llen', KEYS[1]);if tonumber(ARGV[1]) < len then local pivot = redis.call('lindex', KEYS[1], ARGV[1]);redis.call('linsert', KEYS[1], 'before', pivot, ARGV[2]);return;end;redis.call('rpush', KEYS[1], ARGV[2]);" "1" "redisson_priority_queue" "0" "\xf7\x01"
1599634858.706406 [0 lua] "llen" "redisson_priority_queue"
1599634858.706441 [0 lua] "lindex" "redisson_priority_queue" "0"
1599634858.706480 [0 lua] "linsert" "redisson_priority_queue" "before" "\xf7\x03" "\xf7\x01"
1599634858.713541 [0 192.168.147.1:58276] "EVAL" "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then return nil;end; local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); if (counter > 0) then redis.call('pexpire', KEYS[1], ARGV[2]); return 0; else redis.call('del', KEYS[1]); redis.call('publish', KEYS[2], ARGV[1]); return 1; end; return nil;" "2" "redisson_sortedset_lock:{redisson_priority_queue}" "redisson_lock__channel:redisson_sortedset_lock:{redisson_priority_queue}" "0" "30000" "43908a60-9fda-46da-bb73-1833250a4a86:1"
1599634858.713858 [0 lua] "hexists" "redisson_sortedset_lock:{redisson_priority_queue}" "43908a60-9fda-46da-bb73-1833250a4a86:1"
1599634858.713901 [0 lua] "hincrby" "redisson_sortedset_lock:{redisson_priority_queue}" "43908a60-9fda-46da-bb73-1833250a4a86:1" "-1"
1599634858.713938 [0 lua] "del" "redisson_sortedset_lock:{redisson_priority_queue}"
1599634858.713955 [0 lua] "publish" "redisson_lock__channel:redisson_sortedset_lock:{redisson_priority_queue}" "0"
1599634859.738955 [0 192.168.147.1:58278] "LLEN" "redisson_priority_queue"
1599634860.745438 [0 192.168.147.1:58272] "LLEN" "redisson_priority_queue"
1599634870.067682 [0 192.168.147.1:58281] "EVAL" "if (redis.call('exists', KEYS[1]) == 0) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; return redis.call('pttl', KEYS[1]);" "1" "redisson_sortedset_lock:{redisson_priority_queue}" "30000" "43908a60-9fda-46da-bb73-1833250a4a86:1"
1599634870.067867 [0 lua] "exists" "redisson_sortedset_lock:{redisson_priority_queue}"
1599634870.067900 [0 lua] "hincrby" "redisson_sortedset_lock:{redisson_priority_queue}" "43908a60-9fda-46da-bb73-1833250a4a86:1" "1"
1599634870.067941 [0 lua] "pexpire" "redisson_sortedset_lock:{redisson_priority_queue}" "30000"
1599634870.075270 [0 192.168.147.1:58280] "GET" "{redisson_priority_queue}:redisson_sortedset_comparator"
1599634870.084189 [0 192.168.147.1:58282] "LLEN" "redisson_priority_queue"
1599634870.089818 [0 192.168.147.1:58283] "LINDEX" "redisson_priority_queue" "0"
1599634870.094181 [0 192.168.147.1:58284] "LINDEX" "redisson_priority_queue" "1"
1599634870.100092 [0 192.168.147.1:58271] "EVAL" "local len = redis.call('llen', KEYS[1]);if tonumber(ARGV[1]) < len then local pivot = redis.call('lindex', KEYS[1], ARGV[1]);redis.call('linsert', KEYS[1], 'before', pivot, ARGV[2]);return;end;redis.call('rpush', KEYS[1], ARGV[2]);" "1" "redisson_priority_queue" "1" "\xf7\x02"
1599634870.100198 [0 lua] "llen" "redisson_priority_queue"
1599634870.100217 [0 lua] "lindex" "redisson_priority_queue" "1"
1599634870.100234 [0 lua] "linsert" "redisson_priority_queue" "before" "\xf7\x03" "\xf7\x02"
1599634870.106248 [0 192.168.147.1:58285] "EVAL" "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then return nil;end; local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); if (counter > 0) then redis.call('pexpire', KEYS[1], ARGV[2]); return 0; else redis.call('del', KEYS[1]); redis.call('publish', KEYS[2], ARGV[1]); return 1; end; return nil;" "2" "redisson_sortedset_lock:{redisson_priority_queue}" "redisson_lock__channel:redisson_sortedset_lock:{redisson_priority_queue}" "0" "30000" "43908a60-9fda-46da-bb73-1833250a4a86:1"
1599634870.106358 [0 lua] "hexists" "redisson_sortedset_lock:{redisson_priority_queue}" "43908a60-9fda-46da-bb73-1833250a4a86:1"
1599634870.106380 [0 lua] "hincrby" "redisson_sortedset_lock:{redisson_priority_queue}" "43908a60-9fda-46da-bb73-1833250a4a86:1" "-1"
1599634870.106395 [0 lua] "del" "redisson_sortedset_lock:{redisson_priority_queue}"
1599634870.106404 [0 lua] "publish" "redisson_lock__channel:redisson_sortedset_lock:{redisson_priority_queue}" "0"
1599634871.132534 [0 192.168.147.1:58266] "LLEN" "redisson_priority_queue"
1599634872.138351 [0 192.168.147.1:58270] "LLEN" "redisson_priority_queue"
~~~

![](../../images/RedissonPriorityQueue实现原理.png)

实现原理：使用llen获取当前队列大小，然后根据二分查找法查找出要插入的元素的索引，然后使用linsert命令插入到指定的元素之前。

