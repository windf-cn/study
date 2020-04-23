package cn.windf.study.middleware.kafka;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private AtomicReference<Integer> counter = new AtomicReference<>(0);

    private KafkaProducer<Integer, String> producer;

    public KafkaController() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.201:9092,192.168.1.202:9092,192.168.1.203:9092");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "test-producer");
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "cn.windf.study.middleware.kafka.MyPartition");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<>(properties);
    }

    @GetMapping("/producer")
    public String producer(String topic) {

        int oldValue, newValue;
        do {
            oldValue = counter.get();
            newValue = oldValue + 1;
        } while (!counter.compareAndSet(oldValue, newValue));

//        // 异步获取
//        producer.send(new ProducerRecord<>(topic, 1, "haha" + newValue), (metadata, exception) -> {
//            System.out.println(metadata + "-" + metadata.toString());
//        });
        // 同步获取
        String result = "";
        try {
            result = producer.send(new ProducerRecord<>(topic, newValue, "haha" + newValue)).get().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "message success，" + result;
    }

    @GetMapping("/consumer")
    public String consumer(String topic, String groupId) {

        if (StringUtils.isEmpty(groupId)) {
            groupId = "test_windf01";
        }

        String clientId = "test-consumer" + UUID.randomUUID().toString();

        Properties properties=new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.201:9092,192.168.1.202:9092,192.168.1.203:9092");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);    // 组id，同一个消息，在同一个组下，只能被消费一次
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");  // 超时时间
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000"); //自动提交(批量确认)
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest"); //这个属性. 它能够消费昨天发布的数据

        KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(properties);

        Executors.newFixedThreadPool(1).submit(() -> {
            consumer.subscribe(Collections.singleton(topic));
            consumer.listTopics();
            while(true) {
                ConsumerRecords<Integer, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));  // 每1秒获取一次
                consumerRecords.forEach((action) -> {
                    // 可以查看consumer的和partition的关系
                    System.out.println("consumerId:" + clientId + ":" + action.toString());
                });
            }
        });

        return "message is showing on the console";

    }
}
