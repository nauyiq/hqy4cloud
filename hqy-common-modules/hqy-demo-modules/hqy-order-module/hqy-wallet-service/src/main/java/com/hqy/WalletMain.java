package com.hqy;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.hqy.util.spring.ProjectContextInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:36
 */
@Slf4j
@EnableDiscoveryClient
@MapperScan(basePackages = "com.hqy.*.dao")
@SpringBootApplication(exclude = { DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class })
public class WalletMain {

    public static void main(String[] args) {
        SpringApplication.run(WalletMain.class, args);
        ProjectContextInfo.startPrintf();
    }
}
