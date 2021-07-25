package com.hqy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author qy
 * @create 2021/7/25 23:40
 */
@SpringBootApplication
@EnableConfigServer
//@EnableDiscoveryClient
public class CloudConfigMain {

    public static void main(String[] args) {
        SpringApplication.run(EnableDiscoveryClient.class, args);
    }

}
