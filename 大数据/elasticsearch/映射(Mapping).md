https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping-intro.html

**Elasticsearch 根据我们索引的文档，为域(称为 *属性* )动态生成的映射。**

# 自定义映射

尽管在很多情况下基本域数据类型已经够用，但你经常需要为单独域自定义映射，特别是字符串域。自定义映射允许你执行下面的操作：

- 全文字符串域和精确值字符串域的区别
- 使用特定语言分析器
- 优化域以适应部分匹配
- 指定自定义数据格式
- 还有更多

域最重要的属性是 `type` 。对于不是 `string` 的域，你一般只需要设置 `type` ：

~~~json
{
    "number_of_clicks": {
        "type": "integer"
    }
}
~~~

默认， `string` 类型域会被认为包含全文。就是说，它们的值在索引前，会通过一个分析器，针对于这个域的查询在搜索前也会经过一个分析器。

## string域映射

`string` 域映射的两个最重要属性是 `index` 和 `analyzer` 。

### index

`index` 属性控制怎样索引字符串。它可以是下面三个值：

- **`analyzed`**

  首先分析字符串，然后索引它。换句话说，以全文索引这个域。

- **`not_analyzed`**

   索引这个域，所以它能够被搜索，但索引的是精确值。不会对它进行分析。

- **`no`**

  不索引这个域。这个域不会被搜索到。

`string` 域 `index` 属性默认是 `analyzed` 。如果我们想映射这个字段为一个精确值，我们需要设置它为 `not_analyzed` ：

~~~json
{
    "tag": {
        "type":     "string",
        "index":    "not_analyzed"
    }
}
~~~

> 其他简单类型（例如 `long` ， `double` ， `date` 等）也接受 `index` 参数，但有意义的值只有 `no` 和 `not_analyzed` ， 因为它们永远不会被分析。

### analyzer

对于 `analyzed` 字符串域，用 `analyzer` 属性指定在搜索和索引时使用的分析器。默认， Elasticsearch 使用 `standard` 分析器， 但你可以指定一个内置的分析器替代它，例如 `whitespace` 、 `simple` 和 `english`：

~~~json
{
    "tweet": {
        "type":     "string",
        "analyzer": "english"
    }
}
~~~

