# 默认排序

If you had done the same statement with an InnoDB table, they would have been delivered in PRIMARY KEY order, not INSERT order. Again, this is an artifact of the underlying implementation, not something to depend on.

https://forums.mysql.com/read.php?21,239471,239688#msg-239688

# SQL语句两个字段或多个字段同时order by 排序

sql支持多个字段进行order by排序，各字段之间用逗号”,”隔开。如：

SELECT *FROM tablename order by column1,column2,column3 ;
如果不显示指出是升序还是降序，则默认为是升序


（1）ORDER BY column1,column2;
 表示：column1和column2都是升序

 (2)ORDER BY column1,column2 DESC;
 表示：column1 升序，column2是降序；

 (3)ORDER BY column1 DESC,column2;
 表示：column1降序，column2升序

 (4)ORDER BY column1 DESC,column2 DESC;
 表示：column1和column2都是降序。

注：想要对两个字段都同是进行升序/降序操作时候，必须得同时在每个字段
的后面加上关键字（asc/desc）。若是想要对两个关键字段按升序排序，
前面一个字段没有写关键字asc没关系，因为sql默认就是升序；但是若是
想要对两个关键字段进行降序操作，则必须得对两个关键字段都加上desc；
如： column1 desc,column2 desc;
