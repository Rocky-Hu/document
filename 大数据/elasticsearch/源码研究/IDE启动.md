https://discuss.elastic.co/t/failing-to-run-on-intellij-in-debug-mode/227805/3

Your debug run configuration in IntelliJ needs two main items configured for it to work well with the `:run` task.

1. It needs to use "listen" mode instead of the default "attach".
2. "Auto restart" needs to be enabled.

~~~
./gradlew run --debug-jvm
~~~

用户名/密码：elastic/password

~~~json
MacBook-Pro:config rocky$ curl -u elastic:password -X GET "localhost:9200/?pretty"
{
  "name" : "runTask-0",
  "cluster_name" : "runTask",
  "cluster_uuid" : "bpi5QLB_RCu9Pa6hzJBzqw",
  "version" : {
    "number" : "7.12.1-SNAPSHOT",
    "build_flavor" : "default",
    "build_type" : "tar",
    "build_hash" : "unknown",
    "build_date" : "2021-05-29T08:09:45.784111Z",
    "build_snapshot" : true,
    "lucene_version" : "8.8.0",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
~~~

