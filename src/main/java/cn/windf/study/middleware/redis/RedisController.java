package cn.windf.study.middleware.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/redis")
public class RedisController {

    /**
     * 手动连接sentinel,进行设置
     * @param name 要设置的redis的key
     * @param value 要设置的redis的值
     * @return 是否成功
     */
    @GetMapping("/sentinel/handwork")
    public String sentinelHandwork(String name, String value) {
        String masterName = "redis-master";
        Set<String> sentinels = new HashSet<>();
        sentinels.add("192.168.1.201:26379");
        sentinels.add("192.168.1.202:26379");
        sentinels.add("192.168.1.203:26379");

        JedisSentinelPool pool = new JedisSentinelPool(masterName, sentinels);

        Jedis jedis = pool.getResource();
        jedis.select(0);

        return jedis.set(name, value);
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取值
     * 使用springBoot的redis注入的模板
     * @param name key
     * @return 获取到的值
     */
    @GetMapping("/sentinel/autowired")
    public Object sentinelAutowired(String name) {
        return redisTemplate.opsForValue().get(name);
    }

}
