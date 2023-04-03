package com.hqy.cloud.id;

import cn.hutool.core.map.MapUtil;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.PreservedMetadataKeys;
import com.hqy.cloud.id.service.RemoteLeafService;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 基础分布式id生成服务-rpc生产者视角
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 14:51
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.hqy.cloud.**.mapper", "com.hqy.cloud.**.**.mapper"})
public class IdServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(IdServiceMain.class, args);
        ProjectContextInfo.startPrintf();
    }

    @Component
    @RequiredArgsConstructor
    public static class ThriftServerRegisterServer implements ThriftServerLauncher {
        private final RemoteLeafService leafService;

        @Override
        public List<RPCService> getRpcServices() {
            return Collections.singletonList(leafService);
        }

        @Override
        public Map<String, String> getParams() {
            Map<String, String> params = MapUtil.newHashMap(1);
            params.put(PreservedMetadataKeys.INSTANCE_ID_GENERATOR, Constants.SNOWFLAKE_INSTANCE_ID_GENERATOR);
            return params;
        }
    }






}
