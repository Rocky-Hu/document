看一个SQL:

~~~sql
select count(1)
        from tb_customer where
        add_time between '2021-04-05 00:00:00' and '2021-04-12 00:00:00'
        and user_id in ('HT00095','D0014001')
				or 
				user_id in (
				  select distinct(user_id) from tb_user_department where main_department in (2 , 22 , 10 , 11 , 13)
				)
				
~~~

需求的本意是查询id是'HT00095','D0014001'或者在2 , 22 , 10 , 11 , 13部门下的的员工的客户。

上面的查询有问题。

where 后面如果有and,or的条件，则or自动会把左右的查询条件分开，即先执行and，再执行or。原因就是：and的执行优先级最高！

上面的SQL被OR拆分成两条：

~~~sql
select count(1)
        from tb_customer where
        add_time between '2021-04-05 00:00:00' and '2021-04-12 00:00:00'
        and user_id in ('HT00095','D0014001')
~~~

~~~sql
select count(1)
        from tb_customer where
				user_id in (
				  select distinct(user_id) from tb_user_department where main_department in (2 , 22 , 10 , 11 , 13)
				)
~~~

结果为两条SQL结果之和。

正确的SQL如下：

~~~sql
SELECT
	* 
FROM
	tb_customer 
WHERE
	add_time BETWEEN '2021-04-05 00:00:00' 
	AND '2021-04-12 00:00:00' 
	AND (
		user_id IN ( 'HT00095', 'D0014001' ) 
	OR user_id IN ( SELECT DISTINCT ( user_id ) FROM tb_user_department WHERE main_department IN ( 2, 22, 10, 11, 13 ) ))
~~~



