package com.hqy.cloud.admin;

import com.hqy.cloud.registry.config.deploy.EnableDeployClient;
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
@EnableDeployClient
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.hqy.cloud.**.mapper"})
@SpringBootApplication(scanBasePackages = {"com.hqy.cloud.auth", "com.hqy.cloud.admin"})
public class AdminServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceMain.class, args);
    }
}
