package com.hqy.cloud.gateway;

import com.hqy.cloud.registry.config.deploy.EnableDeployClient;
import com.hqy.cloud.sentinel.config.SentinelAutoConfiguration;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 全局网关服务...启动类...
 * @author qiyuan.hong
 * @date 2021/7/25 19:08
 */
@EnableDeployClient
@EnableDiscoveryClient
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, SentinelAutoConfiguration.class})
public class GatewayMain {

    public static void main(String[] args) {
        SpringApplication.run(GatewayMain.class, args);
//        ProjectContextInfo.startPrintf();
    }


}
