package com.hqy.cloud.auth;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.registry.config.deploy.EnableDeployClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 账号授权服务 提供auth2授权、用户相关服务等
 * @author qiyuan.hong
 * @date 2022-03-10 21:43
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableDeployClient(actuatorType = ActuatorNode.DUBBO_PROVIDER)
public class AccountAuthMain {

    public static void main(String[] args) {
        SpringApplication.run(AccountAuthMain.class, args);
    }


}
