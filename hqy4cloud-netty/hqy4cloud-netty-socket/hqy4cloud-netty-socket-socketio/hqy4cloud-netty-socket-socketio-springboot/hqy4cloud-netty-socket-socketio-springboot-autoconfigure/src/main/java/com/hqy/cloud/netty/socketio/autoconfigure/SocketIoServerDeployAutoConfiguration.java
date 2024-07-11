package com.hqy.cloud.netty.socketio.autoconfigure;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.netty.socketio.DefaultSocketIoServerFactory;
import com.hqy.cloud.netty.socketio.SocketIoServerFactory;
import com.hqy.cloud.netty.socketio.SocketIoSocketServer;
import com.hqy.cloud.netty.socketio.deloyer.SocketIoDeployModel;
import com.hqy.cloud.netty.socketio.deloyer.SocketIoServerModel;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hqy.cloud.registry.common.Constants.CONFIGURATION_PREFIX_COMPONENTS;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/12
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(SocketIoModelConfigAutoConfiguration.class)
public class SocketIoServerDeployAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIGURATION_PREFIX_COMPONENTS, name = "socketio.enabled", matchIfMissing = true)
    public SocketIoSocketServer socketServer(ApplicationModel applicationModel, SocketIoServerModel socketIoServerModel) {
        // 创建socketIO Server.
        SocketIoServerFactory socketIoServerFactory = new DefaultSocketIoServerFactory(socketIoServerModel);
        SocketIOServer socketIoServer = socketIoServerFactory.createSocketIoServer();
        return new SocketIoSocketServer(applicationModel, socketIoServer, socketIoServerModel);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIGURATION_PREFIX_COMPONENTS, name = "socketio.enabled", matchIfMissing = true)
    public SocketIoDeployModel socketIoDeployModel(ApplicationModel applicationModel, SocketIoSocketServer socketServer) {
        return new SocketIoDeployModel(applicationModel, socketServer);
    }



}
