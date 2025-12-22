package com.hqy.cloud.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.hqy.cloud.gateway.nacos.NacosRouteDefinitionRepository;
import com.hqy.cloud.gateway.route.DynamicRouteConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 监听配置文件变化事件 实现动态路由配置类
 * @author qy
 * @date  2021-09-17 10:58
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DynamicRouteConfigProperties.class)
@ConditionalOnProperty(prefix = "spring.cloud.gateway.dynamic-route", name = "enabled", havingValue = "true")
public class DynamicRouteConfiguration {
    private final DynamicRouteConfigProperties properties;
    private final ApplicationEventPublisher publisher;

    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnProperty(prefix = "spring.cloud.gateway.dynamic-route", name = "type", havingValue = "nacos", matchIfMissing = true)
    public class NacosDynamicRoute {
        private final NacosConfigProperties nacosConfigProperties;

        @Bean
        public NacosRouteDefinitionRepository repository() {
            return new NacosRouteDefinitionRepository(properties.getDataId(), properties.getGroup(), publisher, nacosConfigProperties);
        }
    }

}
