# 一、内部编码

有序集合类型的内部编码有两种：

- ziplist（压缩列表）
- skiplist（跳跃表）

## 1.1. ziplist

当有序集合的元素个数小于`zset-max-ziplist-entries`配置（默认128个），同时每个元素的值小于`zset-max-ziplist-value`配置（默认64字节）时，Redis会用ziplist来作为有序集合的内部实现，ziplist可以有效减少内存的使用。

## 1.2. skiplist

当ziplist条件不满足时，有序集合会使用skiplist作为内部实现，因为此时ziplist的读写效率会下降。





















