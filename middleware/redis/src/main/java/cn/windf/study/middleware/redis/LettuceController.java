package cn.windf.study.middleware.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/redis/lettuce")
public class LettuceController {

    /**
     * 异步设置值，并且获取
     * @param key       同步设置的key
     * @param value     同步设置的value
     * @return          再次后去的value
     */
    @GetMapping("/async")
    public Object async(String key, String value) {
        RedisClient redisClient = RedisClient.create("redis://192.168.1.201:6381");
        try (StatefulRedisConnection<String, String> redisConnection = redisClient.connect()) {
            // 线程安全的长连接，连接丢失时会自动重连

            // 获取异步api
            RedisAsyncCommands<String, String> commands = redisConnection.async();
            commands.set(key, value);
            RedisFuture future = commands.get(key);

            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 同步设置，并且获取
     * @param key       要设置的key
     * @param value     要设置的value
     * @return          设置后，再次读取的value
     */
    @GetMapping("/sync")
    public Object rsync(String key, String value) {
        RedisClient redisClient = RedisClient.create("redis://192.168.1.201:6381");
        // 线程安全的长连接，连接丢失时会自动重连
        StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
        // 获取同步api
        RedisCommands<String, String> commands = redisConnection.sync();
        commands.set(key, value);
        return commands.get(key);
    }

    /**
     * 流式编程
     * @param key 要获取的key
     * @return redis中的value
     */
    @GetMapping("/reactive")
    public Object reactive(String key) {
        RedisClient redisClient = RedisClient.create("redis://192.168.1.201:6381");

        AtomicReference<String> result = new AtomicReference<>();
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringReactiveCommands<String, String> reactive = connection.reactive();

            reactive.get(key).subscribe(result::set);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } finally {
            redisClient.shutdown();
        }

        return result.get();

    }
}
