type意味着类型，这里的type官方全称是“join type”，意思是“连接类型”,这样很容易给人一种错觉觉得必须需要俩个表以上才有连接类型。事实上这里的连接类型并非字面那样的狭隘，它更确切的说是一种数据库引擎查找表的一种方式，在《高性能mysql》一书中作者更是觉得称呼它为访问类型更贴切一些。

# system

该表只有一行（=系统表）。

# const

表最多有一个匹配行，在查询开始时读取。因为只有一行，所以优化器的其余部分可以将此行中列的值视为常量。const表非常快，因为它们只读取一次。

将主键或唯一索引的所有部分与常量值进行比较时使用const。在以下查询中，tbl_name可以用作常量表：

~~~sql
SELECT * FROM tbl_name WHERE primary_key=1;

SELECT * FROM tbl_name WHERE primary_key_part1=1 AND primary_key_part2=2;
~~~

# eq_ref

对于前面表中的每一行组合，从这个表中读取一行。与system和const类型不同，这是最好的连接类型。当一个索引的所有部分都被连接使用，并且索引是一个主键或唯一的NOT NULL索引时，就会使用它。

eq_ref可以用于使用=操作符进行比较的索引列。比较值可以是一个常量，也可以是使用在此表之前读取的表中的列的表达式。在下面的例子中，MySQL可以使用eq_ref join来处理ref_table:

~~~sql
SELECT * FROM ref_table,other_table
  WHERE ref_table.key_column=other_table.column;

SELECT * FROM ref_table,other_table
  WHERE ref_table.key_column_part1=other_table.column
  AND ref_table.key_column_part2=1;
~~~

# ref

对于前面表中的每个行组合，将从这个表中读取具有匹配索引值的所有行。如果连接仅使用键的最左边的前缀，或者键不是主键或唯一索引(换句话说，如果连接不能根据键值选择单行)，则使用ref。如果所使用的键只匹配几行，那么这是一种很好的连接类型。

ref可以用于使用=或<=>操作符进行比较的索引列。在下面的例子中，MySQL可以使用一个ref join来处理ref_table:

~~~sql
SELECT * FROM ref_table WHERE key_column=expr;

SELECT * FROM ref_table,other_table
  WHERE ref_table.key_column=other_table.column;

SELECT * FROM ref_table,other_table
  WHERE ref_table.key_column_part1=other_table.column
  AND ref_table.key_column_part2=1;
~~~

# fulltext

连接是使用全文索引执行的。

# ref_or_null

这种连接类型类似于ref，但是MySQL会对包含空值的行进行额外的搜索。这种连接类型优化最常用于解析子查询。在下面的例子中，MySQL可以使用ref_or_null连接来处理ref_table:

~~~sql
SELECT * FROM ref_table
  WHERE key_column=expr OR key_column IS NULL;
~~~

# index_merge

这种连接类型表明使用了索引合并优化。在这种情况下，输出行中的key列包含使用的索引列表，而key_len包含所使用索引的最长键部分列表。

# unique_subquery

这种类型替换了以下形式的子查询中的一些eq_ref:

~~~sql
value IN (SELECT primary_key FROM single_table WHERE some_expr)
~~~

unique_subquery只是一个索引查找函数，它完全取代了子查询，以获得更好的效率。

# index_subquery

这种连接类型类似于unique_subquery。它在子查询中替换，但它适用于以下形式的子查询中的非唯一索引:

~~~sql
value IN (SELECT key_column FROM single_table WHERE some_expr)
~~~

# range

只检索给定范围内的行，并使用索引来选择行。输出行中的key列指示使用了哪个索引。key_len包含所使用的最长的键部分。对于这种类型，ref列为NULL。

range可用于使用任意=、<>、>、>=、<、<=、NULL、<=>、BETWEEN、LIKE或IN()操作符将键列与常量进行比较:

~~~sql
SELECT * FROM tbl_name
  WHERE key_column = 10;

SELECT * FROM tbl_name
  WHERE key_column BETWEEN 10 and 20;

SELECT * FROM tbl_name
  WHERE key_column IN (10,20,30);

SELECT * FROM tbl_name
  WHERE key_part1 = 10 AND key_part2 IN (10,20,30);
~~~

# index

索引联接类型与ALL相同，只是扫描了索引树。这有两种方式：

- 查询所用到的是覆盖索引，可以满足从表中获取所有的所需的数据，则只扫描索引树。在这种情况下，Extra列的内容为Using index。仅索引扫描通常比全部扫描快，因为索引的大小通常小于表数据。
- 使用从索引读取数据以按索引顺序查找数据行来执行全表扫描。使用索引不会出现在Extra列中。

当查询只使用作为单个索引一部分的列时，MySQL可以使用这种连接类型。

# ALL

对前一个表中的每一行组合都执行一次完整的表扫描。如果表是第一个没有标记const的表，那么这通常是不好的，在所有其他情况下通常是非常糟糕的。通常，您可以通过添加索引来避免ALL，这些索引允许基于先前表中的常量值或列值从表中检索行。

