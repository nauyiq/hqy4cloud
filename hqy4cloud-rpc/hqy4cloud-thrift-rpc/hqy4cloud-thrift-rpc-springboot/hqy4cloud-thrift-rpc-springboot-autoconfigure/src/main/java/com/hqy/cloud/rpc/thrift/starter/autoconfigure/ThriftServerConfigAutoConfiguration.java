package com.hqy.cloud.rpc.thrift.starter.autoconfigure;

import cn.hutool.core.net.NetUtil;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.model.RpcServerAddress;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.starter.model.RpcProviderDeployModel;
import com.hqy.cloud.rpc.starter.server.RpcServer;
import com.hqy.cloud.rpc.thrift.server.ThriftRpcServer;
import com.hqy.cloud.rpc.thrift.server.ThriftServerModel;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerProperties;
import com.hqy.cloud.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;

import static com.hqy.cloud.registry.common.Constants.CONFIGURATION_PREFIX_COMPONENTS;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ThriftServerProperties.class)
public class ThriftServerConfigAutoConfiguration implements BeanFactoryAware {
    private static final Logger log = LoggerFactory.getLogger(ThriftServerConfigAutoConfiguration.class);
    private ConfigurableListableBeanFactory factory;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIGURATION_PREFIX_COMPONENTS, name = "rpc-server.enabled", havingValue = "true")
    public ThriftServerModel thriftServerModel(RpcModel rpcModel, ThriftServerProperties serverProperties) {
        // setting params
        RpcServerAddress serverAddr = initRpcServerAddr(rpcModel, serverProperties);
        rpcModel.setServerAddress(serverAddr);
        serverProperties.setRpcPort(serverAddr.getPort());

        Map<String, RPCService> rpcServiceMap = factory.getBeansOfType(RPCService.class);
        Map<String, ThriftServerContextHandleService> handleServiceMap = factory.getBeansOfType(ThriftServerContextHandleService.class);
        return new ThriftServerModel(rpcModel, serverProperties, new ArrayList<>(rpcServiceMap.values()), new ArrayList<>(handleServiceMap.values()));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIGURATION_PREFIX_COMPONENTS, name = "rpc-server.enabled", havingValue = "true")
    public RpcServer rpcServer(ThriftServerModel thriftServerModel) {
        return new ThriftRpcServer(thriftServerModel);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIGURATION_PREFIX_COMPONENTS, name = "rpc-server.enabled", havingValue = "true")
    public RpcProviderDeployModel rpcProviderDeployModel(RpcModel rpcModel, RpcServer rpcServer) {
        return new RpcProviderDeployModel(rpcModel, rpcServer);
    }

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.factory = (ConfigurableListableBeanFactory) beanFactory;
    }


    private RpcServerAddress initRpcServerAddr(RpcModel rpcModel, ThriftServerProperties properties) {
        RpcServerAddress serverAddress = rpcModel.getServerAddress();
        String ip = serverAddress == null ? NetUtil.getLocalhostStr() : serverAddress.getHostAddr();
        int port = getEnableRpcPort(rpcModel.getModel().getPort(), properties);
        return new RpcServerAddress(port, ip);
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
