# 一、distinct某列，同时返回其他列

在实际的开发中有这样的需求，select选择多列，同时需要某列是不重复的，于是就想到了distinct关键字。看下面示例。

示例表结构：

~~~sql
mysql> desc tb_distinct_data;
+-------+--------------+------+-----+---------+-------+
| Field | Type         | Null | Key | Default | Extra |
+-------+--------------+------+-----+---------+-------+
| id    | int(11)      | NO   | PRI | NULL    |       |
| name  | varchar(255) | NO   |     | NULL    |       |
+-------+--------------+------+-----+---------+-------+
2 rows in set (0.00 sec)
~~~

表中数据：

~~~sql
mysql> select * from tb_distinct_data;
+----+------+
| id | name |
+----+------+
|  1 | a    |
|  2 | b    |
|  3 | c    |
|  4 | c    |
|  5 | b    |
+----+------+
5 rows in set (0.00 sec)
~~~

查询语句：

~~~sql
select distinct name, id from tb_distinct_data
~~~

查询结果：

~~~sql
mysql> select distinct name, id from tb_distinct_data;
+------+----+
| name | id |
+------+----+
| a    |  1 |
| b    |  2 |
| c    |  3 |
| c    |  4 |
| b    |  5 |
+------+----+
5 rows in set (0.00 sec)
~~~

我们的本意是希望name返回的数据是不重复的，但是因为同时选择了其他列。distinct作用的就是多列，也就是只有name和id同时相同才认为是重复的。

如果希望就是根据name来判断是否重复，同时返回其他列，而不关心其他列的取值，可以这样操作：

~~~sql
mysql> select distinct name, id from tb_distinct_data group by name;
+------+----+
| name | id |
+------+----+
| a    |  1 |
| b    |  2 |
| c    |  3 |
+------+----+
3 rows in set (0.00 sec)
~~~



