package cn.windf.study.middleware.kafka;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/kafka/autoware")
public class KafkaSpringBootController {
    private AtomicReference<Integer> counter = new AtomicReference<>(0);

    @Resource
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @GetMapping("/producer")
    public String producer(String topic) {

        int oldValue, newValue;
        do {
            oldValue = counter.get();
            newValue = oldValue + 1;
        } while (!counter.compareAndSet(oldValue, newValue));

        // 同步获取
        String result = "";
        try {
            result = kafkaTemplate.send(new ProducerRecord<>(topic, 1, "haha" + newValue)).get().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "message success，" + result;
    }
}
