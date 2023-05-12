package com.hqy.cloud.rpc.server.config;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.cloud.common.base.lang.ActuatorNodeEnum;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.rpc.model.RegistryInfo;
import com.hqy.cloud.rpc.nacos.NacosThriftStarter;
import com.hqy.cloud.rpc.resgitry.node.NacosServerInfo;
import com.hqy.cloud.rpc.server.core.ThriftServerWrapper;
import com.hqy.cloud.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/22 15:46
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class, NacosDiscoveryClientConfiguration.class})
public class NacosThriftServerStarterAutoConfiguration {

    @Value("${server.port}")
    private int port;
    private final Environment environment;

    private static final Logger log = LoggerFactory.getLogger(NacosThriftServerStarterAutoConfiguration.class);

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public NacosThriftStarter nacosThriftStarter(ThriftServerLauncher thriftServerLauncher, NacosDiscoveryProperties properties, ThriftServerWrapper thriftServer) {
        RegistryInfo registryInfo = NacosThriftStarter.buildRegistryInfo(properties.getServerAddr());
        NacosServerInfo nacosServerInfo = new NacosServerInfo(registryInfo, properties.getGroup(), properties.getNamespace());
        Map<String, String> params = thriftServerLauncher.getParams();
        return new NacosThriftStarter(properties.getService(), port, nacosServerInfo, thriftServerLauncher.getWight(),
                ActuatorNodeEnum.PROVIDER, thriftServerLauncher.getHashFactor(), environment, params) {
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
    public NacosWatch nacosWatch(NacosDiscoveryProperties nacosDiscoveryProperties, NacosThriftStarter nacosThriftStarter, NacosServiceManager nacosServiceManager) {
        //register project context info.
        try {
            nacosThriftStarter.registerProjectContextInfo();
            Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
            Map<String, String> metadataMap = nacosThriftStarter.getMetadata().toMetadataMap();
            if (MapUtils.isNotEmpty(metadata)) {
                metadataMap.putAll(metadata);
            }
            nacosDiscoveryProperties.setMetadata(metadataMap);
            return new NacosWatch(nacosServiceManager, nacosDiscoveryProperties);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            ConfigurableApplicationContext cyx = (ConfigurableApplicationContext) SpringContextHolder.getApplicationContext();
            cyx.close();
            throw t;
        }

    }


}
