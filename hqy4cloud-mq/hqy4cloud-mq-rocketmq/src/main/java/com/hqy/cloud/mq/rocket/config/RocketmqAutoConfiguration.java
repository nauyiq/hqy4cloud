package com.hqy.cloud.mq.rocket.config;

import com.hqy.cloud.mq.rocket.server.RocketmqProducer;
import com.hqy.cloud.mq.rocket.server.RocketmqProducerFactory;
import com.hqy.cloud.mq.rocket.server.StreamBridgeProducer;
import com.hqy.cloud.stream.api.StreamProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/10 17:00
 */
@Configuration
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
public class RocketmqAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public RocketmqProducer rocketmqProducer(RocketMQTemplate rocketMQTemplate) {
        // 创建配置类
        StreamProducer.Config config =  StreamProducer.Config.builder()
                .supportSendEmptyMessage(false)
                .supportAsyncApi(true).build();
        RocketmqProducerFactory factory = new RocketmqProducerFactory(rocketMQTemplate);
        return (RocketmqProducer) factory.create(config);
    }

    @Bean
    public StreamBridgeProducer streamBridgeProducer(StreamBridge bridge) {
        return new StreamBridgeProducer(bridge);
    }


}
