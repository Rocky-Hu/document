https://www.mysqltutorial.org/mysql-varchar/#:~:text=MySQL%20VARCHAR%20is%20the%20variable-length%20string%20whose%20length,255%20bytes%2C%20the%20length%20prefix%20is%201%20byte.

MySQL VARCHAR是一个可变长度的字符串，其长度可以达到65535。MySQL将VARCHAR值存储为1字节或2字节`长度前缀`加上实际数据。

`长度前缀指定值中的字节数。`如果列所需的字节数小于255，则长度前缀为1字节。如果列需要超过255个字节，则长度前缀为两个长度字节。

