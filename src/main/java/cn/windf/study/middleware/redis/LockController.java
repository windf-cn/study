package cn.windf.study.middleware.redis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/redis")
public class LockController {
    private JedisCluster jedisCluster = new JedisCluster(new HostAndPort("192.168.1.202", 6379));

    private int counter = 0;

    /**
     * 分布式锁的应用
     * @return
     */
    @GetMapping("/lock")
    public Object distributedLock(boolean needRelease) {
        String requestId = UUID.randomUUID().toString();

        int ncount = counter;

        // 验证锁
        String result = jedisCluster.set("redisLock", requestId, SetParams.setParams().nx().ex(1));
        if ("OK".equals(result)) {
            ncount = ++counter;
        }

        // 释放锁，通过lua脚本，保证原子性
        if (needRelease) {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            jedisCluster.eval(script, Collections.singletonList("redisLock"), Collections.singletonList(requestId));
        }

        return ncount;
    }

}
