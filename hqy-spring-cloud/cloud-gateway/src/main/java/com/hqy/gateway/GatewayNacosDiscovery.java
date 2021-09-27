package com.hqy.gateway;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.facebook.swift.service.ThriftServer;
import com.hqy.rpc.nacos.AbstractNacosClientWriter;
import com.hqy.rpc.nacos.NacosNode;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-24 18:01
 */
@Configuration
public class GatewayNacosDiscovery extends AbstractNacosClientWriter {


   /* @Bean
    @DependsOn("thriftServer")
    public NacosWatch nacosWatch(NacosDiscoveryProperties properties) {
        //清除掉所有数据
        ThriftServer thriftServer = SpringContextHolder.getBean(ThriftServer.class);
        if (!thriftServer.isRunning()) {
            thriftServer.start();
        }


        return null;
    }*/






}
