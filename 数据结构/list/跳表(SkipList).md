# 一、介绍

## 1.1. 动机

如果一个集合里的元素是有序的，那么我们可以利用数组的随机访问 (Random Access， 即通过下标 index 来访问元素) 性质，或者我们可以通过二叉树的二分结构性质来使用二分查找法。

**那么我们可以在单向链表中使用二分查找法吗？**

**答案是不能。**

因为在一般的链表当中，我们没有办法**直接访问**到中间的元素，因此无法使用二分查找法。然而，我们可以通过改变链表的结构，给链表加上不同的层次来达到二分查找的效果。这种改变结构的加强版链表，我们称之为**跳跃链表** (Skip List)。

跳表是一个随机化的数据结构，实质就是一种**可以进行二分查找的有序链表**。跳表在原有的有序链表上面增加了多级索引，通过索引来实现快速查找。跳表不仅能提高搜索性能，同时也可以提高插入和删除操作的性能。

## 1.2. 跳跃表的构造特征

- 一个跳跃表应该有若干个层（Level）链表组成；
- 跳跃表中最底层的链表包含所有数据； 每一层链表中的数据都是有序的；
- 如果一个元素X出现在第i层，那么编号比 i 小的层都包含元素X；
- 第 i 层的元素通过一个指针指向下一层拥有相同值的元素；
- 在每一层中，-∞ 和 +∞两个元素都出现(分别表示INT_MIN 和 INT_MAX)；
- 头指针（head）指向最高一层的第一个元素；

## 1.3. 算法复杂度

O(logN)

# 二、实现

~~~java
import java.util.Random;

public class SkipList {

    public SkipListEntry head;// First element of the top level
    public SkipListEntry tail;// Last element of the top level

    public int n;// number of entries in the Skip list

    public int h;// Height
    public Random r;// Coin toss

    /* ----------------------------------------------
     Constructor: empty skiplist

                          null        null
                           ^           ^
                           |           |
     head --->  null <-- -inf <----> +inf --> null
                           |           |
                           v           v
                          null        null
     ---------------------------------------------- */
    public SkipList() {// Default constructor...
        SkipListEntry p1, p2;

        p1 = new SkipListEntry(SkipListEntry.negInf, null);
        p2 = new SkipListEntry(SkipListEntry.posInf, null);

        head = p1;
        tail = p2;

        p1.right = p2;
        p2.left = p1;

        n = 0;
        h = 0;

        r = new Random();
    }

    /** Returns the value associated with a key. */
    public Integer get (String k) {
        SkipListEntry p;
        p = findEntry(k);

        if ( k.equals( p.key ) )
            return(p.value);
        else
            return(null);
    }


    /* ------------------------------------------------------
    findEntry(k): find the largest key x <= k
          on the LOWEST level of the Skip List
    ------------------------------------------------------ */
    private SkipListEntry findEntry(String k) {
        SkipListEntry p;

        /* -----------------
           Start at "head"
           ----------------- */
        p = head;

        while (true) {
            /* --------------------------------------------
               Search RIGHT until you find a LARGER entry

                   E.g.: k = 34

                             10 ---> 20 ---> 30 ---> 40
                                              ^
                                              |
                                              p stops here
                p.right.key = 40
               -------------------------------------------- */
            while (p.right.key != SkipListEntry.posInf && p.right.key.compareTo(k) <= 0) {
                p = p.right;
                // System.out.println(">>>> " + p.key);
            }

            /* ---------------------------------
	            Go down one level if you can...
	           --------------------------------- */
            if (p.down != null) {
                p = p.down;
                // System.out.println("vvvv " + p.key);
            } else {
                break;// We reached the LOWEST level... Exit...
            }

        }

        return (p);// p.key <= k
    }

    /** Put a key-value pair in the map, replacing previous one if it exists. */
    public Integer put(String k, Integer v) {
        SkipListEntry p, q;
        int i;

        p = findEntry(k);
        //   System.out.println("findEntry(" + k + ") returns: " + p.key);

        /* ------------------------
            Check if key is found
           ------------------------ */
        if (k.equals(p.getKey())) {
            Integer old = p.value;
            p.value = v;
            return (old);
        }

        /* ------------------------
            Insert new entry (k,v)
           ------------------------ */
        q = new SkipListEntry(k, v);
        q.left = p;
        q.right = p.right;
        p.right.left = q;
        p.right = q;

        i = 0;// Current level = 0

        while (r.nextDouble() < 0.5) {

            // Coin flip success: make one more level....
            //	System.out.println("i = " + i + ", h = " + h );

            /* ---------------------------------------------
               Check if height exceed current height.
               If so, make a new EMPTY level
               --------------------------------------------- */
            if (i >= h) {
                SkipListEntry p1, p2;
                h = h+1;

                p1 = new SkipListEntry(SkipListEntry.negInf,null);
                p2 = new SkipListEntry(SkipListEntry.posInf,null);

                p1.right = p2;
                p1.down = head;

                p2.left = p1;
                p2.down = tail;

                head.up = p1;
                tail.up = p2;

                head = p1;
                tail = p2;
            }

            /* -------------------------
               Scan backwards...
               ------------------------- */
            while (p.up == null) {
                // System.out.print(".");
                p = p.left;
            }

            //	System.out.print("1 ");
            p = p.up;

            /* ---------------------------------------------
               Add one more (k,v) to the column
               --------------------------------------------- */
            SkipListEntry e;
            e = new SkipListEntry(k, null);// Don't need the value...

            /* ---------------------------------------
               Initialize links of e
               --------------------------------------- */
            e.left = p;
            e.right = p.right;
            e.down = q;

            /* ---------------------------------------
               Change the neighboring links..
               --------------------------------------- */
            p.right.left = e;
            p.right = e;
            q.up = e;

            q = e;// Set q up for the next iteration

            i = i+1;// Current level increased by 1
        }

        n = n + 1;
        return (null);
    }

    public Integer remove(String key) {
        SkipListEntry p, q;

        p = findEntry(key);

        if(!p.key.equals(key)) {
            return null;
        }

        Integer oldValue = p.value;
        while(p != null) {
            q = p.up;
            p.left.right = p.right;
            p.right.left = p.left;
            p = q;
        }

        return oldValue;
    }

    private static class SkipListEntry {
        public String key;
        public Integer value;

        public int pos;// I added this to print the skiplist "nicely"

        public SkipListEntry up, down, left, right;

        public static String negInf = new String("-oo");// -inf key value
        public static String posInf = new String("+oo");// +inf key value

        public SkipListEntry(String k, Integer v) {
            key = k;
            value = v;

            up = down = left = right = null;
        }

        public Integer getValue() {
            return value;
        }

        public String getKey() {
            return key;
        }

        public Integer setValue(Integer val) {
            Integer oldValue = value;
            value = val;
            return oldValue;
        }

        public boolean equals(Object o) {
            SkipListEntry ent;

            try {
                ent = (SkipListEntry) o;    // Test if o is a SkipListEntry...
            }
            catch (ClassCastException ex) {
                return false;
            }

            return (ent.getKey() == key) && (ent.getValue() == value);
        }

        public String toString() {
            return "(" + key + "," + value + ")";
        }
    }

}
~~~

参考：

https://people.ok.ubc.ca/ylucet/DS/SkipList.html

http://www.mathcs.emory.edu/~cheung/Courses/323/Syllabus/Map/skip-list-impl.html

https://blog.csdn.net/DERRANTCM/article/details/79063312

