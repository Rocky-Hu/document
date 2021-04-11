filtered列表示通过表条件筛选的表行的估计百分比。最大值是100，这意味着没有对行进行筛选。从100开始下降的值表明过滤的数量在增加。rows显示所检查的估计行数，rows × filtered显示与下表连接的行数。例如，如果rows为1000，过滤后为50.00(50%)，则需要与下表连接的行数为1000 × 50% = 500。

The `filtered` column indicates an estimated percentage of table rows that are filtered by the table condition. The maximum value is 100, which means no filtering of rows occurred. Values decreasing from 100 indicate increasing amounts of filtering. `rows` shows the estimated number of rows examined and `rows` × `filtered` shows the number of rows that are joined with the following table. For example, if `rows` is 1000 and `filtered` is 50.00 (50%), the number of rows to be joined with the following table is 1000 × 50% = 500.

