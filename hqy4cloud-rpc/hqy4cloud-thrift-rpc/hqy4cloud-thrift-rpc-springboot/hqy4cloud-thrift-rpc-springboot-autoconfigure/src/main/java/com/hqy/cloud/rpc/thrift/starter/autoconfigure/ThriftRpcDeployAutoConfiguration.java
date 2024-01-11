package com.hqy.cloud.rpc.thrift.starter.autoconfigure;

import com.hqy.cloud.common.base.lang.DeployComponent;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.starter.client.Client;
import com.hqy.cloud.rpc.starter.model.RpcConsumerDeployModel;
import com.hqy.cloud.rpc.starter.model.RpcProviderDeployModel;
import com.hqy.cloud.rpc.starter.server.RpcServer;
import com.hqy.cloud.rpc.thrift.client.ThriftRPCClient;
import com.hqy.cloud.rpc.thrift.server.ThriftRpcServer;
import com.hqy.cloud.rpc.thrift.server.ThriftServerModel;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ThriftRpcModelAutoConfiguration.class)
public class ThriftRpcDeployAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ThriftServerModel.class)
    @ConditionalOnProperty(value = {"hqy4cloud.application.deploy.components.rpc-sever-component.enabled"}, matchIfMissing = true)
    public RpcServer rpcServer(ThriftServerModel thriftServerModel) {
        return new ThriftRpcServer(thriftServerModel);
    }

    @Bean(name = DeployComponent.Constants.RPC_SERVER_COMPONENT)
    @ConditionalOnMissingBean(name = DeployComponent.Constants.RPC_SERVER_COMPONENT)
    @ConditionalOnBean({RpcModel.class, RpcServer.class})
    @ConditionalOnProperty(value = {"hqy4cloud.application.deploy.components.rpc-sever-component.enabled"}, matchIfMissing = true)
    public RpcProviderDeployModel rpcProviderDeployModel(RpcModel rpcModel, RpcServer rpcServer) {
        return new RpcProviderDeployModel(rpcModel, rpcServer);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RpcModel.class, Registry.class})
    @ConditionalOnProperty(value = {"hqy4cloud.application.deploy.components.rpc-client-component.enabled"}, matchIfMissing = true)
    public Client client(RpcModel rpcModel, Registry registry) {
        return new ThriftRPCClient(registry, rpcModel);
    }

    @Bean(name = DeployComponent.Constants.RPC_CLIENT_COMPONENT)
    @ConditionalOnMissingBean(name = DeployComponent.Constants.RPC_CLIENT_COMPONENT)
    @ConditionalOnProperty(value = {"hqy4cloud.application.deploy.components.rpc-client-component.enabled"}, matchIfMissing = true)
    @ConditionalOnBean(value = { RpcModel.class, Client.class })
    public RpcConsumerDeployModel rpcClientDeployModel(RpcModel rpcModel, Client client) {
        return new RpcConsumerDeployModel(rpcModel, client);
    }

}
