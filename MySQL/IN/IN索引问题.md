# 一、表结构

~~~sql
CREATE TABLE `tb_user_department`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信user_id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `department_id` int(32) UNSIGNED NOT NULL COMMENT '部门ID',
  `main_department` int(32) NULL DEFAULT NULL COMMENT '主部门',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_dep`(`user_id`, `department_id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  INDEX `idx_main_department`(`main_department`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 75712 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门人员关联表' ROW_FORMAT = Dynamic;

~~~

# 二、IN索引使用测试

表总数：

~~~sql
select count(*) from tb_user_department

输出：170
~~~

**查询1：**

~~~sql
select * from tb_user_department where main_department in (4)
~~~

执行计划：

~~~sql
mysql> explain select * from tb_user_department where main_department in (4)\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user_department
   partitions: NULL
         type: ref
possible_keys: idx_main_department
          key: idx_main_department
      key_len: 5
          ref: const
         rows: 39
     filtered: 100.00
        Extra: NULL
1 row in set, 1 warning (0.03 sec)
~~~

**查询2：**

~~~sql
select * from tb_user_department where main_department in (1)
~~~

执行计划：

~~~sql
mysql> explain select * from tb_user_department where main_department in (1)\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user_department
   partitions: NULL
         type: ref
possible_keys: idx_main_department
          key: idx_main_department
      key_len: 5
          ref: const
         rows: 59
     filtered: 100.00
        Extra: NULL
1 row in set, 1 warning (0.03 sec)
~~~

**查询3**：

~~~sql
select * from tb_user_department where main_department in (4,1)
~~~

执行计划：

~~~sql
mysql> explain select * from tb_user_department where main_department in (4,1)\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user_department
   partitions: NULL
         type: ALL
possible_keys: idx_main_department
          key: NULL
      key_len: NULL
          ref: NULL
         rows: 171
     filtered: 57.31
        Extra: Using where
1 row in set, 1 warning (0.03 sec)
~~~

**查询4**：

~~~sql
select count(*) from tb_user_department where main_department in (4,1)
~~~

执行计划：

~~~sql
mysql> explain select count(*) from tb_user_department where main_department in (4,1)\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user_department
   partitions: NULL
         type: range
possible_keys: idx_main_department
          key: idx_main_department
      key_len: 5
          ref: NULL
         rows: 98
     filtered: 100.00
        Extra: Using where; Using index
1 row in set, 1 warning (0.03 sec)
~~~

**查询5**：

~~~sql
select * from tb_user_department where id in(75685,75690,75697,75700)
~~~

执行计划：

~~~sql
mysql> explain select * from tb_user_department where id in(75685,75690,75697,75700)\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user_department
   partitions: NULL
         type: range
possible_keys: PRIMARY
          key: PRIMARY
      key_len: 4
          ref: NULL
         rows: 4
     filtered: 100.00
        Extra: Using where
1 row in set, 1 warning (0.03 sec)
~~~

**查询6：**

~~~sql
select * from tb_user_department where `name` in ('余瑶','王慧珏')
~~~

执行计划：

~~~sql
mysql> explain select * from tb_user_department where `name` in ('余瑶','王慧珏')\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user_department
   partitions: NULL
         type: range
possible_keys: idx_name
          key: idx_name
      key_len: 258
          ref: NULL
         rows: 2
     filtered: 100.00
        Extra: Using index condition
1 row in set, 1 warning (0.03 sec)
~~~

**总结**

IN走不走索引，看具体的情况

- 若IN的字段是主键，那么会走索引。（IN条件集很大会不会索引失效？？）
- 若IN的字段是唯一索引，那么会走索引。（IN条件集很大会不会索引失效？？）
- 若IN的字段是非唯一索引，那么可能会走索引，也可能不会，主要看字段内的数据的重复程度，重复太多，IN条件集增到到两个就不会走索引了。





