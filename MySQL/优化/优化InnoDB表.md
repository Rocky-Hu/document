## 一、优化InnoDB Read-Only事务

https://dev.mysql.com/doc/refman/5.7/en/innodb-performance-ro-txn.html

InnoDB可以避免为已知的只读事务设置事务ID (TRX_ID字段)带来的开销。事务ID仅用于执行写操作或锁定读操作的事务，如SELECT…FOR UPDATE。消除不必要的事务ID可以减少每次查询或数据更改语句构造读取视图时所查询的内部数据结构的大小。

InnoDB在以下情况下检测只读事务：

- 事务是用START TRANSACTION READ ONLY 语句启动的。在这种情况下，试图更改数据库（对于InnoDB、MyISAM或其他类型的表）会导致错误，事务将继续处于只读状态：

  ~~~reStructuredText
  ERROR 1792 (25006): Cannot execute statement in a READ ONLY transaction.
  ~~~

  您仍然可以对只读事务中特定于会话的临时表进行更改，或者为它们发出锁定查询，因为这些更改和锁对任何其他事务都不可见。

- autocommit设置已打开，因此事务被保证是一个单独的语句，组成事务的单个语句是一个“非锁定”SELECT语句。也就是说，不使用FOR UPDATE或LOCK IN SHARED MODE模式子句的SELECT。

- 事务在没有READ ONLY选项的情况下启动，但尚未执行显式锁定行的更新或语句。在需要更新或显式锁定之前，事务保持只读模式。

因此，对于像报表生成器这样的读密集型应用程序，您可以通过调整将一系列的查询组合到START TRANSACTION READ ONLY 和COMMIT中，或者在运行SELECT语句之前打开autocommit设置，或者通过简单地避免查询中穿插任何数据更改语句来优化InnoDB查询序列。

~~~sql
mysql> set session transaction read only;
Query OK, 0 rows affected (0.00 sec)

mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> insert into user(name) values('rocky');
ERROR 1792 (25006): Cannot execute statement in a READ ONLY transaction.
mysql> update user set name='rocky1' where id='1';
ERROR 1792 (25006): Cannot execute statement in a READ ONLY transaction.
mysql> delete from user;
ERROR 1792 (25006): Cannot execute statement in a READ ONLY transaction.
~~~

