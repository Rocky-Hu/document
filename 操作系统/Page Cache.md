Linux系统中为了减少对磁盘的IO操作，会将打开的磁盘内容进行缓存，而缓存的地方则是物理内存，进而将对磁盘的访问转换成对内存的访问，有效提高程序的速度。Linux的缓存方式是利用物理内存缓存磁盘上的内容，称为页缓存（page cache）。

页缓存是由内存中的物理页面组成的，其内容对应磁盘上的物理块。页缓存的大小会根据系统的内存空闲大小进行动态调整，它可以通过占用内存以扩张大小，也可以自我收缩以缓解内存使用压力。

在虚拟内存机制出现以前，操作系统使用块缓存系列，但是在虚拟内存出现以后，操作系统管理IO的粒度更大，因此采用了页缓存机制，页缓存是基于页的、面向文件的缓存机制。

~~~
The page cache is the main disk cache used by the Linux kernel. In most cases, the kernel refers to the page cache when reading from or writing to disk. New pages are added to the page cache to satisfy User Mode processes’s read requests. If the page is not already in the cache, a new entry is added to the cache and filled with the data read from the disk. If there is enough free memory, the page is kept in the cache for an indefinite period of time and can then be reused by other processes without accessing the disk.

Similarly, before writing a page of data to a block device, the kernel verifies whether the corresponding page is already included in the cache; if not, a new entry is added to the cache and filled with the data to be written on disk. The I/O data transfer does not start immediately: the disk update is delayed for a few seconds, thus giving a chance to the processes to further modify the data to be written (in other words, the kernel implements deferred write operations).
~~~

### 页缓存的读取

Linux系统在读取文件时，会优先从页缓存中读取文件内容，如果页缓存不存在，系统会先从磁盘中读取文件内容更新到页缓存中，然后再从页缓存中读取文件内容并返回。大致过程如下：

1. 进程调用库函数read发起读取文件请求
2. 内核检查已打开的文件列表，调用文件系统提供的read接口
3. 找到文件对应的inode，然后计算出要读取的具体的页
4. 通过inode查找对应的页缓存，1）如果页缓存节点命中，则直接返回文件内容；2）如果没有对应的页缓存，则会产生一个缺页异常（page fault）。这时系统会创建新的空的页缓存并从磁盘中读取文件内容，更新页缓存，然后重复第4步
5. 读取文件返回

所以说，所有的文件内容的读取，无论最初有没有命中页缓存，最终都是直接来源于页缓存。

### 页缓存的写入

因为页缓存的存在，当一个进程调用write时，对文件的更新仅仅是被写到了文件的页缓存中，让后将对应的页标记为dirty，整个过程就结束了。Linux内核会在周期性地将脏页写回到磁盘，然后清理掉dirty标识。

由于写操作只会把变更写入页缓存，因此进程并不会因此为阻塞直到磁盘IO发生，如果此时计算机崩溃，写操作的变更可能并没有发生在磁盘上。所以对于一些要求比较严格的写操作，比如数据系统，就需要主动调用fsync等操作及时将变更同步到磁盘上。读操作则不同，read通常会阻塞直到进程读取到数据，而为了减少读操作的这种延迟，Linux系统还是用了“预读”的技术，即从磁盘中读取数据时，内核将会多读取一些页到页缓存中。

#### 回写线程

页缓存的回写是由内核中的单独的线程来完成的，回写线程会在以下3种情况下进行回写：

1. 空闲内存低于阈值时。当空闲内存不足时，需要释放掉一部分缓存，由于只有不脏的页才能被释放，所以需要把脏页都回写到磁盘，使其变为可回收的干净的页。
2. 脏页在内存中处理时间超过阈值时。这是为了确保脏页不会无限期的留在内存中，减少数据丢失的风险。
3. 当用户进程调用sync和fsync系统调用时。这是为了给用户进程提供强制回写的方法，满足回写要求严格的使用场景。