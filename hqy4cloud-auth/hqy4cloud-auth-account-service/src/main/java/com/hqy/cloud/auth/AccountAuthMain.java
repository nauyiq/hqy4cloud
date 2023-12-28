package com.hqy.cloud.auth;

import com.hqy.cloud.account.service.RemoteAccountProfileService;
import com.hqy.cloud.account.service.RemoteAccountService;
import com.hqy.cloud.account.service.RemoteAuthService;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Arrays;
import java.util.List;

/**
 * 账号授权服务 提供auth2授权、用户相关服务等
 * @author qiyuan.hong
 * @date 2022-03-10 21:43
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.hqy.cloud.**.mapper"})
public class AccountAuthMain {

    public static void main(String[] args) {
        SpringApplication.run(AccountAuthMain.class, args);
        ProjectContextInfo.startPrintf();
    }

    @Component
    @RequiredArgsConstructor
    public static class ThriftServerRegisterServer implements ThriftServerLauncher {
        private final RemoteAccountService remoteAccountService;
        private final RemoteAccountProfileService remoteAccountProfileService;
        private final RemoteAuthService remoteAuthService;

        @Override
        public List<RPCService> getRpcServices() {
            return Arrays.asList(remoteAccountService, remoteAccountProfileService, remoteAuthService);
        }
    }

}
