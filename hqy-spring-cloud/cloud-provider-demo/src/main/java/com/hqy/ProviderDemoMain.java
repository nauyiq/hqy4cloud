package com.hqy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author qy
 * @create 2021/7/15 23:38
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.hqy.cloud.dao")
@EnableCircuitBreaker
public class ProviderDemoMain {

    public static void main(String[] args) {
        SpringApplication.run(ProviderDemoMain.class, args);
    }


}
