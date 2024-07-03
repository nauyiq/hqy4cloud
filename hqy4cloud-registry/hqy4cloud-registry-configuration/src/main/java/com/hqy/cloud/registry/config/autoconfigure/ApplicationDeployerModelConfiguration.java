package com.hqy.cloud.registry.config.autoconfigure;

import com.hqy.cloud.lock.service.LockService;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.cluster.MasterElectionService;
import com.hqy.cloud.registry.cluster.support.LockMasterServiceImpl;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.DeployModel;
import com.hqy.cloud.registry.config.deploy.AutoApplicationDeployerProperties;
import com.hqy.cloud.registry.context.RegistryContext;
import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.hqy.cloud.registry.common.Constants.CONFIGURATION_PREFIX;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AutoApplicationDeployerProperties.class)
@ConditionalOnProperty(prefix = CONFIGURATION_PREFIX, name = "enabled", matchIfMissing = true)
public class ApplicationDeployerModelConfiguration implements InitializingBean, BeanFactoryAware {
    private ConfigurableListableBeanFactory factory;

    @Bean
    @ConditionalOnMissingBean
    public MasterElectionService masterElectionService(LockService lockService, Registry registry) {
        return new LockMasterServiceImpl(lockService, registry);
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistryContext registryContext(ApplicationModel model, Registry registry, MasterElectionService masterElectionService) {
        return new RegistryContext(model, registry, masterElectionService);
    }

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.factory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // get deploy models
        Map<String, DeployModel> models = this.factory.getBeansOfType(DeployModel.class);
        models.values().forEach(RegistryContext::addDeployModel);
    }
}
