package com.hqy.cloud.limit.autoconfigure;

import com.hqy.cloud.limit.api.ManualWhiteIpService;
import com.hqy.cloud.limit.core.BiBlockedIpRedisService;
import com.hqy.cloud.limit.core.ManualBlockedIpService;
import com.hqy.cloud.limit.core.ManualWhiteIpRedisService;
import com.hqy.cloud.limit.flow.FlowConfigProperties;
import com.hqy.cloud.limit.flow.HttpAccessFlowControlCenter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
@Configuration
@EnableConfigurationProperties(FlowConfigProperties.class)
public class LimiterAutoConfiguration {

    @Bean
    @ConditionalOnBean
    public ManualWhiteIpService manualWhiteIpService(RedissonClient redisson) {
        return new ManualWhiteIpRedisService(redisson);
    }

    @Bean
    @Lazy
    @ConditionalOnBean
    @ConditionalOnMissingBean
    public BiBlockedIpRedisService biBlockedIpRedisService(RedissonClient redissonClient) {
        return new BiBlockedIpRedisService(redissonClient);
    }

    @Bean
    @Lazy
    @ConditionalOnBean
    @ConditionalOnMissingBean
    public ManualBlockedIpService manualBlockedIpService(RedissonClient redissonClient) {
        return new ManualBlockedIpService(redissonClient);
    }

    @Bean
    @Lazy
    @ConditionalOnBean
    public HttpAccessFlowControlCenter httpAccessFlowControlCenter(FlowConfigProperties properties) {
        return new HttpAccessFlowControlCenter(properties);
    }


}
