package com.hqy;

import com.hqy.communication.service.mail.EmailRemoteService;
import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.util.spring.ProjectContextInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 公共通讯服务 - 启动入口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 10:51
 */
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

        private final EmailRemoteService emailRemoteService;

        @Override
        public List<RPCService> getRpcServices() {
            return Collections.singletonList(emailRemoteService);
        }
    }


}
