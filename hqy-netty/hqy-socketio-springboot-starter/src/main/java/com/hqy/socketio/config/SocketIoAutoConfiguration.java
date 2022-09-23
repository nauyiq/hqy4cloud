package com.hqy.socketio.config;

import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.foundation.util.SocketHashFactorUtils;
import com.hqy.fundation.common.route.LoadBalanceHashFactorManager;
import com.hqy.fundation.common.route.SocketClusterStatus;
import com.hqy.fundation.common.route.SocketClusterStatusManager;
import com.hqy.rpc.common.config.EnvironmentConfig;
import com.hqy.socketio.ServerLauncher;
import com.hqy.socketio.SocketIOServer;
import com.hqy.socketio.SocketIoServerStarter;
import com.hqy.socketio.support.ServerEventLauncherFactory;
import com.hqy.util.IpUtil;
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
    @ConditionalOnBean(SocketIoServerStarter.class)
    @ConditionalOnMissingBean
    public SocketIOServer eventServerLauncher(SocketIoServerStarter socketIoServerStarter) throws Exception {
        ServerEventLauncherFactory factory = new ServerEventLauncherFactory(socketIoServerStarter.eventListeners());
        ServerLauncher launcher = factory.create();
        SocketIOServer socketIOServer = launcher.startUp(socketIoServerStarter.serverPort(), socketIoServerStarter.contextPath(), socketIoServerStarter.authorizationListener());

        //registry socketIo Info to redis.
        SocketClusterStatusManager.registry(new SocketClusterStatus(MicroServiceConstants.MESSAGE_NETTY_SERVICE,
                EnvironmentConfig.getInstance().getEnvironment(), socketIoServerStarter.clusterNode(), socketIoServerStarter.enableMultiNodes(), socketIoServerStarter.contextPath()));

        if (socketIoServerStarter.enableMultiNodes()) {
            String hashFactor = SocketHashFactorUtils.genHashFactor(IpUtil.getHostAddress(), socketIoServerStarter.serverPort());
            LoadBalanceHashFactorManager.registry(socketIoServerStarter.serviceName(), socketIoServerStarter.thisHash(), hashFactor);
        }

        return socketIOServer;
    }

}
