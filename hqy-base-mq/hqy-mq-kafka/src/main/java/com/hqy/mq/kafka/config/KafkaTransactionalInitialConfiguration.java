package com.hqy.mq.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 大白话kafka架构原理 https://mp.weixin.qq.com/s?__biz=MzU1NDA0MDQ3MA==&mid=2247483958&idx=1&sn=dffaad318b50f875eea615bc3bdcc80c&chksm=fbe8efcfcc9f66d9ff096fbae1c2a3671f60ca4dc3e7412ebb511252e7193a46dcd4eb11aadc&scene=21#wechat_redirect
 * kafka ha 高可用 https://mp.weixin.qq.com/s?__biz=MzU1NDA0MDQ3MA==&mid=2247483965&idx=1&sn=20dd02c4bf3a11ff177906f0527a5053&chksm=fbe8efc4cc9f66d258c239fefe73125111a351d3a4e857fd8cd3c98a5de2c18ad33aacdad947&scene=21#wechat_redirect
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/10 14:36
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.kafka", name = "transactional", havingValue = "true" )
public class KafkaTransactionalInitialConfiguration {

    public static final String TRANSACTIONAL_TOPIC = "global-transaction-topic";

    public static final String TRANSACTIONAL_STORAGE_TOPIC = "global-transaction-storage";

    /**
     * 本地消息表 事务topic
     * 分区数   partition: 4
     * 副本数   replicationFactor 3
     * @return topic
     */
    @Bean
    public NewTopic transactionalTopic() {
        return new NewTopic(TRANSACTIONAL_TOPIC, 4, (short) 3);
    }

    @Bean
    public NewTopic transactionalStorageTopic() {
        return new NewTopic(TRANSACTIONAL_STORAGE_TOPIC, 4, (short) 3);
    }


}
