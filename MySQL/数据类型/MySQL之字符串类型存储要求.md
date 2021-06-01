在下面列表中，对于字符型字符串来说M表示的是字符串长度，对于二进制型字符串来说表示的是字节大小。L表示给定字符串值的实际长度（以字节为单位）。

| 数据类型                    | 存储要求                                                     |
| :-------------------------- | :----------------------------------------------------------- |
| CHAR(M)                     | 特殊情况查看 [COMPACT Row Format Storage Characteristics](https://dev.mysql.com/doc/refman/5.7/en/innodb-row-format.html#innodb-compact-row-format-characteristics). 否则，存储需要M× w字节，0<=M<=255，其中w是字符集中最大长度字符所需的字节数。 |
| BINARY(M)                   | *`M`* 个字节, 0 `<= M <=` 255                                |
| VARCHAR(M),VARBINARY(M)     | 如果列所需的字节数小于255，则长度前缀为1字节，存储需要L+1字节。如果列需要超过255个字节，则长度前缀为两个长度字节，存储需要L+2字节。 |
| TINYBLOB, TINYTEXT          | *`L`* + 1 字节, 其中 *`L`* < 28                              |
| BLOB, TEXT                  | *`L`* + 2 字节, 其中*`L`* < 216                              |
| MEDIUMBLOB,MEDIUMTEXT       | *`L`* + 3 字节, 其中*`L`* < 224                              |
| LONGBLOB, LONGTEXT          | *`L`* + 4 字节, 其中 *`L`* < 232                             |
| ENUM('value1','value2',...) | 1或2字节，取决于枚举值的数目（最大值为65535个值）            |
| SET('value1','value2',...)  | 1、2、3、4或8字节，取决于集合元素的数量（最多64个元素）      |

