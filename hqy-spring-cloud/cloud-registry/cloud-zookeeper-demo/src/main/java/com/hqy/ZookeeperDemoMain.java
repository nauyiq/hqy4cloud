package com.hqy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author qy
 * @create 2021/7/15 22:08
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ZookeeperDemoMain {
    public static void main(String[] args) {
        SpringApplication.run(ZookeeperDemoMain.class, args);
    }

}
