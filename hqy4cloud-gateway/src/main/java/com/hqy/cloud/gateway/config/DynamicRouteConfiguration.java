package com.hqy.cloud.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.hqy.cloud.gateway.nacos.NacosRouteDefinitionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 监听配置文件变化事件 实现动态路由配置类
 * @author qy
 * @date  2021-09-17 10:58
 */
@Configuration
@ConditionalOnProperty(prefix = "gateway.dynamicRoute", name = "enabled", havingValue = "true")
public class DynamicRouteConfiguration {

    @Resource
    private ApplicationEventPublisher publisher;

    @Configuration
    @ConditionalOnProperty(prefix = "gateway.dynamicRoute", name = "type", havingValue = "nacos", matchIfMissing = true)
    public class NacosDynamicRoute{

        @Value("${gateway.dynamicRoute.dataId:hqy4cloud-gateway-routing.json}")
        private String dataId;

        @Value("${gateway.dynamicRoute.group:DEV_GROUP}")
        private String group;

        @Resource
        private NacosConfigProperties nacosConfigProperties;

        @Bean
        public NacosRouteDefinitionRepository repository() {
            return new NacosRouteDefinitionRepository(dataId, group, publisher, nacosConfigProperties);
        }
    }

}
