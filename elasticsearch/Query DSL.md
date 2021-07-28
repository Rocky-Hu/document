 All **query clauses** have either one of these two formats:

~~~json
{
  QUERY_CLAUSE: {
    ARGUMENT: VALUE,
    ARGUMENT: VALUE,...
  }
}
{
  QUERY_CLAUSE: {
    FIELD_NAME: {
      ARGUMENT: VALUE,
      ARGUMENT: VALUE,...
    }
  }
}
~~~

可以嵌套：

~~~json
{
  QUERY_CLAUSE {
    QUERY_CLAUSE: {
      QUERY_CLAUSE: {
        QUERY_CLAUSE: {
          ARGUMENT: VALUE,
          ARGUMENT: VALUE,...
        }
      }
    }
  }
}
~~~

# Match Query Clause

~~~
{ "match": { "description": "Fourier analysis signals processing" }}
{ "match": { "date": "2014-09-01" }}
{ "match": { "visible": true }}
~~~

# The Match All Query Clause

~~~
{ "match_all": {} }
~~~

# Term/Terms Query Clause

~~~
{ "term": { "tag": "math" }}
{ "terms": { "tag": ["math", "statistics"] }}
~~~

# Multi Match Query Clause

~~~json
{
  "multi_match": {
    "query": "probability theory",
    "fields": ["title", "body"]
  }
}
~~~

# **Exists and Missing Filters Query Clause**

~~~json
{
  "exists" : {
    "field" : "title"
  }
}
~~~

~~~json
{
  "missing" : {
    "field" : "title"
  }
}
~~~

# Range Filter Query Clause

~~~
{ "range" : { "age" : { "gt" : 30 } } }
{ 
  "range": {
    "born" : {
       "gte": "01/01/2012",
       "lte": "2013",
       "format": "dd/MM/yyyy||yyyy"
    }
  }
}
~~~

# Bool Query Clause

~~~json
{
    "bool": {
        "must": [
                  { "term": { "tag":    "math" }},
                  { "term": { "level": "beginner" }}
         ]
        "must_not": { "term": { "tag":    "probability"  }},
        "should": [
                    { "term": { "favorite": true   }},
                    { "term": { "unread":  true   }}
        ]
    }
}
~~~

类比SQL:

~~~sql
SELECT * FROM posts
WHERE posts.tag = 'math'
AND posts.level = 'beginner'
AND posts.tag != 'probability'
AND (posts.favorite IS true OR posts.unread IS true);
~~~



# 参考资料

https://user3141592.medium.com/understanding-the-elasticsearch-query-dsl-ce1d67f1aa5b

