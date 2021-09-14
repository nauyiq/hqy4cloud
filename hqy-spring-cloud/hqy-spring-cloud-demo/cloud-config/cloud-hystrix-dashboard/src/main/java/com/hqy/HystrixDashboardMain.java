package com.hqy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * hystrix_dashboard启动类
 * http://localhost:9000/hystrix
 * @author qy
 * @create 2021/7/25 16:09
 */
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashboardMain {

    public static void main(String[] args) {
        SpringApplication.run(HystrixDashboardMain.class, args);
    }

}
