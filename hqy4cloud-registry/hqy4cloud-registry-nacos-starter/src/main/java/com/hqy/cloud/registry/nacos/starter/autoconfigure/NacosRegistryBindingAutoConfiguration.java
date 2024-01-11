package com.hqy.cloud.registry.nacos.starter.autoconfigure;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.config.autoconfigure.AutoApplicationDeployerConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/5
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureAfter({AutoApplicationDeployerConfiguration.class, NacosApplicationAutoConfiguration.class})
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class, NacosDiscoveryClientConfiguration.class})
public class NacosRegistryBindingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({NacosDiscoveryProperties.class, NacosServiceManager.class, ApplicationModel.class})
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosDiscoveryProperties nacosDiscoveryProperties, NacosServiceManager nacosServiceManager, ApplicationModel applicationModel) {
        MetadataInfo metadataInfo = applicationModel.getMetadataInfo();
        Map<String, String> metadataMap = metadataInfo.getMetadataMap();
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        metadata.putAll(metadataMap);
        nacosDiscoveryProperties.setMetadata(metadata);
        return new NacosWatch(nacosServiceManager, nacosDiscoveryProperties);
    }



}
