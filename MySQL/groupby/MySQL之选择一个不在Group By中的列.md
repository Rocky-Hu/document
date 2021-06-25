~~~sql
SELECT @@GLOBAL.sql_mode;
~~~

It's true that this feature permits some ambiguous queries, and silently returns a result set with an arbitrary value picked from that column. In practice, it tends to be the value from the row within the group that is physically stored first.

These queries aren't ambiguous if you only choose columns that are functionally dependent on the column(s) in the GROUP BY criteria. In other words, if there can be only one distinct value of the "ambiguous" column per value that defines the group, there's no problem. This query would be illegal in Microsoft SQL Server (and ANSI SQL), even though it cannot logically result in ambiguity:

```sql
SELECT AVG(table1.col1), table1.personID, persons.col4
FROM table1 JOIN persons ON (table1.personID = persons.id)
GROUP BY table1.personID;
```

Also, MySQL has an SQL mode to make it behave per the standard: [`ONLY_FULL_GROUP_BY`](http://dev.mysql.com/doc/refman/5.7/en/sql-mode.html#sqlmode_only_full_group_by)

FWIW, SQLite also permits these ambiguous GROUP BY clauses, but it chooses the value from the *last* row in the group.†

† At least in the version I tested. What it means to be *arbitrary* is that either MySQL or SQLite could change their implementation in the future, and have some different behavior. You should therefore not rely on the behavior staying they way it is currently in ambiguous cases like this. It's better to rewrite your queries to be deterministic and not ambiguous. That's why MySQL 5.7 now enables ONLY_FULL_GROUP_BY by default.



