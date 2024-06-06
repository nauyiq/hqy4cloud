package com.hqy.cloud.mq.rocket.config;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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


}
