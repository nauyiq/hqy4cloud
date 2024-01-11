package com.hqy.cloud.rpc.thrift.starter.autoconfigure;

import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.threadpool.DefaultExecutorRepository;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/9
 */
@Configuration(proxyBeanMethods = false)
public class ThriftRpcModelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({ApplicationModel.class})
    public RpcModel rpcModel(ApplicationModel model) {
        RpcModel rpcModel = new RpcModel(model);
        // create executor repository bean
        ExecutorRepository repository = new DefaultExecutorRepository(rpcModel);
        BeanRepository.getInstance().register(ExecutorRepository.class, repository);
        return rpcModel;
    }

}
