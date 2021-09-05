# 一、原理

## 1.1. accessOrder = false

~~~java
public class LinkedHashMapTest {

    public static void main(String[] args) {
        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("a", 1);
        linkedHashMap.put("b", 2);
        linkedHashMap.put("c", 3);
        linkedHashMap.put("d", 4);
        linkedHashMap.put("e", 5);
        linkedHashMap.put("aaaaa", 6);
        linkedHashMap.put("f", 7);
        linkedHashMap.put("ee", 8);

        Set<String> keySet = linkedHashMap.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

}
~~~

### put

![](../images/LinkedHashMap.png)

## 1.2. accessOrder = true

~~~java
public class LinkedHashMapTest {

    public static void main(String[] args) {
        LinkedHashMap<String, Integer> linkedHashMap1 = new LinkedHashMap<String, Integer>(16, 0.75f, true);
        linkedHashMap1.put("a", 1);
        linkedHashMap1.put("b", 2);
        linkedHashMap1.put("c", 3);
        linkedHashMap1.put("d", 4);
        linkedHashMap1.put("e", 5);
        linkedHashMap1.put("aaaaa", 6);
        linkedHashMap1.put("f", 7);
        linkedHashMap1.put("aaaaa", 6);

        Set<Map.Entry<String, Integer>> entrySet = linkedHashMap1.entrySet();
        Iterator<Map.Entry<String, Integer>> it1 = entrySet.iterator();
        while (it1.hasNext()) {
            Map.Entry<String, Integer> entry = it1.next();
            System.out.println(entry.getKey() + "=>" + entry.getValue());
        }
    }

}
~~~



### put

![](/Users/rocky/Work/project/document/images/LinkedHashMap_访问顺序_put.png)

# 二、扩展方法

## 2.1. afterNodeAccess

accessOrder为true，会执行afterNodeAccess方法，afterNodeAccess方法负责将访问的元素设置为双向队列的队尾节点。

## 2.2. afterNodeRemoval

~~~java
void afterNodeRemoval(Node<K,V> e) { // unlink
    LinkedHashMap.Entry<K,V> p =
        (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
    p.before = p.after = null;
    if (b == null)
        head = a;
    else
        b.after = a;
    if (a == null)
        tail = b;
    else
        a.before = b;
}
~~~

## 2.3. afterNodeInsertion

~~~java
void afterNodeInsertion(boolean evict) { // possibly remove eldest
    LinkedHashMap.Entry<K,V> first;
    if (evict && (first = head) != null && removeEldestEntry(first)) {
        K key = first.key;
        removeNode(hash(key), key, null, false, true);
    }
}
~~~

# 三、LRU

~~~java
public class LRUCache<K,V> extends LinkedHashMap<K,V> {
    
  private int cacheSize;
  
  public LRUCache(int cacheSize) {
      super(16,0.75f,true);
      this.cacheSize = cacheSize;
  }

  /**
   * 判断元素个数是否超过缓存容量
   */
  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      return size() > cacheSize;
  }
}
~~~



