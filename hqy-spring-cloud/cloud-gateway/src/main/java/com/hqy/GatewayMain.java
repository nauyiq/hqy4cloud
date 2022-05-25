package com.hqy;

import com.hqy.util.spring.ProjectContextInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.HashSet;
import java.util.Set;

/**
 * 启动类必须放在包com.hqy下 不然很多bean会扫描不到 导致程序启动抛出not found bean
 * 全局网关服务...启动类...
 * @author qiyuan.hong
 * @date 2021/7/25 19:08
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class GatewayMain {

    public static void main(String[] args) {

        SpringApplication.run(GatewayMain.class, args);
        ProjectContextInfo.startPrintf();
    }

}
