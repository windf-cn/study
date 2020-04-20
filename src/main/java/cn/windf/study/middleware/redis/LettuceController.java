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

    @GetMapping("/async")
    public Object async(String key, String value) {
        RedisClient redisClient = RedisClient.create("redis://192.168.1.201:6381");
        // 线程安全的长连接，连接丢失时会自动重连
        StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
        // 获取异步api
        RedisAsyncCommands<String, String> commands = redisConnection.async();
        commands.set(key, value);
        RedisFuture future = commands.get(key);
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }


    @GetMapping("/sync")
    public Object rsync(String key, String value) {
        RedisClient redisClient = RedisClient.create("redis://192.168.1.201:6381");
        // 线程安全的长连接，连接丢失时会自动重连
        StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
        // 获取异步api
        RedisCommands<String, String> commands = redisConnection.sync();
        commands.set(key, value);
        return commands.get(key);
    }

    @GetMapping("/reactive")
    public Object reactive(String key) {
        RedisClient redisClient = RedisClient.create("redis://192.168.1.201:6381");

        StatefulRedisConnection<String, String> connection = redisClient.connect();

        AtomicReference<String> result = new AtomicReference();
        try {
            RedisStringReactiveCommands<String, String> reactive = connection.reactive();

            reactive.get(key).subscribe((String t) -> {
                result.set(t);
            });

            try {
                Thread.sleep(1000 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } finally {
            connection.close();
            redisClient.shutdown();
        }

        return result.get();

    }
}
