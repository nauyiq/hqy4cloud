package com.hqy.cloud.registry.nacos.starter.autoconfigure;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.api.RegistryFactory;
import com.hqy.cloud.registry.common.context.Environment;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.PubMode;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import com.hqy.cloud.registry.config.deploy.AutoApplicationDeployerProperties;
import com.hqy.cloud.registry.nacos.Constants;
import com.hqy.cloud.registry.nacos.core.NacosRegistryFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "hqy4cloud.application.deploy.enabled", matchIfMissing = true)
public class NacosApplicationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Environment environment() {
        return new Environment();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({NacosDiscoveryProperties.class, AutoApplicationDeployerProperties.class})
    public ApplicationModel applicationModel(NacosDiscoveryProperties properties, AutoApplicationDeployerProperties applicationDeployerProperties, Environment environment) {
        ApplicationModel model = ApplicationModel.of(properties.getService(), properties.getNamespace(), properties.getGroup());
        model.setId(properties.getIp());
        model.setPort(properties.getPort());
        model.setStartupTimeMillis(System.currentTimeMillis());
        // build registry info
        RegistryInfo registryInfo = buildRegistryInfoByNacosDiscoveryProperties(properties);
        model.setRegistryInfo(registryInfo);
        // build metadata info
        MetadataInfo metadataInfo = buildMetadataInfoByProperties(properties, applicationDeployerProperties, environment);
        model.setMetadataInfo(metadataInfo);
        return model;
    }

    @Bean
    @ConditionalOnMissingBean
    public Registry registry(ApplicationModel applicationModel) {
        RegistryFactory registryFactory = new NacosRegistryFactory();
        return registryFactory.getRegistry(applicationModel);
    }

    private MetadataInfo buildMetadataInfoByProperties(NacosDiscoveryProperties properties, AutoApplicationDeployerProperties applicationDeployerProperties, Environment environment) {
        return new MetadataInfo(properties.getService(), environment.getEnvironment(),
                applicationDeployerProperties.getActuatorType(), getPubModeByEnv(environment), applicationDeployerProperties.getRevision(),false);
    }

    private PubMode getPubModeByEnv(Environment environment) {
        if (environment.isDevEnvironment() || environment.isUatEnvironment()) {
            return PubMode.GRAY;
        }
        return PubMode.WHITE;
    }

    private RegistryInfo buildRegistryInfoByNacosDiscoveryProperties(NacosDiscoveryProperties properties) {
        // nacos discovery
        String serverAddr = properties.getServerAddr();
        RegistryInfo registryInfo = new RegistryInfo(Constants.NACOS_NAME, serverAddr);
        String username = properties.getUsername();
        String password = properties.getPassword();
        registryInfo.setUsername(username);
        registryInfo.setPassword(password);
        return registryInfo;
    }


}
