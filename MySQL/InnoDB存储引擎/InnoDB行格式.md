https://dev.mysql.com/doc/refman/5.7/en/innodb-row-format.html

表的行格式决定了其行的物理存储方式，这反过来又会影响查询和DML操作的性能。随着单个磁盘页中容纳更多的行，查询和索引查找可以更快地工作，缓冲池中所需的缓存更少，写入更新值所需的I/O也更少。

表中的数据被分隔到页中。构成每个表的页都排列在称为B树索引的树型数据结构中。表数据和二级索引都使用这种类型的结构。表示整个表的B树索引称为聚集索引，它根据主键列进行组织。聚集索引数据结构的节点包含行中所有列的值。二级索引结构的节点包含索引列和主键列的值。

可变长度列是列值存储在B树索引节点中的规则的一个例外。长度可变的列太长，无法放在B树页上，这些列存储在单独分配的磁盘页（称为溢出页）上。这样的列称为页外列。页外列的值存储在溢出页的单链接列表中，每个这样的列都有自己的一个或多个溢出页列表。根据列长度，所有或可变长度列值的前缀都存储在B树中，以避免浪费存储空间和必须读取单独的页。

InnoDB存储引擎支持四种行格式：(REDUNDANT)冗余、(COMPACT)紧凑、(DYNAMIC)动态和(COMPRESSED)压缩。

| 行格式       | 紧凑的存储特性 | 增强的可变长度列存储 | 大索引键前缀支持 | 压缩支持 | 支持的表空间类型                | 所需的文件格式        |
| :----------- | :------------- | :------------------- | :--------------- | :------- | :------------------------------ | :-------------------- |
| `REDUNDANT`  | No             | No                   | No               | No       | system, file-per-table, general | Antelope or Barracuda |
| `COMPACT`    | Yes            | No                   | No               | No       | system, file-per-table, general | Antelope or Barracuda |
| `DYNAMIC`    | Yes            | Yes                  | Yes              | No       | system, file-per-table, general | Barracuda             |
| `COMPRESSED` | Yes            | Yes                  | Yes              | Yes      | file-per-table, general         | Barracuda             |

# 冗余行格式

冗余格式提供了与MySQL旧版本的兼容性。

InnoDB文件格式（Antelope和Barracuda）都支持冗余行格式。

使用冗余行格式的表将可变长度列值（VARCHAR、VARBINARY、BLOB和文本类型）的前768字节存储在B-tree节点的索引记录中，其余字节存储在溢出页上。大于或等于768字节的固定长度列被编码为可变长度列，可以存储在页外。例如，如果字符集的最大字节长度大于3，则CHAR（255）列会超过768字节，比如说使用utf8mb4。

如果列的值为768字节或更少，则不会使用溢出页面，可能会导致I/O方面的一些节省，因为该值完全存储在B-tree节点中。这对于相对较短的BLOB列值很有效，但是可能导致B-tree节点填充数据而不是键值，从而降低了它们的效率。具有许多BLOB列的表可能会导致B-tree节点变得太满，并且包含的行太少，这会使整个索引的效率低于行更短或列值存储在页外的情况。

## 冗余行格式存储特性

冗余行格式具有以下存储特性：

- 每个索引记录包含一个6字节的头。头用于将连续的记录链接在一起，并用于行级锁定。
- 聚集索引中的记录包含所有用户定义列的字段。此外，还有一个6字节的事务ID字段和一个7字节的滚动指针字段。
- 如果没有为表定义主键，则每个聚集索引记录还包含一个6字节的行ID字段。
- 每个辅助索引记录包含为聚集索引键定义的不在辅助索引中的所有主键列。
- 记录包含指向该记录的每个字段的指针。如果记录中字段的总长度小于128字节，则指针为1字节；否则，两个字节。指针数组称为记录目录。指针指向的区域是记录的数据部分。
- 在内部，诸如CHAR（10）之类的固定长度字符列以固定长度格式存储。尾部空格不会从VARCHAR列截断。
- 大于或等于768字节的固定长度列被编码为可变长度列，可以存储在页外。例如，如果字符集的最大字节长度大于3，则CHAR（255）列会超过768字节，比如说使用utf8mb4。
- SQL NULL值在记录目录中保留一个或两个字节。如果存储在变长列中，那么SQL NULL值在记录的数据部分中保留0字节。对于固定长度的列，该列的固定长度保留在记录的数据部分中。为NULL值保留固定的空间允许将列从NULL值及时更新为非NULL值，而不会导致索引页碎片。

# 紧凑行格式

与冗余行格式相比，COMPACT行格式减少了约20%的行存储空间，但以增加某些操作的CPU使用为代价。
如果您的工作负载通常受到缓存命中率和磁盘速度的限制，那么COMPACT格式可能更快一些。
如果工作负载受CPU速度的限制，紧凑格式可能会比较慢。

InnoDB文件格式（Antelope和Barracuda）都支持紧凑行格式。

使用紧凑行格式的表将可变长度列值（VARCHAR、VARBINARY、BLOB和文本类型）的前768字节存储在B-tree节点的索引记录中，其余字节存储在溢出页上。大于或等于768字节的固定长度列被编码为可变长度列，可以存储在页外。例如，如果字符集的最大字节长度大于3，则CHAR（255）列会超过768字节，比如说使用utf8mb4。

如果列的值为768字节或更少，则不会使用溢出页面，可能会导致I/O方面的一些节省，因为该值完全存储在B-tree节点中。这对于相对较短的BLOB列值很有效，但是可能导致B-tree节点填充数据而不是键值，从而降低了它们的效率。具有许多BLOB列的表可能会导致B-tree节点变得太满，并且包含的行太少，这会使整个索引的效率低于行更短或列值存储在页外的情况。

## 紧凑行格式存储特性

紧凑行格式具有以下存储特性：

- 每个索引记录包含一个5字节的头，它前面可能还有一个可变长度部分。头用于将连续的记录链接在一起，并用于行级锁定。

- 记录头的可变长度部分包含用于指示NULL列的位向量。如果索引中可以为NULL的列数为N，则位向量占用CEILING(N/8)字节 (例如，如果有9到16列可以为NULL，则位向量使用两个字节)。 空列不占用此向量中位以外的空间。记录头的变长部分还包含变长列的长度。根据列的最大长度，每个长度需要一个或两个字节。如果索引中的所有列都不为NULL并且具有固定长度，则记录头没有可变长度部分。

- 对于每个非NULL的可变长度字段，记录头包含一个或两个字节的列长度。只有当列的一部分存储在溢出页的外部或最大长度超过255字节而实际长度超过127字节时，才需要两个字节。对于外部存储列，2字节长度表示内部存储部分的长度加上指向外部存储部分的20字节指针。内部部分是768字节，因此长度是768+20。20字节指针存储列的真实长度。

- 记录头后面是非空列的数据内容。

- 聚集索引中的记录包含所有用户定义列的字段。此外，还有一个6字节的事务ID字段和一个7字节的滚动指针字段。

- 如果没有为表定义主键，则每个聚集索引记录还包含一个6字节的行ID字段。

- 每个辅助索引记录包含为聚集索引键定义的不在辅助索引中的所有主键列。
  如果任何主键列是可变长度的，那么每个辅助索引的记录头都有一个可变长度的部分来记录它们的长度，即使辅助索引是在固定长度的列上定义的。

- 每个辅助索引记录包含为聚集索引键定义的不在辅助索引中的所有主键列。
  如果任何主键列是可变长度的，那么每个辅助索引的记录头都有一个可变长度的部分来记录它们的长度，即使辅助索引是在固定长度的列上定义的。

- 在内部，对于可变长度的字符集，如utf8mb3和utf8mb4, InnoDB试图通过删除末尾的空格来存储N字节的CHAR(N)。如果CHAR(N)列值的字节长度超过N个字节，则将尾随空格修剪为列值字节长度的最小值。
  CHAR(N)列的最大长度是最大字符字节长度× N。

  最少为CHAR(N)保留N个字节。在许多情况下，保留最小空间N可以在适当的位置进行列更新，而不会导致索引页碎片。相比之下，当使用冗余行格式时，CHAR(N)列占据最大字符字节长度× N。

  大于或等于768字节的固定长度列被编码为可变长度字段，可变长度字段可以在页外存储。例如，一个CHAR(255)列会超过768字节，如果字符集的最大字节长度大于3，比如使用utf8mb4。

# 动态行格式

DYNAMIC行格式提供了与COMPACT行格式相同的存储特征，但是为长变长列增加了增强的存储能力，并支持大型索引键前缀。

Barracuda文件格式支持动态行格式。

当使用ROW_FORMAT=DYNAMIC创建表时，InnoDB可以将长变长列值(对于VARCHAR、VARBINARY、BLOB和TEXT类型)完全存储在页外，聚集索引记录只包含一个指向溢出页的20字节指针。大于或等于768字节的固定长度字段被编码为可变长度字段。

列是否在页外存储取决于页大小和行总大小。当一行太长时，将选择最长的列作为页外存储，直到聚集索引记录适合B-tree页。小于或等于40字节的TEXT和BLOB列将行存储。

DYNAMIC行格式保持了在索引节点中存储整个行(COMPACT和REDUNDANT格式也是这样)的效率，但是DYNAMIC行格式避免了用大量长列的数据字节填充B-tree节点的问题。DYNAMIC行格式基于这样一种思想:如果长数据值的一部分是在页外存储的，那么通常最有效的方法是在页外存储整个值。使用DYNAMIC格式，B-tree节点中可能保留更短的列，从而最小化给定行所需的溢出页的数量。

动态行格式支持最多3072字节的索引键前缀。该特性由innodb_large_prefix变量控制，该变量默认是启用的。

使用DYNAMIC行格式的表可以存储在系统表空间、每个表的文件表空间和一般表空间中。如果要在系统表空间中存储DYNAMIC表，可以禁用innodb_file_per_table并使用常规的CREATE TABLE或ALTER TABLE语句，或者在CREATE TABLE或ALTER TABLE时使用tablespace [=] innodb_system表选项。变量innodb_file_per_table和innodb_file_format不适用于一般的表空间，也不适用于使用TABLESPACE [=] innodb_system表项在系统表空间中存储DYNAMIC表。

## 动态行格式存储特性

DYNAMIC行格式是COMPACT行格式的变体。

# 压缩行格式

压缩行格式提供了与动态行格式相同的存储特性和功能，但增加了对表和索引数据压缩的支持。

Barracuda文件格式支持压缩行格式。

与DYNAMIC行格式一样，COMPRESSED行格式在页外存储方面也使用了类似的内部细节，还考虑了压缩表和索引数据的额外存储和性能考虑，并使用更小的页大小。
使用compress行格式，KEY_BLOCK_SIZE选项控制在聚集索引中存储多少列数据，以及在溢出页面上放置多少列数据。

动态行格式支持最多3072字节的索引键前缀。该特性由innodb_large_prefix变量控制，该变量默认是启用的。

可以在每个表文件的表空间或一般表空间中创建使用COMPRESSED行格式的表。system表空间不支持COMPRESSED row格式。如果要将压缩表保存在“file-per-table”表空间中，需要启用innodb_file_per_table变量，并将innodb_file_format设置为“Barracuda”。变量innodb_file_per_table和innodb_file_format不适用于一般的表空间。
常规表空间支持所有行格式，但需要注意的是，由于物理页大小不同，压缩表和未压缩表不能在同一个常规表空间中共存。

## 压缩行格式存储特性

DYNAMIC行格式是COMPACT行格式的变体。

# 定义表的行格式

InnoDB表的默认行格式由变量innodb_default_row_format定义，该变量的默认值为DYNAMIC。当未显式定义ROW_FORMAT表选项或指定了ROW_FORMAT= default时，将使用默认行格式。

表的行格式可以在CREATE table或ALTER table语句中使用ROW_FORMAT表选项显式定义。例如:

~~~sql
CREATE TABLE t1 (c1 INT) ROW_FORMAT=DYNAMIC;
~~~

显式定义的ROW_FORMAT设置会覆盖默认的行格式。指定ROW_FORMAT=DEFAULT等价于使用隐式DEFAULT。

innodb_default_row_format变量可以动态设置:

~~~sql
mysql> SET GLOBAL innodb_default_row_format=DYNAMIC;
~~~

有效的innodb_default_row_format选项包括DYNAMIC、COMPACT和REDUNDANT。compress行格式不支持在系统表空间中使用，不能定义为默认格式。它只能在CREATE TABLE或ALTER TABLE语句中显式指定。尝试将innodb_default_row_format变量设置为COMPRESSED返回错误:

~~~sql
mysql> SET GLOBAL innodb_default_row_format=COMPRESSED;
ERROR 1231 (42000): Variable 'innodb_default_row_format'
can't be set to the value of 'COMPRESSED'
~~~

当未显式指定ROW_FORMAT选项或使用ROW_FORMAT=DEFAULT时，新创建的表将使用innodb_default_row_format变量定义的行格式。例如，下面的CREATE TABLE语句使用innodb_default_row_format变量定义的行格式。

~~~sql
CREATE TABLE t1 (c1 INT);
~~~

~~~sql
CREATE TABLE t2 (c1 INT) ROW_FORMAT=DEFAULT;
~~~

当未显式指定ROW_FORMAT选项时，或者当使用ROW_FORMAT=DEFAULT时，重建表的操作会自动将表的行格式更改为innodb_default_row_format变量定义的格式。

表重建操作包括在需要表重建的地方使用ALGORITHM=COPY或ALGORITHM=INPLACE的ALTER Table操作。

下面的示例演示了一个表重建操作，该操作在没有显式定义行格式的情况下默默地更改所创建表的行格式。

~~~sql
mysql> SELECT @@innodb_default_row_format;
+-----------------------------+
| @@innodb_default_row_format |
+-----------------------------+
| dynamic                     |
+-----------------------------+

mysql> CREATE TABLE t1 (c1 INT);

mysql> SELECT * FROM INFORMATION_SCHEMA.INNODB_SYS_TABLES WHERE NAME LIKE 'test/t1' \G
*************************** 1. row ***************************
     TABLE_ID: 54
         NAME: test/t1
         FLAG: 33
       N_COLS: 4
        SPACE: 35
  FILE_FORMAT: Barracuda
   ROW_FORMAT: Dynamic
ZIP_PAGE_SIZE: 0
   SPACE_TYPE: Single

mysql> SET GLOBAL innodb_default_row_format=COMPACT;

mysql> ALTER TABLE t1 ADD COLUMN (c2 INT);

mysql> SELECT * FROM INFORMATION_SCHEMA.INNODB_SYS_TABLES WHERE NAME LIKE 'test/t1' \G
*************************** 1. row ***************************
     TABLE_ID: 55
         NAME: test/t1
         FLAG: 1
       N_COLS: 5
        SPACE: 36
  FILE_FORMAT: Antelope
   ROW_FORMAT: Compact
ZIP_PAGE_SIZE: 0
   SPACE_TYPE: Single
~~~



























