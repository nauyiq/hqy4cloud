package com.hqy.cloud.rpc.server.config;

import cn.hutool.core.map.MapUtil;
import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.rpc.cluster.client.Client;
import com.hqy.cloud.rpc.config.model.ConsumerModel;
import com.hqy.cloud.rpc.config.model.ProducerModel;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.model.*;
import com.hqy.cloud.rpc.nacos.core.NacosRPCModelUtil;
import com.hqy.cloud.rpc.nacos.core.NacosRPCStarter;
import com.hqy.cloud.rpc.nacos.node.Metadata;
import com.hqy.cloud.rpc.nacos.node.NacosServerInfo;
import com.hqy.cloud.rpc.registry.api.support.RegistryUtil;
import com.hqy.cloud.rpc.server.core.NacosThriftRPCServer;
import com.hqy.cloud.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.cloud.rpc.thrift.support.ThriftServerProperties;
import com.hqy.cloud.thrift.core.ThriftServerModel;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

import static com.hqy.cloud.rpc.nacos.core.NacosRegistry.NAME;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/22 15:46
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class, NacosDiscoveryClientConfiguration.class})
public class NacosThriftServerStarterAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(NacosThriftServerStarterAutoConfiguration.class);

    @Value("${server.port}")
    private int port;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ThriftServerLauncher.class)
    public ThriftServerModel thriftServerModel(ThriftServerLauncher thriftServerLauncher, ThriftServerProperties thriftServerProperties) {
        return new ThriftServerModel(thriftServerLauncher.getRpcServices(), thriftServerLauncher.getThriftServerEventHandlerServices(), thriftServerProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ThriftServerModel.class)
    public NacosThriftRPCServer thriftServer(ThriftServerModel thriftServerModel, NacosDiscoveryProperties properties) {
        // 获取注册到nacos的IP
        String ip = properties.getIp();
        return new NacosThriftRPCServer(ip, port, thriftServerModel);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public NacosRPCStarter nacosThriftStarter(ThriftServerLauncher thriftServerLauncher, NacosDiscoveryProperties properties,
                                              NacosThriftRPCServer thriftServer, Environment environment, Client client) {
        // 获取参数配置.
        Map<String, String> attachment = getAttachment();
        // 构建注册中心Info
        RegistryInfo registryInfo = RegistryUtil.buildRegistryInfo(properties.getServerAddr(), NAME);
        // 获取生产者RPC地址数据
        RPCServerAddress serverAddr = thriftServer.getServerAddr();
        // 构建rpc model.
        RPCModel rpcModel = NacosRPCModelUtil.buildRPCModel(properties.getService(), port, properties.getGroup(),
                registryInfo, serverAddr, attachment);
        ProjectContextInfo.setBean(RPCModel.class, rpcModel);
        // 构建nacos metadata 暂时采用默认配置.
        Metadata metadata = NacosRPCModelUtil.buildMetadata(thriftServerLauncher.getWight(), thriftServerLauncher.getHashFactor(),
                ActuatorNode.PROVIDER, serverAddr, environment, attachment);
        // 构建ApplicationModel
        ApplicationModel applicationModel;
        ModuleModel providerModel = new ProducerModel(rpcModel, thriftServer);
        if (client != null) {
            applicationModel = ApplicationModel.of(rpcModel, providerModel, new ConsumerModel(rpcModel, client));
        } else {
            applicationModel = ApplicationModel.of(rpcModel, providerModel);
        }
        applicationModel.setActuatorNode(ActuatorNode.PROVIDER);
        return new NacosRPCStarter(applicationModel, new NacosServerInfo(registryInfo, properties.getGroup(), properties.getNamespace()), metadata);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosDiscoveryProperties nacosDiscoveryProperties, NacosRPCStarter nacosRPCStarter, NacosServiceManager nacosServiceManager) {
        //register project context info.
        try {
            nacosRPCStarter.start();
            Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
            Map<String, String> metadataMap = nacosRPCStarter.getMetadata().toMetadataMap();
            if (MapUtils.isNotEmpty(metadata)) {
                metadataMap.putAll(metadata);
            }
            nacosDiscoveryProperties.setMetadata(metadataMap);
            return new NacosWatch(nacosServiceManager, nacosDiscoveryProperties);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            ConfigurableApplicationContext cyx = (ConfigurableApplicationContext) SpringContextHolder.getApplicationContext();
            cyx.close();
            throw t;
        }

    }

    private Map<String, String> getAttachment() {
        // FIXME 后续应该从配置中心中获取
        return MapUtil.newHashMap();
    }


}
