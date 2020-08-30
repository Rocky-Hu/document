https://www.electronjs.org/apps/etcd-manager



~~~
curl -L  https://github.com/coreos/etcd/releases/download/v2.3.7/etcd-v2.3.7-linux-amd64.tar.gz -o etcd-v2.3.7-linux-amd64.tar.gz
tar xzvf etcd-v2.3.7-linux-amd64.tar.gz
cd etcd-v2.3.7-linux-amd64
./etcd
~~~



远程访问https://www.cnblogs.com/chenqionghe/p/10503840.html



~~~
./etcd -listen-client-urls="http://0.0.0.0:2379" --advertise-client-urls="http://0.0.0.0:2379"
~~~

