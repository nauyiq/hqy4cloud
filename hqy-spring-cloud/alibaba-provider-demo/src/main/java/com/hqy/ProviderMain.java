package com.hqy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author qy
 * @create 2021/8/4 22:24
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.hqy.cloud.dao")
public class ProviderMain {

    public static void main(String[] args) {
        SpringApplication.run(ProviderMain.class, args);
    }

}
