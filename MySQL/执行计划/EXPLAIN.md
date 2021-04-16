![](../../images/mysql/explain.png)

explain作用：

查看sql的执行计划，帮助我们分析mysql是如何解析sql语句的。

- 查看表的加载顺序
- 查看sql的查询类型
- 哪些索引可能被使用，哪些索引又实际使用了。
- 表之间的引用关系。
- 一个表中有多少行被优化器查询。
- 其他额外的辅助信息。

# 一、Explain输出列

EXPLAIN的每个输出行提供了关于一个表的信息。

| 列            | JSON名称      | 含义                                           |
| ------------- | ------------- | ---------------------------------------------- |
| id            | select_id     | The SELECT identifier                          |
| select_type   | None          | The SELECT type                                |
| table         | table_name    | The table for the output row                   |
| partitions    | partitions    | The matching partitions                        |
| type          | access_type   | The join type                                  |
| possible_keys | possible_keys | The possible indexes to choose                 |
| key           | key           | The index actually chosen                      |
| key_len       | key_length    | The length of the chosen key                   |
| ref           | ref           | The columns compared to the index              |
| rows          | rows          | Estimate of rows to be examined                |
| filtered      | filtered      | Percentage of rows filtered by table condition |
| Extra         | None          | Additional information                         |

## 1.1. id

含义：select查询的序列号，是一组数字，表示的是查找中执行select子句或者是操作表的顺序。

>  **我的理解是SQL执行的顺序的标识，SQL从大到小的执行**

id的情况有三种，分别是：

- id相同表示加载表的顺序是从上到下；
- id不同id值越大，优先级越高，越先被执行；
- id有相同，也有不通，同时存在。id相同的可以认为是一组，从上往下顺序执行；在所有的组中，id的值越大，优先级越高，越先执行。



## 1.2. select_type

参考：https://www.cnblogs.com/danhuangpai/p/8475458.html

select的类型（查询中每个select子句的类型）。可能的值如下表所示：

| select_type值        | JSON Name                  | 含义                                                         |
| -------------------- | -------------------------- | ------------------------------------------------------------ |
| SIMPLE               | None                       | Simple SELECT（not using UNION or subqueries）               |
| PRIMARY              | None                       | Outermost（最外面的） SELECT                                 |
| UNION                | None                       | Second or later SELECT statement in a UNION                  |
| DEPENDENT UNION      | dependent（true）          | Second or later SELECT statement in a UNION, dependent on outer query |
| UNION RESULT         | union_result               | Result of a UNION                                            |
| SUBQUERY             | None                       | First SELECT in subquery                                     |
| DEPENDENT SUBQUERY   | dependent(true)            | First SELECT in subquery, dependent on outer query           |
| DERIVED              | None                       | Derived table                                                |
| MATERIALIZED         | materialized_from_subquery | Materialized subquery                                        |
| UNCACHEABLE SUBQUERY | cacheable (false)          | A subquery for which the result cannot be cached and must be re-evaluated for each row of the outer query |
| UNCACHEABLE UNION    | cacheable (false)          | The second or later select in a UNION that belongs to an uncacheable subquery (see UNCACHEABLE SUBQUERY) |

### MATERIALIZED

It means that the result of a subquery was saved as a virtual temporary  table instead of executing it for each row. This was introduced in MySQL 5.7 and speeds up some queries that were super slow before due to the  fact the result of their subquery parts wasn't cached.

~~~
MySQL引入了Materialization（物化）这一关键特性用于子查询（比如在IN/NOT IN子查询以及 FROM 子查询）优化。 

具体实现方式是：在SQL执行过程中，第一次需要子查询结果时执行子查询并将子查询的结果保存为临时表 ，后续对子查询结果集的访问将直接通过临时表获得。 

与此同时，优化器还具有延迟物化子查询的能力，先通过其它条件判断子查询是否真的需要执行。物化子查询优化SQL执行的关键点在于对子查询只需要执行一次。 与之相对的执行方式是对外表的每一行都对子查询进行调用，其执行计划中的查询类型为“DEPENDENT SUBQUERY”。 
~~~

## 1.3. table

输出行所指向的表的名称（显示这一步所访问数据库中表名称（显示这一行的数据是关于哪张表的），有时不是真实的表名字，可能是简称）。可能的取值如下：

- <unionM,N>:The row refers to the union of the rows with id values of M and N.
- <derivedN>: The row refers to the derived table result for the row with an id value of N. A derived table may result, for example, from a subquery in the FROM clause.
- <subqueryN>:The row refers to the result of a materialized subquery for the row with an id value of N.

## 1.4. partitions

The partitions from which records would be matched by the query. The value is NULL for nonpartitoned tables.

## 1.5. type

The join type（对表访问方式，表示MySQL在表中找到所需行的方式，又称“访问类型”。）. 

The type column of EXPLAIN output describes how tables are joined. The following list describes the join types, ordered from the best type to the worst:

- system

  The table has only one row (=system table). This is a special case of the const join type.

- const

  The table has at most one matching row, which is read at the start of the query. Because there is only one row, values from the column in this row can be regarded as constants by the rest of the optimizer. const tables are very fast because they are read only once.

- eq_ref

  One row is read from this table for each combination of rows from the previous tables. Other than the system and const types, this is the best possible join type. It is used when all parts of an index are used by the join and the index is a PRIMARY KEY or UNIQUE NOT NULL index.

- ref

  All rows with matching index values are read from this table for each combination of rows from the previous tables. ref is used if the join uses only a leftmost prefix of the key or if the key is not a PRIMARY KEY or UNIQUE index（in other words, if the join cannot select a single row based on the key value）. If the key that is used matches only a few rows, this is a good join type.

  查找条件列使用了索引而且不为主键和unique。其实，意思就是虽然使用了索引，但该索引列的值并不唯一，有重复。这样即使使用索引快速查找到了第一条数据，仍然不能停止，要进行目标值附近的小范围扫描。但它的好处是它并不需要扫全表，因为索引是有序的，即便有重复值，也是在一个非常小的范围内扫描。

- fulltext

  The join is performed using a FULLTEXT index.

- ref_or_null

  The join type is like ref, but with the addition that MySQL does an extra search for rows that contain NULL values. This join type optimization is used most often in resolving subqueries.

- index_merge

  This join type indicates that the Index Merge optimization is used. In this case, the key column in the output row contains a list of indexes used, and key_len contains a list of the longest key parts for the indexes used.

- unique_subquery

  This type replaces eq_ref for some IN subqueries of the following form:

  ~~~sql
  value IN (SELECT primary_key FROM single_table WHERE some_expr)
  ~~~

  unique_subquery is just an index lookup function that replaces the subquery completely for better efficiency. 

- index_subquery

  The join type is similar to unique_subquery. It replaces IN subqueries, but it works for nonunique indexes in subqueries of the following form:

  ~~~sql
  value IN (SELECT key_column FROM single_table WHERE some_expr)
  ~~~

- range

  Only rows that are in a given range are retrieved, using an index to select the rows. The key column in the output row indicates which index is used. The key_len contains the longest key part that was used. The ref column is NULL for this type.

  有范围的索引扫描，相对于index的全表扫描，他有范围限制，因此要优于index。

  range can be used when a key column is compared to a constant using any of the =, <>, >, >=, <, <=, IS NULL, <=>, BETWEEN, LIKE, or IN() operators: 

  > ```sql
  > SELECT * FROM tbl_name
  >   WHERE key_column = 10;
  > 
  > SELECT * FROM tbl_name
  >   WHERE key_column BETWEEN 10 and 20;
  > 
  > SELECT * FROM tbl_name
  >   WHERE key_column IN (10,20,30);
  > 
  > SELECT * FROM tbl_name
  >   WHERE key_part1 = 10 AND key_part2 IN (10,20,30);
  > ```

- index

  另一种形式的全表扫描，只不过他的扫描方式是按照索引的顺序。

  The index join type is the same as ALL, except that the index tree is scanned. This occurs two ways:

  - If the index is a covering index for the queries and can be used to satisfy all data required form the table, only the index tree is scanned. In this case, the Extra column says Using index. An index-only scan usually is faster than ALL because the size of the index usually is smaller than the table data.
  - A full table scan is performed using reads from the index to look up data rows in index order. Uses index does not appear in the Extra column. 

   MySQL can use this join type when the query uses only columns that are part of a single index. 

- ALL

  A full table scan is done for each combination of rows from the previous tables. This is normally not good if the table is the first table not marked const, and usually very bad in all other cases. Normally, you can avoid ALL by adding indexes that enable row retrieval from the table based on constant values or column values from earlier tables. 

## 1.6. possible_keys 

**指出MySQL能使用哪个索引在表中找到记录，查询涉及到的字段上若存在索引，则该索引将被列出，但不一定被查询使用（该查询可以利用的索引，如果没有任何索引显示 null）**

该列完全独立于EXPLAIN输出所示的表的次序。这意味着在possible_keys中的某些键实际上不能按生成的表次序使用。
如果该列是NULL，则没有相关的索引。在这种情况下，可以通过检查WHERE子句看是否它引用某些列或适合索引的列来提高你的查询性能。如果是这样，创造一个适当的索引并且再次用EXPLAIN检查查询。

## 1.7. key

**key列显示MySQL实际决定使用的键（索引）**。

## 1.8. key_len

**表示索引中使用的字节数，可通过该列计算查询中使用的索引的长度（key_len显示的值为索引字段的最大可能长度，并非实际使用长度，即key_len是根据表定义计算而得，不是通过表内检索出的）**

不损失精确性的情况下，长度越短越好。

## 1.9. ref

The ref column shows which columns or constants are compared to the index named in the key column to select rows from the table. 

显示索引的哪一列被使用了，如果可能的话，是一个常数。哪些列或常量被用于查找索引列上的值。

## 1.10. rows

The rows column indicates the number of rows MySQL believes it must examine to execute the query.

For InnoDB tables, this number is an estimate, and may not always be exact. 

## 1.11. filtered

The filtered column indicates an estimated percentage of table rows filtered by the table condition. The maximum value is 100, which means no filtering of rows occurred. Values decreasing from 100 indicate increasing amounts of filtering. rows shows the estimated number of rows examined and rows × filtered shows the number of rows joined with the following table. For example, if rows is 1000 and filtered is 50.00 (50%), the number of rows to be joined with the following table is 1000 × 50% = 500. 

## 1.12. Extra

This column contains additional information about how MySQL resolves the query. For descriptions of the different values, see EXPLAIN Extra Information. 































