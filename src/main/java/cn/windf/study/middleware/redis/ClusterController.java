package cn.windf.study.middleware.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

@RestController
@RequestMapping("/redis/cluster")
public class ClusterController {
    /**
     * cluster的手动方式
     * @param key   redis的key
     * @param value  redis的value，用于list结构，栈
     * @return 返回设置成功的标识
     */
    @GetMapping("/handwork")
    public Object clusterHandwork(String key, String value) {
        JedisCluster jedisCluster = new JedisCluster(new HostAndPort("192.168.1.202", 6379));
        return jedisCluster.set(key, value);
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * cluster通过注入操作
     * @param key 要获取的key
     * @return key对应的value
     */
    @GetMapping("/autowired")
    public Object clusterAutowired(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}
