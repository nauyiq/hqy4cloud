package com.hqy.cloud.socketio.starter.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.foundation.common.route.LoadBalanceHashFactorManager;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.foundation.common.route.support.SocketPortRouterManager;
import com.hqy.cloud.socketio.starter.core.ServerLauncher;
import com.hqy.cloud.socketio.starter.core.SocketIoServerStarter;
import com.hqy.cloud.socketio.starter.core.support.ServerEventLauncherFactory;
import com.hqy.cloud.util.IpUtil;
import com.hqy.foundation.util.SocketHashFactorUtils;
import com.hqy.cloud.rpc.core.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SocketIoAutoConfiguration.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 13:27
 */
@Configuration
public class SocketIoAutoConfiguration {

    @Value("${server.port}")
    private int port;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(value = {SocketIoServerStarter.class})
    public SocketIOServer eventServerLauncher(SocketIoServerStarter starter) throws Exception {
        ServerEventLauncherFactory factory = new ServerEventLauncherFactory(starter.eventListeners());
        ServerLauncher launcher = factory.create();
        SocketIOServer socketIOServer = launcher.startUp(starter.serverPort(), starter.contextPath(), starter.authorizationListener());

        //registry socketIo Info to redis.
        SocketClusterStatusManager.registry(new SocketClusterStatus(starter.serviceName(),
                Environment.getInstance().getEnvironment(), starter.clusterNode(), starter.isCluster(), starter.contextPath()));

        //注册socket.io端口到redis. 方便gateway进行路由
        String hostAddress = IpUtil.getHostAddress();
        SocketPortRouterManager.registryPort(starter.serviceName(), SocketHashFactorUtils.genHashFactor(hostAddress, port), starter.serverPort());

        //集群启动时, 注册自身的hash因子到redis
        if (starter.isCluster()) {
            String hashFactor = SocketHashFactorUtils.genHashFactor(hostAddress, starter.serverPort());
            LoadBalanceHashFactorManager.registry(starter.serviceName(), starter.clusterHash(), hashFactor);
        }

        return socketIOServer;
    }

}
