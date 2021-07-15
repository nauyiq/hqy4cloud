package com.hqy.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author qy
 * @create 2021/7/15 23:38
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProviderDemoMain {
    public static void main(String[] args) {
        SpringApplication.run(ProviderDemoMain.class, args);
    }
}
