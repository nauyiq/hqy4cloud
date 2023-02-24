package com.hqy.rpc.nacos.client.starter.support;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.cloud.common.base.lang.ActuatorNodeEnum;
import com.hqy.rpc.common.CommonConstants;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.rpc.nacos.starter.NacosThriftStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/22 15:57
 */
@Configuration
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class, NacosDiscoveryClientConfiguration.class})
public class NacosThriftClientStarterAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(NacosThriftClientStarterAutoConfiguration.class);

    @Value("${server.port}")
    private int port;

    @Bean
    @ConditionalOnMissingBean
    public NacosThriftStarter nacosThriftStarter(NacosDiscoveryProperties properties) {
        return new NacosThriftStarter(properties.getService(), port, properties.getServerAddr(), CommonConstants.DEFAULT_WEIGHT, ActuatorNodeEnum.CONSUMER, CommonConstants.DEFAULT_HASH_FACTOR, properties.getGroup()) {
            @Override
            protected RPCServerAddress getRpcServerAddress() {
                return RPCServerAddress.createConsumerRpcServer();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosDiscoveryProperties nacosDiscoveryProperties, NacosThriftStarter nacosThriftStarter) {
        //register project context info.
        try {
            nacosThriftStarter.registerProjectContextInfo();
            nacosDiscoveryProperties.setMetadata(nacosThriftStarter.getMetadata().toMetadataMap());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            System.exit(0);
        }
        return new NacosWatch(nacosDiscoveryProperties);
    }

}
