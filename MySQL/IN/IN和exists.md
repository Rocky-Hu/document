区分in和exists主要是造成了驱动顺序的改变（这是性能变化的关键），如果是exists，那么以外层表为驱动表，先被访问；如果是IN，那么先执行子查询。所以IN适合外表大而内表小的情况；EXISTS适合于外表小而内表大的情况。

https://blog.csdn.net/weixin_41485592/article/details/80810849



