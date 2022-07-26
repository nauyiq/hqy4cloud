package com.hqy.mq.kafka.server;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * 自定义分区策略
 * kafka中的每个topic被划分为多个分区 而生产者在讲消息发送到topic时 是根据分区策略 来具体追加到哪个分区 </br>
 * kafka为我们提供了默认的分区策略, 同时它也支持自定义分区策略。其路由机制为:
 * 1. 若发送消息时指定了分区（即自定义分区策略），则直接将消息append到指定分区；
 *      启用分区策略必须在配置文件中声明生产者的自定义分区策略. 即
 *      spring.kafka.producer.properties.partitioner.class=com.hqy.mq.kafka.server.DefaultKafkaPartitioner
 * 2.若发送消息时未指定 Partition，但指定了 key（kafka允许为每条消息设置一个key），则对key值进行hash计算，根据计算结果路由到指定分区，这种情况下可以保证同一个 Key 的所有消息都进入到相同的分区；
 * 3. Partition 和 key 都未指定，则使用kafka默认的分区策略，轮询选出一个 Partition；
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/12 14:52
 */
public class DefaultKafkaPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
