package com.hqy.cloud.rpc.server.config;

import com.hqy.cloud.rpc.server.core.ThriftServerWrapper;
import com.hqy.cloud.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.cloud.rpc.thrift.support.ThriftServerProperties;
import com.hqy.cloud.thrift.core.ThriftServerModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * start thrift rpc server. {@link ThriftServerWrapper}
 * project must create bean impl {@link ThriftServerLauncher}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/14 9:39
 */
@Configuration
@EnableConfigurationProperties(ThriftServerProperties.class)
public class ThriftServerAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ThriftServerLauncher.class)
    public ThriftServerModel thriftServerModel(ThriftServerLauncher thriftServerLauncher, ThriftServerProperties thriftServerProperties) {
        return new ThriftServerModel(thriftServerLauncher.getRpcServices(), thriftServerLauncher.getThriftServerEventHandlerServices(), thriftServerProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ThriftServerModel.class)
    public ThriftServerWrapper thriftServer(ThriftServerModel thriftServerModel) {
        return new ThriftServerWrapper(thriftServerModel);
    }





}
