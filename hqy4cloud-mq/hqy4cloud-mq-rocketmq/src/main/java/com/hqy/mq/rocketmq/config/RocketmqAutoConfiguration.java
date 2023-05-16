package com.hqy.mq.rocketmq.config;

import com.hqy.mq.rocketmq.server.RocketmqMessageProducer;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/10 17:00
 */
@Configuration
@RequiredArgsConstructor
public class RocketmqAutoConfiguration {

    private final RocketMQTemplate rocketMQTemplate;


    @Bean
    @ConditionalOnMissingBean
    public RocketmqMessageProducer rocketmqMessageProducer() {
        return new RocketmqMessageProducer(rocketMQTemplate);
    }


}
