# 简介

 Rebalance(再均衡)机制指的是：将一个Topic下的**多个队列(或称之为分区)**，在同一个消费者组(consumer group)下的**多个消费者实例**(consumer instance)之间进行重新分配。 

 **Rebalance机制本意是为了提升消息的并行处理能力。**



敏锐读者注意到了，Broker是通知每个消费者各自Rebalance，即每个消费者自己给自己重新分配队列，而不是Broker将分配好的结果告知Consumer。从这个角度，RocketMQ与Kafka Rebalance机制类似，二者Rebalance分配都是在客户端进行，不同的是：

- **Kafka：**会在消费者组的多个消费者实例中，选出一个作为Group Leader，由这个Group Leader来进行分区分配，分配结果通过Cordinator(特殊角色的broker)同步给其他消费者。相当于Kafka的分区分配只有一个大脑，就是Group Leader。
- **RocketMQ：**每个消费者，自己负责给自己分配队列，相当于每个消费者都是一个大脑。

# 分配策略

RocketMQ的分配策略使用AllocateMessageQueueStrategy接口表示，并提供了多种实现：

- AllocateMessageQueueAveragely：平均分配，**默认**

- AllocateMessageQueueAveragelyByCircle：循环分配
- AllocateMessageQueueConsistentHash：一致性哈希
- AllocateMessageQueueByConfig：根据配置进行分配
- AllocateMessageQueueByMachineRoom：根据机房
- AllocateMachineRoomNearby：就近分配

