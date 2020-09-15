# 一、内部编码

列表类型的内部编码有两种：

- ziplist（压缩列表）
- linkedlist（链表）

## 1.1. ziplist

当列表的元素个数小于`list-max-ziplist-entries`配置（默认512个），同时列表中每个元素的值都小于`list-max-ziplist-value`配置时（默认64字节），Redis会选用ziplist来作为列表的内部实现来减少内存的使用。

## 1.2. linkedlist

当列表类型无法满足ziplist的条件时，Redis会使用linkedlist作为列表的内部实现。

