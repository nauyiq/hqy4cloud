package com.hqy.cloud.registry.nacos.starter.autoconfigure;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.api.RegistryFactory;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.api.Environment;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.PubMode;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import com.hqy.cloud.registry.config.deploy.AutoApplicationDeployerProperties;
import com.hqy.cloud.registry.nacos.Constants;
import com.hqy.cloud.registry.nacos.core.NacosRegistryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.hqy.cloud.registry.common.Constants.CONFIGURATION_PREFIX;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = CONFIGURATION_PREFIX, name = "enabled", matchIfMissing = true)
public class NacosApplicationAutoConfiguration {
    private final NacosServiceManager nacosServiceManager;
    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    @Value("${spring.profiles.active:dev}")
    private String env;
    @Value("${server.port}")
    private int port;

    @PostConstruct
    public void init() {
        // registry naming service to bean repository
        NamingMaintainService namingMaintainService = nacosServiceManager.getNamingMaintainService(nacosDiscoveryProperties.getNacosProperties());
        BeanRepository.getInstance().register(NamingMaintainService.class, namingMaintainService);
        NamingService namingService = nacosServiceManager.getNamingService();
        BeanRepository.getInstance().register(NamingService.class, namingService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({NacosDiscoveryProperties.class, AutoApplicationDeployerProperties.class})
    public ApplicationModel applicationModel(NacosDiscoveryProperties properties, AutoApplicationDeployerProperties applicationDeployerProperties) {
        ApplicationModel model = ApplicationModel.of(properties.getService(), properties.getNamespace(), properties.getGroup());
        model.setIp(properties.getIp());
        model.setPort(port);
        model.setStartupTimeMillis(System.currentTimeMillis());
        // build registry info
        RegistryInfo registryInfo = buildRegistryInfoByNacosDiscoveryProperties(properties);
        model.setRegistryInfo(registryInfo);
        // build metadata info
        Environment environment = new Environment(env);
        MetadataInfo metadataInfo = buildMetadataInfoByProperties(properties, applicationDeployerProperties, environment);
        model.setMetadataInfo(metadataInfo);
        return model;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({ApplicationModel.class})
    public Registry registry(ApplicationModel applicationModel) {
        RegistryFactory registryFactory = new NacosRegistryFactory();
        Registry registry = registryFactory.getRegistry(applicationModel);
        BeanRepository.getInstance().register(Registry.class, registry);
        return registry;
    }

    private MetadataInfo buildMetadataInfoByProperties(NacosDiscoveryProperties properties, AutoApplicationDeployerProperties applicationDeployerProperties, Environment environment) {
        return new MetadataInfo(properties.getService(), environment.getEnvironment(),
                applicationDeployerProperties.getActuatorType(), getPubModeByEnv(environment), applicationDeployerProperties.getRevision(),
                false, applicationDeployerProperties.getWeight());
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
