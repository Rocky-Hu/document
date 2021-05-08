# 示例一

~~~json
curl -X GET "localhost:9200/my_store/_analyze?pretty" -H 'Content-Type: application/json' -d'
{
  "field": "productID",
  "text": "XHDK-A-1293-#fJ3"
}
'
~~~

~~~json
{
  "tokens" : [
    {
      "token" : "xhdk",
      "start_offset" : 0,
      "end_offset" : 4,
      "type" : "<ALPHANUM>",
      "position" : 0
    },
    {
      "token" : "a",
      "start_offset" : 5,
      "end_offset" : 6,
      "type" : "<ALPHANUM>",
      "position" : 1
    },
    {
      "token" : "1293",
      "start_offset" : 7,
      "end_offset" : 11,
      "type" : "<NUM>",
      "position" : 2
    },
    {
      "token" : "fj3",
      "start_offset" : 13,
      "end_offset" : 16,
      "type" : "<ALPHANUM>",
      "position" : 3
    }
  ]
}
~~~

# 示例二

~~~json
curl -X GET "http://vpc-fat-133.zmaxis.com:9200/chat_message_v01_bak/_analyze?pretty" -H 'Content-Type: application/json' -d'
{
  "field": "content",
  "text": "你好啊"
}
'
~~~

~~~json
{
  "tokens" : [
    {
      "token" : "你好",
      "start_offset" : 0,
      "end_offset" : 2,
      "type" : "CN_WORD",
      "position" : 0
    },
    {
      "token" : "好啊",
      "start_offset" : 1,
      "end_offset" : 3,
      "type" : "CN_WORD",
      "position" : 1
    }
  ]
}
~~~

