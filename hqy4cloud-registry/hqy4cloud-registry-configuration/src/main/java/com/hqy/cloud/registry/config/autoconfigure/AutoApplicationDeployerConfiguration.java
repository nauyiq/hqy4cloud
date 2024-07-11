package com.hqy.cloud.registry.config.autoconfigure;

import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.registry.context.RegistryContext;
import com.hqy.cloud.registry.deploy.ApplicationDeployModel;
import com.hqy.cloud.registry.deploy.ApplicationLifecycleDeployer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hqy.cloud.registry.common.Constants.CONFIGURATION_PREFIX;

/**
 * @see com.hqy.cloud.registry.context.ProjectContext
 * start application deploy.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/15
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ApplicationDeployerModelConfiguration.class)
@ConditionalOnProperty(prefix = CONFIGURATION_PREFIX, name = "enabled", matchIfMissing = true)
public class AutoApplicationDeployerConfiguration {

    @Bean
    public ApplicationLifecycleDeployer applicationLifecycleDeployer(RegistryContext registryContext) {
        ApplicationDeployModel deployModel = new ApplicationDeployModel(registryContext);
        ApplicationLifecycleDeployer deployer = new ApplicationLifecycleDeployer(deployModel);
        BeanRepository.getInstance().register(ApplicationLifecycleDeployer.class, deployer);
        return deployer;
    }

    @Bean
    public ProjectContext projectContext(RegistryContext registryContext, ApplicationLifecycleDeployer applicationLifecycleDeployer) {
        return new ProjectContext(registryContext, applicationLifecycleDeployer);
    }

}
