package com.hqy.cloud.rpc.nacos.client.support;

import cn.hutool.core.map.MapUtil;
import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.rpc.cluster.client.Client;
import com.hqy.cloud.rpc.config.EnvironmentConfiguration;
import com.hqy.cloud.rpc.config.model.ConsumerModel;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.model.*;
import com.hqy.cloud.rpc.nacos.core.NacosRPCModelUtil;
import com.hqy.cloud.rpc.nacos.core.NacosRPCStarter;
import com.hqy.cloud.rpc.nacos.node.Metadata;
import com.hqy.cloud.rpc.nacos.node.NacosServerInfo;
import com.hqy.cloud.rpc.registry.api.support.RegistryUtil;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringApplicationConfiguration;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.hqy.cloud.common.base.lang.exception.RpcException.UNKNOWN_EXCEPTION;
import static com.hqy.cloud.rpc.nacos.core.NacosRegistry.NAME;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/22 15:57
 */
@Configuration
@ConditionalOnNacosDiscoveryEnabled
@RequiredArgsConstructor
@AutoConfigureAfter({EnvironmentConfiguration.class})
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class, NacosDiscoveryClientConfiguration.class, SpringApplicationConfiguration.class})
public class NacosThriftClientStarterAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(NacosThriftClientStarterAutoConfiguration.class);

    @Value("${server.port}")
    private int port;

    @Bean
    @ConditionalOnMissingBean
    public Client client(NacosDiscoveryProperties properties) {
        // 构建注册中心Info
        RegistryInfo registryInfo = RegistryUtil.buildRegistryInfo(properties.getServerAddr(), NAME);
        // 构建消费者RPC地址数据
        RPCServerAddress consumerRpcServer = RPCServerAddress.createConsumerRpcServer(properties.getIp());
        // 构建rpc model.
        RPCModel rpcModel = NacosRPCModelUtil.buildRPCModel(properties.getService(), port, properties.getGroup(),
                registryInfo, consumerRpcServer, getAttachment());
        ProjectContextInfo.setBean(RPCModel.class, rpcModel);
        return NacosThriftRPCClient.of(rpcModel);
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosRPCStarter nacosThriftStarter(NacosDiscoveryProperties properties, Environment environment, Client client) {
        RPCModel rpcModel = ProjectContextInfo.getBean(RPCModel.class);
        if (rpcModel == null) {
            throw new IllegalArgumentException();
        }
        RPCServerAddress consumerRpcServer = rpcModel.getServerAddress();
        // 构建nacos metadata 暂时采用默认配置.
        Metadata metadata = NacosRPCModelUtil.buildMetadata(CommonConstants.DEFAULT_WEIGHT, CommonConstants.DEFAULT_HASH_FACTOR,
                ActuatorNode.CONSUMER, consumerRpcServer, environment, rpcModel.getParameters());
        // 构建ApplicationModel
        ModuleModel consumerModel = new ConsumerModel(rpcModel, client);
        ApplicationModel applicationModel = ApplicationModel.of(rpcModel, consumerModel);
        applicationModel.setActuatorNode(ActuatorNode.CONSUMER);
        return new NacosRPCStarter(applicationModel, new NacosServerInfo(rpcModel.getRegistryInfo(), properties.getGroup(), properties.getNamespace()), metadata);
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosDiscoveryProperties nacosDiscoveryProperties, NacosRPCStarter nacosRPCStarter, NacosServiceManager nacosServiceManager) {
        //register project context info.
        try {
            if (!nacosRPCStarter.start()) {
                log.error("Failed execute to start rpc server, starter = {}.", JsonUtil.toJson(nacosRPCStarter));
                throw new RpcException(UNKNOWN_EXCEPTION);
            }
            nacosDiscoveryProperties.setMetadata(nacosRPCStarter.getMetadata().toMetadataMap());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            ConfigurableApplicationContext cyx = (ConfigurableApplicationContext) SpringContextHolder.getApplicationContext();
            cyx.close();
            throw t;
        }
        return new NacosWatch(nacosServiceManager, nacosDiscoveryProperties);
    }

    private Map<String, String> getAttachment() {
        // FIXME 后续应该从配置中心中获取
        return MapUtil.newHashMap();
    }

}
