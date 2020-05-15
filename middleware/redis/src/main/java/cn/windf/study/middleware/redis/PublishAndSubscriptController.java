package cn.windf.study.middleware.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/redis")
public class PublishAndSubscriptController {

    private RedisClient redisClient = RedisClient.create("redis://192.168.1.201:6381");

    /**
     * 订阅消息
     * 消息通道 a
     * @return 最后获取的消息
     */
    @GetMapping("/subscribe")
    public String subscribe() {
        AtomicReference<String> result = new AtomicReference<>();

        RedisPubSubListener<String, String> listener = new RedisPubSubListener<String, String>() {
            @Override
            public void message(String pattern, String channel) {
                System.out.println("message:" + pattern + "," + channel);
                result.set(pattern);
            }

            @Override
            public void message(String pattern, String channel, String message) {
                System.out.println("message:" + pattern + "," + channel + "," + message);
                result.set(pattern);
            }

            @Override
            public void subscribed(String channel, long count) {
                System.out.println("subscribed:" + channel + "," + count);
            }

            @Override
            public void psubscribed(String pattern, long count) {
                System.out.println("psubscribed:" + pattern + "," + count);
            }

            @Override
            public void unsubscribed(String channel, long count) {
                System.out.println("unsubscribed:" + channel + "," + count);
            }

            @Override
            public void punsubscribed(String pattern, long count) {
                System.out.println("punsub:" + pattern + "," + count);
            }
        };

        try (StatefulRedisPubSubConnection<String, String> pubSubConnection = redisClient.connectPubSub()) {
            pubSubConnection.addListener(listener);

            RedisPubSubCommands<String, String> connection = pubSubConnection.sync();
            connection.subscribe("a");

            try {
                Thread.sleep(1000 * 10 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result.get();

    }

    /**
     * 发布消息
     * 消息通道a
     * @param value 要发布的内容
     * @return 订阅者的数量
     */
    @GetMapping("/publish")
    public Long subscript(String value) {
        Long count;

        try (StatefulRedisPubSubConnection<String, String>  pubSubConnection = redisClient.connectPubSub()) {

            RedisPubSubCommands<String, String> connection = pubSubConnection.sync();

            count = connection.publish("a", value);
        }

        return count;

    }
}
