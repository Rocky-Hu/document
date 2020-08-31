负载均衡策略：

~~~
/**
 * 负载均衡策略
 */
public interface LoadBalancerStrategy {

    T choose(List candidates);

}
~~~



# 1. 随机算法

## 1.1. 算法原理

实现原理：获取服务提供者列表大小区间之间的随机数，作为服务提供者列表的索引来获取服务。

## 1.2. 算法实现

~~~
/**
 * 负载均衡策略 - 随机算法
 *
 * 实现原理：获取服务提供者列表大小区间之间的随机数，作为服务提供者列表的索引来获取服务
 */
public class RandomLoadBalancerStrategy<T> implements LoadBalancerStrategy<T> {

    @Override
    public T choose(List<T> candidates) {
        Random random = new Random();
        int i = random.nextInt(candidates.size());
        return candidates.get(i);
    }

}
~~~

# 2. 加权随机算法

## 2.1. 算法原理

加权随机算法在随机算法的基础上针对权重做了处理。

## 2.2. 算法实现

~~~
package org.distributed.loadbalancer.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 负载均衡策略 - 加权随机算法
 *
 * 实现原理：首先根据加权数放大服务提供者列表，比如服务提供者A加权数为3，放大之后变为A，A，A，存放在新的服务提供者列表，然后对新的服务提供者列表应用随机算法。
 */
public class WeightRandomLoadBalancerStrategy implements LoadBalancerStrategy<Server> {

    @Override
    public Server choose(List<Server> candidates) {
        // 存放加权后的服务提供者列表
        List newCandidates = new ArrayList<>();
        for (Server server : candidates) {
            int weight = server.getWeight();
            for (int i= 0; i < weight; i++) {
                Server server1 = new Server();
                server1.setUrl(server.getUrl());
                server1.setWeight(server.getWeight());
                newCandidates.add(server1);
            }
        }

        Random random = new Random();
        int i = random.nextInt(newCandidates.size());
        return newCandidates.get(i);
    }

}
~~~

# 3. 轮询算法

## 3.1. 算法原理

## 3.2. 算法实现

~~~
/**
 * 负载均衡策略 - 轮询算法
 *
 * 实现原理：列表索引循环。
 * @param 
 */
public class RoundRobinLoadBalancerStrategy<T> implements LoadBalancerStrategy<T> {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public T choose(List<T> candidates) {
        int position = Math.abs(atomicInteger.getAndIncrement());
        return candidates.get(position % candidates.size());
    }

}
~~~

# 4. 加权轮询算法

## 4.1. 算法原理

上面轮询法不会关注服务器的性能和负载承受能力，所以在此基础上提出了加权轮询来解决平均分配请求带来的问题。主要的思想就是权重分配高的服务器处理更多的请求。

算法步骤：

1. 对于每个请求，遍历集群中的所有可用后端，对于每个后端peer执行：peer->current_weight += peer->effecitve_weight

   同时累加所有peer的effective_weight，保存为total。

2. 集群中选出current_weight最大的peer，作为本次选定的后端；
3. 于本次选定的后端，执行：peer->current_weight -= total

## 4.2. 算法实现

~~~
public class Server {

    private String url;

    // 配置文件中指定的该后端的权重，这个值是固定不变的
    private int weight;
    // 有效权重。初始值为weight。这个变量的引入主要是处理节点异常，当节点出现异常时需要降低其权重。
    private int effectiveWeight;
    // 当前权重。一开始为0，之后会动态调整。
    private int currentWeight = 0;

    public Server() {
    }

    public Server(String url) {
        this.url = url;
    }

    public Server(String url, int weight) {
        this.url = url;
        this.weight = weight;
        this.effectiveWeight = weight;// 设置初始值为weight
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getEffectiveWeight() {
        return effectiveWeight;
    }

    public void setEffectiveWeight(int effectiveWeight) {
        this.effectiveWeight = effectiveWeight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    @Override
    public String toString() {
        return "Server{" +
                "url='" + url + '\'' +
                '}';
    }

}
~~~

~~~
/**
 * 负载均衡策略 - 平滑加权轮询算法
 * @param
 */
public class SmoothWeightedRoundRobinLoadBalancerStrategy implements LoadBalancerStrategy<Server> {

    public Server choose(List candidates) {

        int totalWeight = 0;
        int maxCurrent = Integer.MIN_VALUE;

        Server selectedServer = null;

        for (Server server : candidates) {

            // 获取节点的相关权重
            int weight = server.getWeight();
            int currentWeight = server.getCurrentWeight();
            int effectiveWeight = server.getEffectiveWeight();

            // 计算当前权重
            currentWeight = currentWeight + effectiveWeight;
            server.setCurrentWeight(currentWeight);

            // 从所有节点中选出currentWeight最大的节点，作为本次选中的节点
            if (currentWeight > maxCurrent) {
                maxCurrent = currentWeight;
                selectedServer = server;
            }

            // 累加所有节点的effectiveWeight
            totalWeight += effectiveWeight;
        }

        if (selectedServer != null) {
            // 被选择的节点的当前权重做减totalWeight操作
            selectedServer.setCurrentWeight(selectedServer.getCurrentWeight() - totalWeight);
            return selectedServer;
        }

        return candidates.get(0);
    }

}
~~~

# 5. 源地址Hash算法

## 5.1. 算法原理

## 5.2. 算法实现

~~~
/**
 * 负载均衡策略 - 源地址哈希算法
 */
public class SourceHashingLoadBalancerStrategy<T> implements LoadBalancerStrategy<T> {

    @Override
    public T choose(List<T> candidates) {
        return null;
    }

    public T choose(List<T> candidates, String ip) {
        int hashCode = ip.hashCode();
        int pos = hashCode % candidates.size();
        return candidates.get(pos);
    }

}
~~~

# 6. 一致性Hash算法

