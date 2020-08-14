库information_schema下涉及到InnoDB的事务和锁的信息表如下。

# 一、表描述

## 1.1. innodb_trx

https://dev.mysql.com/doc/refman/5.7/en/information-schema-innodb-trx-table.html

INNODB_TRX表提供了INNODB中当前执行的每个事务的信息，包括事务是否正在等待锁、何时启动以及事务正在执行的SQL语句（如果有）。

INNODB_TRX表中的列如下：

- TRX_ID

  InnoDB内部的唯一事务ID号。这些ID不是为只读和非锁定的事务创建的。

- TRX_WEIGHT

  事务的权重，反映（但不一定是确切的计数）更改的行数和事务锁定的行数。要解决死锁，请 InnoDB选择权重最小的事务作为回滚的“ 受害者 ”。无论更改和锁定行的数量如何，已更改非事务表的事务都被认为比其他事务更重。

- TRX_STATE

  事务执行状态。允许值是 RUNNING，LOCK WAIT， ROLLING BACK，和 COMMITTING。

- TRX_STARTED

  事务开始时间。

- TRX_REQUESTED_LOCK_ID

  事务当前正在等待的锁的ID。当TRX_STATE值是LOCK WAIT时才有值，其他情况为null。要获取锁的详细信息，请将此列与INNODB_LOCKS表的LOCK_ID列联接起来进行查询。

- TRX_WAIT_STARTED

  事务开始等待锁的时间。当TRX_STATE值是LOCK WAIT时才有值，其他情况为null。

- TRX_MYSQL_THREAD_ID

  MySQL线程ID。若要获取有关线程的详细信息，请将此列与INFORMATION_SCHEMA PROCESSLIST表的ID列联接起来进行查询。

- TRX_QUERY

  事务正在执行的SQL语句。

- TRX_OPERATION_STATE

  事务的当前操作（如果有）；否则为空。

- TRX_TABLES_IN_USE

  处理此事务的当前SQL语句时使用的InnoDB表数。

- TRX_TABLES_LOCKED

  当前SQL语句具有行锁的InnoDB表数。（因为这些是行锁，而不是表锁，所以表通常仍然可以被多个事务读写，尽管有些行被锁定。）

- TRX_LOCK_STRUCTS

  事务保留的锁数。

- TRX_LOCK_MEMORY_BYTES

- TRX_ROWS_LOCKED

- TRX_ROWS_MODIFIED

- TRX_CONCURRENCY_TICKETS

- TRX_ISOLATION_LEVEL

- TRX_UNIQUE_CHECKS

- TRX_FOREIGN_KEY_CHECKS

- TRX_LAST_FOREIGN_KEY_ERROR

- TRX_ADAPTIVE_HASH_LATCHED

- TRX_ADAPTIVE_HASH_TIMEOUT

- TRX_IS_READ_ONLY

- TRX_AUTOCOMMIT_NON_LOCKING

## 1.2. innodb_locks

https://dev.mysql.com/doc/refman/5.7/en/information-schema-innodb-locks-table.html

此表在MySQL5.7.14中已被弃用，在MySQL8.0中被删除。

## 1.3. innodb_lock_waits

https://dev.mysql.com/doc/refman/5.7/en/information-schema-innodb-lock-waits-table.html

此表在MySQL5.7.14中已被弃用，在MySQL8.0中被删除。



