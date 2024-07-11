package com.hqy.cloud.communication;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.registry.config.deploy.EnableDeployClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 公共通讯服务 - 启动入口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14
 */

@EnableDeployClient(actuatorType = ActuatorNode.DUBBO)
@SpringBootApplication
@EnableDiscoveryClient
public class CommunicationServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(CommunicationServiceMain.class, args);
    }


}
