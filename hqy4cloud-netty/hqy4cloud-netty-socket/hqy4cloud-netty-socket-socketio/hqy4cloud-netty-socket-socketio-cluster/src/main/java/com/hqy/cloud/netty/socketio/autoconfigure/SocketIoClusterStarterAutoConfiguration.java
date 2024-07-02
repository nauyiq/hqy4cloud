package com.hqy.cloud.netty.socketio.autoconfigure;

import com.hqy.cloud.netty.socketio.thrift.DefaultSocketIoThriftDiscovery;
import com.hqy.cloud.netty.socketio.thrift.SocketIoThriftDiscovery;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.rpc.starter.client.Client;
import com.hqy.cloud.socket.cluster.SocketCluster;
import com.hqy.cloud.socket.cluster.client.support.SocketClient;
import com.hqy.cloud.socket.cluster.support.HashSocketCluster;
import com.hqy.cloud.socket.cluster.support.SocketClusters;
import com.hqy.cloud.socket.cluster.HashRouterService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/15
 */
@Configuration(proxyBeanMethods = false)
public class SocketIoClusterStarterAutoConfiguration implements SmartInitializingSingleton, BeanFactoryAware {
    private ConfigurableListableBeanFactory factory;

    @Bean
    @ConditionalOnMissingBean
    public HashSocketCluster hashSocketCluster(HashRouterService hashRouterService) {
        return new HashSocketCluster(hashRouterService);
    }

    @Bean
    @ConditionalOnMissingBean
    public SocketClient socketClient(Registry registry) {
        return new SocketClient(registry);
    }

    @Bean
    @ConditionalOnMissingBean
    public SocketIoThriftDiscovery socketIoThriftDiscovery(Client client, SocketClient socketClient) {
        return new DefaultSocketIoThriftDiscovery(client, socketClient);
    }


    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.factory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, SocketCluster> clusters = factory.getBeansOfType(SocketCluster.class);
        if (MapUtils.isNotEmpty(clusters)) {
            clusters.values().forEach(cluster -> SocketClusters.registerCluster(cluster.getClusterName(), cluster));
        }
    }
}
