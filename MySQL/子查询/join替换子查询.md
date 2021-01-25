原SQL:

~~~sql
select
        count(distinct(user_id)) 
    from
        tb_customer          
    WHERE
        user_id in (
            select
                distinct(user_id) 
            from
                tb_user_department 
            where
                main_department in (
                    4,5,7,9,14
                )
        )
~~~

优化：

~~~sql
select count(distinct(tud.user_id)) from tb_user_department tud join tb_customer tc on tud.user_id = tc.user_id where tud.main_department in (4,5,7,9,14)
~~~

