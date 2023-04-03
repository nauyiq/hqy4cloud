package com.hqy.cloud.socketio.starter.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.foundation.common.route.LoadBalanceHashFactorManager;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.socketio.starter.core.ServerLauncher;
import com.hqy.cloud.socketio.starter.core.SocketIoServerStarter;
import com.hqy.cloud.socketio.starter.core.support.ServerEventLauncherFactory;
import com.hqy.cloud.util.IpUtil;
import com.hqy.foundation.util.SocketHashFactorUtils;
import com.hqy.cloud.rpc.core.Environment;
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

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(value = {SocketIoServerStarter.class})
    public SocketIOServer eventServerLauncher(SocketIoServerStarter socketIoServerStarter) throws Exception {
        ServerEventLauncherFactory factory = new ServerEventLauncherFactory(socketIoServerStarter.eventListeners());
        ServerLauncher launcher = factory.create();
        SocketIOServer socketIOServer = launcher.startUp(socketIoServerStarter.serverPort(), socketIoServerStarter.contextPath(), socketIoServerStarter.authorizationListener());

        //registry socketIo Info to redis.
        SocketClusterStatusManager.registry(new SocketClusterStatus(socketIoServerStarter.serviceName(),
                Environment.getInstance().getEnvironment(), socketIoServerStarter.clusterNode(), socketIoServerStarter.enableMultiNodes(), socketIoServerStarter.contextPath()));

        if (socketIoServerStarter.enableMultiNodes()) {
            String hashFactor = SocketHashFactorUtils.genHashFactor(IpUtil.getHostAddress(), socketIoServerStarter.serverPort());
            LoadBalanceHashFactorManager.registry(socketIoServerStarter.serviceName(), socketIoServerStarter.thisHash(), hashFactor);
        }

        return socketIOServer;
    }

}
