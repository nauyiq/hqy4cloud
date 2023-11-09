package com.hqy.mq.kafka.lang;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/8 9:45
 */
public interface KafkaConstants {

    String PARTITION_KEY = "kafkaPartition";
    String TIMESTAMP_KEY = "kafkaTimestamp";

    String DEFAULT_CANAL_KAFKA_TOPIC = "canal-topic";
    String DEV_CANAL_KAFKA_TOPIC = "dev-canal-topic";
    String TEST_CANAL_KAFKA_TOPIC = "test-canal-topic";



}
