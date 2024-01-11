package com.hqy.cloud.registry.config.autoconfigure;

import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.cluster.MasterElectionService;
import com.hqy.cloud.registry.cluster.support.LockMasterServiceImpl;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.DeployModel;
import com.hqy.cloud.registry.config.deploy.AutoApplicationDeployerProperties;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.registry.context.RegistryContext;
import com.hqy.cloud.registry.deploy.ApplicationDeployModel;
import com.hqy.cloud.registry.deploy.ApplicationLifecycleDeployer;
import com.hqy.foundation.lock.LockService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AutoApplicationDeployerProperties.class)
@ConditionalOnProperty(value = "hqy4cloud.application.deploy.enabled", matchIfMissing = true)
public class AutoApplicationDeployerConfiguration implements SmartInitializingSingleton, BeanFactoryAware {
    private ConfigurableListableBeanFactory factory;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({Registry.class, LockService.class})
    public MasterElectionService masterElectionService(LockService lockService, Registry registry) {
        return new LockMasterServiceImpl(lockService, registry);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({ApplicationModel.class, Registry.class, MasterElectionService.class})
    public RegistryContext registryContext(ApplicationModel model, Registry registry, MasterElectionService masterElectionService) {
        return new RegistryContext(model, registry, masterElectionService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RegistryContext.class})
    public ApplicationLifecycleDeployer applicationLifecycleDeployer(RegistryContext registryContext) {
        ApplicationDeployModel deployModel = new ApplicationDeployModel(registryContext);
        ApplicationLifecycleDeployer deployer = new ApplicationLifecycleDeployer(deployModel);
        BeanRepository.getInstance().register(ApplicationLifecycleDeployer.class, deployer);
        return deployer;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RegistryContext.class, ApplicationLifecycleDeployer.class})
    public ProjectContext projectContext(RegistryContext registryContext, ApplicationLifecycleDeployer applicationLifecycleDeployer) {
        return new ProjectContext(registryContext, applicationLifecycleDeployer);
    }


    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.factory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // get deploy models
        Map<String, DeployModel> models = this.factory.getBeansOfType(DeployModel.class);
        if (MapUtils.isNotEmpty(models)) {
            RegistryContext registryContext = this.factory.getBean(RegistryContext.class);
            models.values().forEach(registryContext::addDeployModel);
        }
    }
}
