https://dev.mysql.com/doc/refman/5.7/en/innodb-locking-transaction-model.html

在InnoDB事务模型中，目标是将多版本数据库的最佳属性与传统的两阶段锁定相结合。

两段锁协议是指每个事务的执行可以分为两个阶段：生长阶段（加锁阶段）和衰退阶段（解锁阶段）。

加锁阶段：在该阶段可以进行加锁操作。在对任何数据进行读操作之前要申请并获得S锁，在进行写操作之前要申请并获得X锁。加锁不成功，则事务进入等待状态，直到加锁成功才继续执行。

解锁阶段：当事务释放了一个封锁以后，事务进入解锁阶段，在该阶段只能进行解锁操作不能再进行加锁操作。

两段封锁法可以这样来实现：事务开始后就处于加锁阶段，一直到执行ROLLBACK和COMMIT之前都是加锁阶段。ROLLBACK和COMMIT使事务进入解锁阶段，即在ROLLBACK和COMMIT模块中DBMS释放所有封锁。

# 一、事务隔离级别（Transaction Isolation Levels）

事务隔离是数据库处理的基础之一。隔离级别是在多个事务同时进行更改和执行查询时，对性能(performance)与可靠性(reliability)、一致性(consistency)和可再现性(reproducibility)之间的平衡进行微调的设置。

InnoDB支持了SQL:1992标准提出的四种隔离级别：READ UNCOMMITED,READ COMMITED, REPEATABLE READ和SERIALIZABLE。默认的隔离级别是REPEATABLE READ。

InnoDB使用不同的锁定策略来支持这些隔离级别。根据不同的场景使用不同的隔离级别。比如说XA事务就是用REPEATABLE READ级别。

下面的内容描述了MySQL如何支持不同的事务级别。

## 1.1. REPEATABLE READ

这是InnoDB的默认隔离级别。

同一事务中的一致读取第一次读取建立的快照。这意味着，如果在同一事务中发出多个普通的（非锁定）SELECT语句，则这些SELECT语句彼此之间也保持一致。

对于锁定读（SELECT...FOR UPDATE、SELECT...LOCK IN SHARE MODE），UPDATE和DELETE语句，锁定取决于语句是将唯一索引作为非范围搜索条件，还是作为范围类型搜索条件（locking depends on whether the statement uses a unique index with a unique search condition, or a range-type search condition.）。

- 对于唯一索引作为非范围搜索条件，InnoDB只锁定找到的索引记录（Record Locks），而不锁定其前置区间。
- 对于其他搜索条件，InnoDB会锁定扫描到的索引范围，使用间隙锁（Gap Locks）或者临键锁（Next-Key Locks）以此来阻塞那些触及到索引范围的执行修改动作的事务。

注意：唯一索引作为非范围搜索条件，使用Record Locks。

> 范围搜索，作为搜索条件的值对应有行存在，则使用Next-Key Locks，用于将该行也锁住；反之，使用Gap Locks。

## 1.2. READ COMMITTED

即使在同一个事务中，每个一致读取都设置并读取自己的新快照。

对于锁定读（SELECT...FOR UPDATE、SELECT...LOCK IN SHARE MODE），UPDATE和DELETE语句，只使用Record Locks。Gap locking is only used for foreign-key constraint checking and duplicate-key checking.

READ COMMITTED隔离级别下，范围锁定被禁用，幻读问题会发生。

使用“READ COMMITTED”有其他效果：

- 对于UPDATE或DELETE语句，InnoDB只对更新或删除的行持有锁。非匹配行的记录锁在MySQL计算WHERE条件后释放。这大大降低了死锁的概率，但它们仍然可能发生。
- 对于UPDATE语句，如果某行已经被锁定，InnoDB执行“半一致”读取，将最新提交的版本返回到MySQL，以便MySQL判断该行是否符合更新的WHERE条件。如果行匹配（必须更新），MySQL会再次读取该行，这次InnoDB要么锁定它，要么等待对它的锁定。

## 1.3. READ UNCOMMITTED

SELECT语句以非锁定方式执行，但可能使用行的早期版本。用这个隔离级别，这样的读取是不一致的。这也叫脏读。

## 1.4. SERIALIZABLE

这个级别类似于REPEATABLE READ,但是如果autocommit被禁用，InnoDB会隐式地将所有普通的SELECT语句转换为SELECT...LOCK IN SHARE MODE。

If [`autocommit`](https://dev.mysql.com/doc/refman/5.7/en/server-system-variables.html#sysvar_autocommit) is enabled, the [`SELECT`](https://dev.mysql.com/doc/refman/5.7/en/select.html) is its own transaction. It therefore is known to be read only and can be serialized if performed as a consistent (nonlocking) read and need not block for other transactions. (To force a plain [`SELECT`](https://dev.mysql.com/doc/refman/5.7/en/select.html) to block if other transactions have modified the selected rows, disable [`autocommit`](https://dev.mysql.com/doc/refman/5.7/en/server-system-variables.html#sysvar_autocommit).)

# 二、自动提交，提交，和回滚（autocommit，Commit，和Rollback）

在InnoDB中，所有用户活动都发生在事务内部。如果启用了autocommit模式，则每个SQL语句将单独形成一个事务。默认情况下，MySQL在启用autocommit的情况下为每个新连接启动会话，因此如果SQL语句没有返回错误，MySQL会在每个SQL语句之后执行一次提交。如果语句返回错误，则提交或回滚行为取决于错误。

启用了autocommit的会话可以执行多语句事务，方法是用显式的START TRANSACTION或BEGIN语句启动它，然后用COMMIT或ROLLBACK语句结束它。

如果在SET autocommit=0的会话中禁用了autocommit模式，则该会话始终有一个事务处于打开状态。COMMIT或ROLLBACK语句结束当前事务并启动新事务。

如果禁用了autocommit的会话在没有显式提交最终事务的情况下结束，MySQL将回滚该事务。

COMMIT意味着在当前事务中所做的更改是永久性的，并且对其他会话可见。另一方面，ROLLBACK语句取消当前事务所做的所有修改。COMMIT和ROLLBACK都会释放当前事务期间设置的所有InnoDB锁。

# 三、一致性非锁定读（Consistent Nonlocking Reads）

一致性的非锁定读（consistent nonlocking read）是指InnoDB存储引擎通过行多版本控制（multi versioning）的方式来读取当前执行事件数据库中行的数据。如果读取的行正在执行DELETE或UPDATE操作，这时读取操作不会因此去等待行上锁的释放。相反地，InnoDB存储引擎会去读取行的一个快照数据。

非锁定读机制极大地提高了数据库的并发性。在InnoDB存储引擎的默认设置下，这是默认的读取方式，即读取不会占用和等待表上的锁。但是在不同的事务隔离级别下，读取的方式不同，并不是在每个事务隔离级别下都是采用非锁定的一致性读。此外，即使都是使用非锁定的一致性读，但是对于快照数据的定义也各不相同。

在READ COMMITTED事务隔离级别下，对于快照数据，非一致性读总是读取被锁定行的最新一份快照数据。而在REPEATABLE READ事务隔离级别下，对于快照数据，非一致性读总是读取事务开始时的行数据版本。

# 四、锁定读（Locking Reads）

在默认配置下，即事务的隔离级别为REPEATABLE READ模式下，InnoDB存储引擎的SELECT操作使用一致性非锁定读。但是在某些情况下，用户需要显示地对数据库读取操作进行加锁以保证数据逻辑的一致性。而这要求数据库支持加锁语句，即使用对于SELECT的只读操作。InnoDB存储引擎对于SELECT语句支持两种一致性的锁定读（locking read）操作：

- SELECT ... LOCK IN SHARE MODE
- SELECT ... FOR UPDATE

> 只有在禁用自动提交时才可以锁定读取（通过使用“START TRANSACTION”启动事务或将“autocommit”设置为0）。 
>
> 当事务提交或者回滚时，所有通过LOCK IN SHARE MODE和FOR UPDATE设置的的锁都会被释放。

## 4.1. SELECT ... LOCK IN SHARE MODE

共享锁模式锁定读。

- 一个session对其读取的行设置了共享锁，其他session可以读取这些行，但是在拥有锁的的session事务提交之前不能改变这些行。

- 一个session对其读取的行设置了共享锁，其他session同样可以以共享锁定读的方式对相同的行设置共享锁，不过，这样这些session之间对于改变行就会相互制约。

共享锁模式可以说是读分享，写牵制。

**死锁检测**

共享锁模式容易出现死锁。因为对于共享锁模式下执行修改行操作，sessions之间会相互牵制，会相互等待其他session进行事务提交释放锁。

示例：

~~~sql
######## session1 ########
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from user where id='1' lock in share mode;
+----+-------+
| id | name  |
+----+-------+
|  1 | rocky |
+----+-------+
1 row in set (0.00 sec)

######## session2 ########
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from user where id='1' lock in share mode;
+----+-------+
| id | name  |
+----+-------+
|  1 | rocky |
+----+-------+
1 row in set (0.00 sec)

######## session1 ########
mysql> update user set name='rocky1' where id='1';
// 这里阻塞了

######## session2 ########
mysql> update user set name='rocky2' where id='1';
ERROR 1213 (40001): Deadlock found when trying to get lock; try restarting transaction
mysql> 

######## session1 ########
mysql> update user set name='rocky1' where id='1';
Query OK, 1 row affected (21.54 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> 
~~~

session1和session2都对相同的行设置了共享锁，session1准备update，此时发现session2也持有共享锁，所以等待session2释放锁，session1阻塞；session2同样准备update，此时发现session1也持有共享锁，所以等待session1释放锁。这样就形成了session1等待session2释放锁，session2等待session1释放锁的局面，这就是死锁，数据库检测到了死锁，所以session2执行update的时候就抛出了错误：

~~~java
ERROR 1213 (40001): Deadlock found when trying to get lock; try restarting transaction
~~~

此时session2的锁被释放，死锁局面解决，session1执行udpate。

## 4.2. SELECT ... FOR UPDATE

独占锁模式锁定读。

对比共享锁定读模式，SELECT ... FOR UPDATE是独占式锁定读。

- 独占式加锁，已通过SELECT ... FOR UPDATE进行加锁，那么其他的session尝试执行SELECT ... LOCK IN SHARE MODE或者SELECT ... FOR UDATE来锁定读时就会被阻塞。
- 所有改变行的操作就会被阻塞，直到成功执行SELECT ... FOR UPDATE的session释放锁。

## 4.3. 嵌套子查询锁定读

锁定读对于嵌套子查询是不起作用的。

例如，下面的语句不锁定表t2中的行。

~~~sql
SELECT * FROM t1 WHERE c1 = (SELECT c1 FROM t2) FOR UPDATE;
~~~

要锁定表t2中的行，请向子查询添加locking read子句：

~~~sql
SELECT * FROM t1 WHERE c1 = (SELECT c1 FROM t2 FOR UPDATE) FOR UPDATE;
~~~

## 4.4. 锁定读使用示例

**示例1** 

有子表child和父表parent，child有字段parent_id引用parent行，保证child和parent的父子关系。下面插入child数据的操作是不安全的：

~~~sql
select * from parent where id = '1'; // 查询id为1的父记录存在
insert into child(parent_id) values('1'); // child表中插入一条数据，设置与parent的关联
~~~

确保父记录存在，然后插入子记录，看似是能保证数据一致性的，不过事实上不可以，因为在上面的SELECT和INSERT之间，可能会有其他session删除parent中id为1的记录。

避免上面的问题出现，可以使用SELECT ... LOCK IN SHARE MODE。

~~~sql
SELECT * FROM parent WHERE id = '1' LOCK IN SHARE MODE;
~~~

**示例2**

CHILD_CODES为sequence表，表中一个整型字段counter_field ，counter_field的数据作为CHILD表插入数据时的主键值。

在CHILD表中插入数据时查询CHILD_CODES获取counter_field：

~~~sql
select counter_field from CHILD_CODES;
UPDATE child_codes SET counter_field = counter_field + 1;
~~~

多个session执行上面的步骤，可能会获取到相同的值导致CHILD表主键重复。

这里使用LOCK IN SHARE MODE是不合适的，因为死锁问题存在，导致大量session会被强制放弃执行。

通过FOR UPDATE方式来解决：

~~~sql
SELECT counter_field FROM child_codes FOR UPDATE;
UPDATE child_codes SET counter_field = counter_field + 1;
~~~

# 五、总结

## 5.1. 快照读和当前读

读取历史数据的方式，我们叫它快照读 (snapshot read)，而读取数据库当前版本数据的方式，叫当前读 (current read)。很显然，在MVCC中：

- 快照读：就是select
  - select * from table ….;
- 当前读：特殊的读操作，插入/更新/删除操作，属于当前读，处理的都是当前的数据，需要加锁。
  - select * from table where ? lock in share mode;
  - select * from table where ? for update;
  - insert;
  - update ;
  - delete;

事务的隔离级别实际上都是定义了当前读的级别，MySQL为了减少锁处理（包括等待其它锁）的时间，提升并发能力，引入了快照读的概念，使得select不用加锁。而update、insert这些“当前读”，就需要另外的模块来解决了。



