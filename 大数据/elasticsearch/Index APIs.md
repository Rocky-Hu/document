官方文档：https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html

# 一、创建索引API

~~~
# 创建索引
请求：
curl -X PUT "localhost:9200/hello"
返回：
{
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "hello"
}

# 创建带有类型、映射的索引(Index)
请求：
curl -X PUT "localhost:9200/hello1"
{
	"settings": {
		"number_of_shards": 3,
		"number_of_replicas": 2
	},
	"mappings": {
        "properties": {
            "commodity_id": {
                "type": "long"
            },
            "commodity_name": {
                "type": "text"
            },
            "picture_url": {
                "type": "keyword"
            },
            "price": {
                "type": "double"
            }
        }
	}
}
返回：
{
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "hello1"
}
~~~

# 二、删除索引

~~~
# 删除索引
请求:
DELETE  http://127.0.0.1:9200/hello
响应:
{
    "acknowledged": true
}
~~~

