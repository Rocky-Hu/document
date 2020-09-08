~~~java
public class RedissonListApp {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.147.128:6379");
        RedissonClient redissonClient = Redisson.create(config);

        RList<Integer> list = redissonClient.getList("redisson_list");
        list.add(1);// RPUSH
        list.get(0);// LINDEX
        list.remove(0);//LPOP
    }

}
~~~

