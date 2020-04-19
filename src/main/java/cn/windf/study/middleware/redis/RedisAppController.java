package cn.windf.study.middleware.redis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.SetParams;

@RestController
@RequestMapping("/redis")
public class RedisAppController {
    private JedisCluster jedisCluster = new JedisCluster(new HostAndPort("192.168.1.202", 6379));

    private int counter = 0;

    /**
     * 分布式锁的应用
     * @return
     */
    @GetMapping("/lock")
    public Object distributedLock() {
        int ncount = counter;
        String result = jedisCluster.set("redisLock", "1", SetParams.setParams().nx().ex(1));
        if ("OK".equals(result)) {
            ncount = ++counter;
        }

        return ncount;
    }

}
