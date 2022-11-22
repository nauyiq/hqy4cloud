package com.hqy.rpc.nacos.starter;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.rpc.server.thrift.ThriftServerWrapper;
import com.hqy.rpc.thrift.service.ThriftServerLauncher;
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
import org.springframework.context.annotation.Primary;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/22 15:46
 */
@Configuration
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class, NacosDiscoveryClientConfiguration.class})
public class NacosThriftServerStarterAutoConfiguration {

    @Value("${server.port}")
    private int port;

    private static final Logger log = LoggerFactory.getLogger(NacosThriftServerStarterAutoConfiguration.class);

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public NacosThriftStarter nacosThriftStarter(ThriftServerLauncher thriftServerLauncher, NacosDiscoveryProperties properties, ThriftServerWrapper thriftServer) {
        return new NacosThriftStarter(properties.getService(), port, properties.getServerAddr(), thriftServerLauncher.getWight(), ActuatorNodeEnum.PROVIDER, thriftServerLauncher.getHashFactor(), properties.getGroup()) {
            @Override
            protected RPCServerAddress getRpcServerAddress() {
                return thriftServer.getServerAddr();
            }
        };
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosDiscoveryProperties nacosDiscoveryProperties, NacosThriftStarter nacosThriftStarter) {
        //register project context info.
        try {
            nacosThriftStarter.registerProjectContextInfo();
            nacosDiscoveryProperties.setMetadata(nacosThriftStarter.getMetadata().toMetadataMap());
            return new NacosWatch(nacosDiscoveryProperties);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            System.exit(1);
            return null;
        }

    }





}
