package cn.windf.study.middleware.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisSentinelPool;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/redis")
public class RedisValueController {

    /**
     * 手动连接sentinel,进行设置
     * @param key 要设置的redis的key
     * @param value 要设置的redis的值
     * @return 是否成功
     */
    @GetMapping("/sentinel/handwork")
    public String sentinelHandwork(String key, String value) {
        String masterName = "redis-master";
        Set<String> sentinels = new HashSet<>();
        sentinels.add("192.168.1.201:26379");
        sentinels.add("192.168.1.202:26379");
        sentinels.add("192.168.1.203:26379");

        JedisSentinelPool pool = new JedisSentinelPool(masterName, sentinels);

        Jedis jedis = pool.getResource();
        jedis.select(0);

        return jedis.set(key, value);
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取值
     * 使用springBoot的redis注入的模板
     * @param key redis的key
     * @return 获取到的值
     */
    @GetMapping("/sentinel/autowired")
    public Object sentinelAutowired(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * cluster的手动方式
     * @param key   redis的key
     * @param value  redis的value，用于list结构，栈
     * @return
     */
    @GetMapping("/cluster/handwork")
    public Object clusterHandwork(String key, String value) {
        JedisCluster jedisCluster = new JedisCluster(new HostAndPort("192.168.1.202", 6379));
        return jedisCluster.set(key, value);
    }

    /**
     * cluster通过注入操作
     * @param key
     * @return
     */
    @GetMapping("/cluster/autowired")
    public Object clusterAutowired(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}
