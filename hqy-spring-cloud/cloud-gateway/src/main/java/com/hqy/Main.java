package com.hqy;

import com.hqy.util.spring.ProjectContextInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 全局网关服务...启动类...
 * @author qiyuan.hong
 * @date 2021/7/25 19:08
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        ProjectContextInfo.startPrintf();
    }


}
