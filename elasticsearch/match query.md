Match Query 是最常用的 Full Text Query 。无论需要查询什么字段， `match` 查询都应该会是首选的查询方式。它既能处理全文字段，又能处理精确字段。

# operator

# analyzer

`analyzer` 属性是指在对查询文本分析时的分析器：

- 如果没有指定则会使用字段mapping 时指定的分析器
- 如果字段在 mapping 时也没有明显指定，则会使用默认的 search analyzer。

这里我们也没有指定，就会使用默认的，就不举例了，在后面文章讲解 analyzer 时再拓展。

# lenient

默认值是 `false` ， 表示用来在查询时如果数据类型不匹配且无法转换时会报错。如果设置成 `true` 会忽略错误。

# fuzzniess

`fuzziness` 参数可以使查询的字段具有模糊搜索的特性。

# prefix_length



# zero_terms_query

