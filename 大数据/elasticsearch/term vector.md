https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-termvectors.html#docs-termvectors-api-term-info

一段完整的term vector信息，term vector是按field为维度来统计的，主要包含三个部分：

- field statistics
- term statistics
- term information

# field statistics

指该索引和type下所有的document，对这个field所有term的统计信息，注意document的范围，不是某一条，是指定index/type下的所有document。

- sum_doc_freq(sum of document frequency)：这个field中所有的term的df之和。
- doc_count(document count)：有多少document包含这个field，有些document可能没有这个field。
- sum_ttf(sum of total term frequency)：这个field中所有的term的tf之和。

# term statistics

hello为当前document中，text field字段分词后的term，查询时设置term_statistics=true时生效。

- doc_freq(document frequency)：有多少document包含这个term。
- ttf(total term frequency)：这个term在所有document中出现的频率。
- term_freq(term frequency in the field)：这个term在当前document中出现的频率。

# term information

示例中tokens里面的内容，tokens里面是个数组

- position：这个term在field里的正排索引位置，如果有多个相同的term，tokens下面会有多条记录。
- start_offset：这个term在field里的偏移，表示起始位置偏移量。
- end_offset：这个term在field里的偏移量，表示结束位置偏移量。

