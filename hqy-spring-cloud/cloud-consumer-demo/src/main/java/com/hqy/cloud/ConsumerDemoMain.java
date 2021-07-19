package com.hqy.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author qy
 * @create 2021/7/15 23:34
 */
@SpringBootApplication
@EnableFeignClients //表示启用openFeign客户端
@EnableEurekaClient
public class ConsumerDemoMain {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerDemoMain.class, args);
    }

}
