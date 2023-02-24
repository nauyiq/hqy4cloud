package com.hqy;

import com.hqy.util.spring.ProjectContextInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * start admin-manager-service.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 16:44
 */
@MapperScan(basePackages = "com.hqy.*.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class AdminServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceMain.class, args);
        ProjectContextInfo.startPrintf();
    }
}
