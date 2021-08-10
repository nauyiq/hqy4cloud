package com.hqy.basic;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 全局基础服务启动类
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-10 13:58
 */
@Slf4j
@MapperScan(basePackages = "com.hqy.basic.dao")
@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class BasicServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(BasicServiceMain.class, args);
    }

}
