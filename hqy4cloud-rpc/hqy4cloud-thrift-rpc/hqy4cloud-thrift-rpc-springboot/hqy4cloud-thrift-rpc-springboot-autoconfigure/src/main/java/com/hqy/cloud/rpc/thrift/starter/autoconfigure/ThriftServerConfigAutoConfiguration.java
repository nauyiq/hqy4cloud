package com.hqy.cloud.rpc.thrift.starter.autoconfigure;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.rpc.model.RpcServerAddress;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.server.ThriftServerModel;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerProperties;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.NetUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@EnableConfigurationProperties(ThriftServerProperties.class)
@ConditionalOnProperty(value = {"hqy4cloud.application.deploy.components.rpc-sever-component.enabled"}, matchIfMissing = true)
public class ThriftServerConfigAutoConfiguration implements SmartInitializingSingleton, BeanFactoryAware {
    private static final Logger log = LoggerFactory.getLogger(ThriftServerConfigAutoConfiguration.class);
    private ConfigurableListableBeanFactory factory;

    private final ThriftServerProperties serverProperties;
    private final RpcModel rpcModel;

    @PostConstruct
    public void init() {
        RpcServerAddress serverAddr = initRpcServerAddr(rpcModel, serverProperties);
        rpcModel.setServerAddress(serverAddr);
        serverProperties.setRpcPort(serverAddr.getPort());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RpcModel.class})
    public ThriftServerModel thriftServerModel() {
        return new ThriftServerModel(rpcModel, serverProperties);
    }

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.factory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        ThriftServerModel model = factory.getBean(ThriftServerModel.class);
        // setting thrift rpc service
        Map<String, RPCService> rpcServiceMap = factory.getBeansOfType(RPCService.class);
        if (MapUtils.isNotEmpty(rpcServiceMap)) {
            model.setThriftRpcServices(new ArrayList<>(rpcServiceMap.values()));
        }
        // setting thrift rpc handlers
        Map<String, ThriftServerContextHandleService> handleServiceMap = factory.getBeansOfType(ThriftServerContextHandleService.class);
        if (MapUtils.isNotEmpty(handleServiceMap)) {
            model.setThriftServerContextHandleServices(new ArrayList<>(handleServiceMap.values()));
        }
        RpcServerAddress serverAddress = this.rpcModel.getServerAddress();
        UsingIpPort uip = new UsingIpPort(serverAddress.getHostAddr(), this.rpcModel.getModel().getPort(), serverAddress.getPort(), serverAddress.getPid());
        ProjectContext.getContextInfo().setUip(uip);
    }

    private RpcServerAddress initRpcServerAddr(RpcModel rpcModel, ThriftServerProperties properties) {
        RpcServerAddress serverAddress = rpcModel.getServerAddress();
        String ip = serverAddress == null ? IpUtil.getHostAddress() : serverAddress.getHostAddr();
        int pid = serverAddress == null ? NetUtils.getProgramId() : serverAddress.getPid();
        int port = getEnableRpcPort(rpcModel.getModel().getPort(), properties);
        return new RpcServerAddress(port, ip, pid);
    }

    private int getEnableRpcPort(int port, ThriftServerProperties properties) {
        int rpcPort = properties.getRpcPort();
        if (rpcPort == 0) {
            rpcPort = port + 10000;
        }
        int connectRetryTime = properties.getConnectRetryTime();
        int i = 0;
        while (NetUtils.isPortUsing(rpcPort)) {
            log.warn("@@@ The server port already bind! retry new port!!! [{}]", rpcPort);
            i++;
            if(i == connectRetryTime){
                throw new RpcException("Port is using! Server failed start after MAX_RETRY_TIMES:" + connectRetryTime);
            }else{
                rpcPort = rpcPort + 4;
            }
        }
        return rpcPort;
    }
}
