package com.hqy.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.hqy.gateway.nacos.NacosRouteDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 监听配置文件变化事件 实现动态路由配置类
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-17 10:58
 */
@Configuration
@ConditionalOnProperty(prefix = "gateway.dynamicRoute", name = "enabled", havingValue = "true")
public class DynamicRouteConfig {

    @Autowired
    private ApplicationEventPublisher publisher;


    @Configuration
    @ConditionalOnProperty(prefix = "gateway.dynamicRoute", name = "type", havingValue = "nacos", matchIfMissing = true)
    public class NacosDynamicRoute{

        @Autowired
        private NacosConfigProperties nacosConfigProperties;

        @Bean
        public NacosRouteDefinitionRepository repository() {
            return new NacosRouteDefinitionRepository(publisher, nacosConfigProperties);
        }

    }

}
