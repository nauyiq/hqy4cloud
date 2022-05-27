package com.hqy;

import com.hqy.util.spring.ProjectContextInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 启动类必须放在包com.hqy下 不然很多bean会扫描不到 导致程序启动抛出not found bean
 * 账号授权服务 提供auth2授权、用户相关服务等<br>
 * @author qiyuan.hong
 * @date 2022-03-10 21:43
 */
@MapperScan(basePackages = "com.hqy.*.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class AccountServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceMain.class, args);
        ProjectContextInfo.startPrintf();
    }

}
