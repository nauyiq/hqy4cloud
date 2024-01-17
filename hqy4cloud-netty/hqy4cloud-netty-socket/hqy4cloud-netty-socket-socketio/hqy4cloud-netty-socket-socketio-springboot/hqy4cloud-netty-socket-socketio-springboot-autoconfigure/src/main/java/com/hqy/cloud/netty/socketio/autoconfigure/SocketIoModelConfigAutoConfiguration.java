package com.hqy.cloud.netty.socketio.autoconfigure;

import com.hqy.cloud.netty.socketio.deloyer.SocketIoServerModel;
import com.hqy.cloud.netty.socketio.listener.SocketIoEventListener;
import com.hqy.cloud.netty.socketio.properties.SocketIoServerProperties;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.foundation.authorization.AuthorizationService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import static com.hqy.cloud.registry.common.Constants.CONFIGURATION_PREFIX_COMPONENTS;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SocketIoServerProperties.class)
public class SocketIoModelConfigAutoConfiguration implements BeanFactoryAware {
    private ConfigurableListableBeanFactory factory;

    @Value("${server.port}")
    private int port;

    @Bean
    @ConditionalOnBean(ApplicationModel.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIGURATION_PREFIX_COMPONENTS, name = "socketio.enabled", matchIfMissing = true)
    public SocketIoServerModel socketIoModel(ApplicationModel applicationModel, SocketIoServerProperties properties, AuthorizationService authorizationService) {
        SocketIoServerModel model = new SocketIoServerModel(applicationModel.getApplicationName());
        model.setPort(properties.getPort() == 0 ? port + 100 : properties.getPort());
        model.setContext(properties.getContext());
        model.setCluster(properties.isCluster());
        model.setAuthorizationService(authorizationService);
        Map<String, SocketIoEventListener> eventListeners = factory.getBeansOfType(SocketIoEventListener.class);
        model.setSocketIoEventListeners(new HashSet<>(eventListeners.values()));
        return model;
    }

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.factory = (ConfigurableListableBeanFactory) beanFactory;
    }

}
