https://dev.mysql.com/doc/refman/5.7/en/innodb-row-format.html

表的行格式决定了其行的物理存储方式，这反过来又会影响查询和DML操作的性能。随着单个磁盘页中容纳更多的行，查询和索引查找可以更快地工作，缓冲池中所需的缓存更少，写入更新值所需的I/O也更少。

表中的数据被分隔到页中。构成每个表的页都排列在称为B树索引的树型数据结构中。表数据和二级索引都使用这种类型的结构。表示整个表的B树索引称为聚集索引，它根据主键列进行组织。聚集索引数据结构的节点包含行中所有列的值。二级索引结构的节点包含索引列和主键列的值。

可变长度列是列值存储在B树索引节点中的规则的一个例外。长度可变的列太长，无法放在B树页上，这些列存储在单独分配的磁盘页（称为溢出页）上。这样的列称为页外列。页外列的值存储在溢出页的单链接列表中，每个这样的列都有自己的一个或多个溢出页列表。根据列长度，所有或可变长度列值的前缀都存储在B树中，以避免浪费存储空间和必须读取单独的页。

InnoDB存储引擎支持四种行格式：(REDUNDANT)冗余、(COMPACT)紧凑、(DYNAMIC)动态和(COMPRESSED)压缩。

| Row Format   | Compact Storage Characteristics | Enhanced Variable-Length Column Storage | Large Index Key Prefix Support | Compression Support | Supported Tablespace Types      | Required File Format  |
| :----------- | :------------------------------ | :-------------------------------------- | :----------------------------- | :------------------ | :------------------------------ | :-------------------- |
| `REDUNDANT`  | No                              | No                                      | No                             | No                  | system, file-per-table, general | Antelope or Barracuda |
| `COMPACT`    | Yes                             | No                                      | No                             | No                  | system, file-per-table, general | Antelope or Barracuda |
| `DYNAMIC`    | Yes                             | Yes                                     | Yes                            | No                  | system, file-per-table, general | Barracuda             |
| `COMPRESSED` | Yes                             | Yes                                     | Yes                            | Yes                 | file-per-table, general         | Barracuda             |

# 冗余行格式

冗余格式提供了与MySQL旧版本的兼容性。

InnoDB文件格式（Antelope和Barracuda）都支持冗余行格式。

使用冗余行格式的表将可变长度列值（VARCHAR、VARBINARY、BLOB和文本类型）的前768字节存储在B-tree节点的索引记录中，其余字节存储在溢出页上。大于或等于768字节的固定长度列被编码为可变长度列，可以存储在页外。





























