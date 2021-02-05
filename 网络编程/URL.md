URL的语法为：

~~~
protocol://userInfo@host:port/path?query#fragment
~~~

用户信息、主机和端口合在一起构成权威机构（authority）。

片段（fragment）指向远程资源的某个特定部分。如果远程资源是HTML，那么这个片段标识符指定该HTML文档中的一个锚（anchor）。如果远程资源是XML，那么这个片段标识符是一个XPointer。片段标识符目标在HTML文档中用id属性创建，如：

~~~html
<h3 id="xtocid1902914">Comments</h3>
~~~

这个标记标识文档中的某个点。为了引用这个点，URL不仅要包括文档的文件名，还要包括片段标识符，与URL的其余部分用#隔开：

~~~
http://www.cafeaulait.org/javafaq.html#xtocid1902914
~~~





























