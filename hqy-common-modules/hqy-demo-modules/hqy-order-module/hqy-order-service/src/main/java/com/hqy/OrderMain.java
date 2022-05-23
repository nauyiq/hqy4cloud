package com.hqy;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.hqy.util.spring.ProjectContextInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:50
 */
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.hqy.*.dao", "com.hqy.mq.common.mapper"})
@SpringBootApplication(exclude = { DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class })
public class OrderMain {

    public static void main(String[] args) {
        SpringApplication.run(OrderMain.class, args);
        ProjectContextInfo.startPrintf();
    }


}
