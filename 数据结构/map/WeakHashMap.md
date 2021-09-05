https://liujiacai.net/blog/2015/09/27/java-weakhashmap/

~~~
Entry(Object key, V value,
ReferenceQueue<Object> queue,
  int hash, Entry<K,V> next) {
  //这里把key传给了父类WeakReference，说明key为弱引用（没有显式的 this.key = key）
  //所有如果key只有通过弱引用访问时，key会被 GC 清理掉
  //同时该key所代表的Entry会进入queue中，等待被处理
  //还可以看到value为强引用（有显式的 this.value = value ），但这并不影响
  //后面可以看到WeakHashMap.expungeStaleEntries方法是如何清理value的
  super(key, queue);
  this.value = value;
  this.hash  = hash;
  this.next  = next;
}
~~~

