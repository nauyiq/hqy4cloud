package com.hqy.cloud.rpc.thrift.starter.autoconfigure;

import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.starter.client.Client;
import com.hqy.cloud.rpc.starter.model.RpcConsumerDeployModel;
import com.hqy.cloud.rpc.thrift.client.ThriftRPCClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hqy.cloud.registry.common.Constants.CONFIGURATION_PREFIX_COMPONENTS;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ThriftRpcModelAutoConfiguration.class)
public class ThriftRpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIGURATION_PREFIX_COMPONENTS, name = "rpc-client.enabled", havingValue = "true")
    public Client client(RpcModel rpcModel, Registry registry) {
        ThriftRPCClient thriftRPCClient = new ThriftRPCClient(registry, rpcModel);
        BeanRepository.getInstance().register(Client.class, thriftRPCClient);
        return thriftRPCClient;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIGURATION_PREFIX_COMPONENTS, name = "rpc-client.enabled", havingValue = "true")
    public RpcConsumerDeployModel rpcClientDeployModel(RpcModel rpcModel, Client client) {
        return new RpcConsumerDeployModel(rpcModel, client);
    }

}
