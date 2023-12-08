package com.hqy.cloud.actuator.config;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.actuator.core.GradeSwitcherListener;
import com.hqy.cloud.actuator.endpoint.MicroServiceInfoContributorEndpoint;
import com.hqy.cloud.actuator.filter.support.EndpointBasicAuthorizationFilter;
import com.hqy.cloud.actuator.server.GradeSwitcherListenerRepository;
import com.hqy.cloud.actuator.service.BasicAuthorizationService;
import com.hqy.cloud.actuator.service.MicroServiceGradeManageService;
import com.hqy.cloud.actuator.service.impl.BasicAuthorizationServiceImpl;
import com.hqy.cloud.actuator.service.impl.MicroServiceGradeManageServiceImpl;
import com.hqy.cloud.rpc.nacos.discovery.NacosDiscovery;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 13:40
 */
@Configuration
public class ActuatorAutoConfiguration implements BeanFactoryAware, SmartInitializingSingleton {
    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Bean
    public MicroServiceGradeManageService microServiceGradeManageService(NacosDiscovery nacosDiscovery) {
        return new MicroServiceGradeManageServiceImpl(nacosDiscovery);
    }

    @Bean
    public MicroServiceInfoContributorEndpoint microServiceInfoContributorEndpoint() {
        return new MicroServiceInfoContributorEndpoint();
    }

    @Bean
    public BasicAuthorizationService basicAuthorizationService() {
        return new BasicAuthorizationServiceImpl();
    }

    @Bean
    public EndpointBasicAuthorizationFilter filter(BasicAuthorizationService basicAuthorizationService) {
        return new EndpointBasicAuthorizationFilter(basicAuthorizationService);
    }

    @Bean
    public FilterRegistrationBean<EndpointBasicAuthorizationFilter> webAuthFilterRegistration(EndpointBasicAuthorizationFilter filter) {
        FilterRegistrationBean<EndpointBasicAuthorizationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setName("EndpointBasicAuthorizationFilter");
        registration.addUrlPatterns("/actuator/*");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }

    public EndpointBasicAuthorizationFilter actuatorBasicFilter(BasicAuthorizationService basicAuthorizationService) {
        return new EndpointBasicAuthorizationFilter(basicAuthorizationService);
    }

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, GradeSwitcherListener> listenerMap = configurableListableBeanFactory.getBeansOfType(GradeSwitcherListener.class);
        if (MapUtil.isNotEmpty(listenerMap)) {
            for (GradeSwitcherListener listener : listenerMap.values()) {
                for (Integer switcherId : listener.supportSwitchers()) {
                    GradeSwitcherListenerRepository.getInstance().registryListener(switcherId, listener);
                }
            }
        }
    }
}
