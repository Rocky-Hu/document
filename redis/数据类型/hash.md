# 一、内部编码

哈希类型的内部编码有两种：

- ziplist（压缩列表）
- hashtable（哈希表）

## 1.1. ziplist

当哈希类型元素个数小于`hash-max-ziplist-entries`配置（默认512）、同时所有值都小于`hash-max-ziplist-value`配置（默认64字节）时，Redis会使用ziplist作为哈希的内部实现，ziplist使用更加紧凑的结构实现多个元素连续存储，所以在节省内存方面比hashtable更加优秀。

## 1.2. hashtable

当哈希类型无法满足ziplist的条件时，Redis会使用hashtable作为哈希的内部实现，因此此时ziplist的读写效率会下降，而hashtable的读写时间复杂度为O(1)。

