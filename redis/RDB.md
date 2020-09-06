Redis会在以下几种情况下对数据进行快照：

- 根据配置规则进行自动快照；
- 用户执行SAVE或BGSAVE命令；
- 执行FLUSHALL命令；
- 执行复制（replication）时。

