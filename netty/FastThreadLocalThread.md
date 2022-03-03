# FastThreadLocalThread

# FastThreadLocal

# InternalThreadLocalMap

回顾TheadLocal，它使用 ThreadLocalMap 存储数据，而 ThreadLocalMap 底层采用**线性探测法**解决 Hash 冲突问题，在空间和时间上寻求平衡。但 Netty 对这样的平衡并不满意，因此重新设计，使用 InternalThreadLocalMap 存储数据。核心思想是以空间换时间。InternalThreadLocalMap 是 UnpaddedInternalThreadLocalMap 对象的子类，之前也说过，可以把 UnpaddedInternalThreadLocalMap 当成一个数据结构，这里主要用到  nextIndex 和 Object[] 两个变量存储相应数据。那肯定有人会问不通过Hash算法怎么确定位置呢? 这就是很有意思的一点，这里就体现变量 nextIndex 的作用了: 每创建一个 FastThreadLocal 对象就从 InternalTheadLocalMap#getAndIncrement() （即 nextIndex 对象）方法获取索引值并保存在 FastThreadLocal#index 变量中，这个索引对应 Object[] 下标对应，通过索引值就能获取 FastThreadLocal 对象保存的值，这对于频繁获取值是非常高效的。
