package cn.windf.study.middleware.kafka;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class KafkaSpringBootListener {

    @KafkaListener(topics = {"test"})
    public void listener(ConsumerRecord record) {
        Optional message = Optional.ofNullable(record);
        if (message.isPresent()) {
            System.out.println(message.get());
        }
    }
}
