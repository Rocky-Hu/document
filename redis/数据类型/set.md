# 一、内部编码

集合类型的内部编码有两种：

- intset（整数集合）
- hashtable（哈希表）

## 1.1. intset

当集合中的元素都是整数且元素个数小于set-max-intset-entries配置（默认512）个时，Redis会选用intset来作为集合的内部实现，从而减少内存的使用。

## 1.2. hashtable

当集合类型无法满足intset的条件时，Redis会使用hashtable作为集合的内部实现。























