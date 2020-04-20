package cn.windf.study.middleware.redis;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("/redis/bloom")
public class BloomFilterController {

    private BloomFilter<String> bf;

    /**
     * 创建一个出书的布隆过滤器
     * @return 返回消耗的时间，和创建的数量的消息
     */
    @GetMapping("/init")
        public String init() {
        // 从数据库获取数据，加载到布隆过滤器
        long start = System.currentTimeMillis();

        long count = 100000;

        // 创建布隆过滤器，默认误判率0.03，即3%
        bf = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), count);
        // 误判率越低，数组长度越长，需要的哈希函数越多
        // bf = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), allUsers.size(), 0.0001);


        // 将数据存入布隆过滤器
        for (int i = 0; i < count; i++) {
            String id = UUID.randomUUID().toString();
            System.out.println(id);
            bf.put(id);
        }
        long end = System.currentTimeMillis();

        return "loading bloom success, count:"+ count +"，used ："+(end -start ) +"ms";
    }

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取redis的信息，同时添加布隆过滤器
     * @param key redis的key
     * @return 获取的value，以及是如何获取value的
     */
    @GetMapping("/get")
    public String get(String key) {
        // 如果布隆过滤器中不存在这个用户直接返回，将流量挡掉
        if (!bf.mightContain(key)) {
            return " not exists in bloom[" + key + "]，403";
        }

        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return "cached，value:" + value;
        }

        // TODO 防止并发重复写缓存（同时多个key不存在，可能会同时去查询），加锁
        synchronized (key) {
            value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return "cached，value:" + value;
            }

            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            redisTemplate.opsForValue().set(key, "haha" + key);

            return "not cache，select in database，save cache,value：" + ("haha" + key);
        }
    }

}
