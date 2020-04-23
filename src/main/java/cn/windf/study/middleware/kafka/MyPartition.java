package cn.windf.study.middleware.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.Random;

public class MyPartition implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        Integer len = cluster.partitionCountForTopic(topic);

        int result = 0;

        if (key == null) {
            result = new Random().nextInt(len);
        } else {
            result= Math.abs(key.hashCode()) % len;
        }

        System.out.println(value.toString() + "放到了分区：" + result);

        return result;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
