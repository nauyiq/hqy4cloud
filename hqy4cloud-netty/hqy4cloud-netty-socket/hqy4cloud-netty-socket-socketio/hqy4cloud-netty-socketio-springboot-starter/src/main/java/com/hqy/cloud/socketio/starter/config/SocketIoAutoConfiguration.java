package com.hqy.cloud.socketio.starter.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.NamingService;
import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.foundation.common.route.LoadBalanceHashFactorManager;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.foundation.common.route.support.SocketPortRouterManager;
import com.hqy.cloud.rpc.nacos.core.NacosRPCStarter;
import com.hqy.cloud.socketio.starter.core.ServerLauncher;
import com.hqy.cloud.socketio.starter.core.SocketIoServerStarter;
import com.hqy.cloud.socketio.starter.core.support.NacosSocketIoEventListener;
import com.hqy.cloud.socketio.starter.core.support.ServerEventLauncherFactory;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import com.hqy.foundation.util.SocketHashFactorUtils;
import com.hqy.cloud.registry.common.context.Environment;
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
    public SocketIOServer eventServerLauncher(SocketIoServerStarter starter,
                                              NacosRPCStarter nacosRPCStarter,
                                              NacosServiceManager nacosServiceManager,
                                              NacosDiscoveryProperties properties) throws Exception {
        ServerEventLauncherFactory factory = new ServerEventLauncherFactory(starter.eventListeners());
        ServerLauncher launcher = factory.create();
        SocketIOServer socketIOServer = launcher.startUp(starter.serverPort(), starter.contextPath(), starter.authorizationListener());

        //registry socketIo Info to redis.
        SocketClusterStatusManager.registry(new SocketClusterStatus(starter.serviceName(),
                Environment.getInstance().getEnvironment(), starter.clusterNode(), starter.isCluster(), starter.contextPath()));

        //注册socket.iox项目端口到redis. 方便gateway进行路由
        String hostAddress = IpUtil.getHostAddress();
        SocketPortRouterManager.registryPort(starter.serviceName(), SocketHashFactorUtils.genHashFactor(hostAddress, port), starter.serverPort());

        ProjectContextInfo contextInfo = SpringContextHolder.getProjectContextInfo();
        UsingIpPort uip = contextInfo.getUip();


        if (starter.isCluster()) {
            //集群启动时, 注册自身的hash因子到redis
            int rpcPort = nacosRPCStarter.getMetadata().getRpcServerAddress().getPort();
            String hashFactor = SocketHashFactorUtils.genHashFactor(hostAddress, rpcPort);
            LoadBalanceHashFactorManager.registry(starter.serviceName(), starter.clusterHash(), hashFactor);

            //订阅当前服务的监听器
            NamingService namingService = nacosServiceManager.getNamingService();
            namingService.subscribe(starter.serviceName(), properties.getGroup(), new NacosSocketIoEventListener(starter));
        }
        return socketIOServer;
    }



}
