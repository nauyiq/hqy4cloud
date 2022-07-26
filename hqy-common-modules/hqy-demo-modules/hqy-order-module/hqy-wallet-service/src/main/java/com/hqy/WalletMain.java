package com.hqy;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.hqy.common.service.WalletRemoteService;
import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.util.spring.ProjectContextInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:36
 */
@Slf4j
@EnableDiscoveryClient
@MapperScan(basePackages = "com.hqy.*.dao")
@SpringBootApplication(exclude = { DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class })
public class WalletMain {

    public static void main(String[] args) {
        SpringApplication.run(WalletMain.class, args);
        ProjectContextInfo.startPrintf();
    }

    @Component
    @RequiredArgsConstructor
    public static class ThriftServerRegisterServer implements ThriftServerLauncher {

        private final WalletRemoteService remoteService;

        @Override
        public List<RPCService> getRpcServices() {
            return Collections.singletonList(remoteService);
        }
    }

}
