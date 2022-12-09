package com.hqy;

import com.hqy.account.service.remote.AccountProfileRemoteService;
import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.util.spring.ProjectContextInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 启动类必须放在包com.hqy下 不然很多bean会扫描不到 导致程序启动抛出not found bean
 * 账号授权服务 提供auth2授权、用户相关服务等<br>
 * @author qiyuan.hong
 * @date 2022-03-10 21:43
 */
@MapperScan(basePackages = "com.hqy.*.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        ProjectContextInfo.startPrintf();
    }

    @Component
    @RequiredArgsConstructor
    public static class ThriftServerRegisterServer implements ThriftServerLauncher {

        private final AccountRemoteService accountRemoteService;
        private final AccountProfileRemoteService accountProfileRemoteService;

        @Override
        public List<RPCService> getRpcServices() {
            return Arrays.asList(accountRemoteService, accountProfileRemoteService);
        }
    }

}
