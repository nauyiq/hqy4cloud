package com.hqy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author qy
 * @create 2021/8/4 23:08
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ConsumerDemoMain {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerDemoMain.class, args);
    }

}
