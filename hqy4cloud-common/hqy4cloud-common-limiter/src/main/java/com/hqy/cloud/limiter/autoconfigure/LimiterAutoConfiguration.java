package com.hqy.cloud.limiter.autoconfigure;

import com.hqy.cloud.limiter.api.ManualWhiteIpService;
import com.hqy.cloud.limiter.core.BiBlockedIpRedisService;
import com.hqy.cloud.limiter.core.ManualBlockedIpService;
import com.hqy.cloud.limiter.core.ManualWhiteIpRedisService;
import com.hqy.cloud.limiter.flow.FlowConfigProperties;
import com.hqy.cloud.limiter.flow.HttpAccessFlowControlCenter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
@Configuration
@EnableConfigurationProperties(FlowConfigProperties.class)
public class LimiterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ManualWhiteIpService manualWhiteIpService(RedissonClient redisson) {
        return new ManualWhiteIpRedisService(redisson);
    }

    @Bean(name = "biBlockedIpRedisService")
    @ConditionalOnMissingBean
    public BiBlockedIpRedisService biBlockedIpRedisService(RedissonClient redissonClient) {
        return new BiBlockedIpRedisService(redissonClient);
    }

    @Bean(name = "manualBlockedIpService")
    @ConditionalOnMissingBean
    public ManualBlockedIpService manualBlockedIpService(RedissonClient redissonClient) {
        return new ManualBlockedIpService(redissonClient);
    }

    @Bean
    public HttpAccessFlowControlCenter httpAccessFlowControlCenter(FlowConfigProperties properties) {
        return new HttpAccessFlowControlCenter(properties);
    }


}
