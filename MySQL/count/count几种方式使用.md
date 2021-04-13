https://learnsql.com/blog/difference-between-count-distinct/#:~:text=The%20simple%20answer%20is%20no,COUNT(1)%20are%20identical

https://dev.mysql.com/doc/refman/5.6/en/aggregate-functions.html#function_count

https://bbs.huaweicloud.com/blogs/180470

https://www.yisu.com/zixun/254.html

https://www.zhangshengrong.com/p/O3aAKnKX4E/

https://www.drupal.org/project/boost/issues/1055344

[`COUNT(*)`](https://dev.mysql.com/doc/refman/5.6/en/aggregate-functions.html#function_count) is somewhat different in that it returns a count of the number of rows retrieved, whether or not they contain `NULL` values.

To process a `SELECT COUNT(*)` statement, `InnoDB` scans an index of the table, which takes some time if the index is not entirely in the buffer pool. 

For `MyISAM` tables, [`COUNT(*)`](https://dev.mysql.com/doc/refman/5.6/en/aggregate-functions.html#function_count) is optimized to return very quickly if the [`SELECT`](https://dev.mysql.com/doc/refman/5.6/en/select.html) retrieves from one table, no other columns are retrieved, and there is no `WHERE` clause. For example:

~~~sql
mysql> SELECT COUNT(*) FROM student;
~~~

This optimization only applies to `MyISAM` tables, because an exact row count is stored for this storage engine and can be accessed very quickly. `COUNT(1)` is only subject to the same optimization if the first column is defined as `NOT NULL`.

# count(*)和count(1)哪个性能更好

`InnoDB` handles `SELECT COUNT(*)` and `SELECT COUNT(1)` operations in the same way. There is no performance difference.

