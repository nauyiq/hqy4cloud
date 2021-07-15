package com.hqy.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author qy
 * @create 2021/7/15 21:30
 */
@SpringBootApplication
@EnableEurekaClient
public class EurekaClientMain {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientMain.class, args);
    }

}
