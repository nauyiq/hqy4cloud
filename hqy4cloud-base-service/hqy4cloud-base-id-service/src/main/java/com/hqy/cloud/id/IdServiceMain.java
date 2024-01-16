package com.hqy.cloud.id;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.registry.config.deploy.EnableDeployClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 基础分布式id生成服务-rpc生产者视角
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 14:51
 */
@SpringBootApplication
@EnableDeployClient(actuatorType = ActuatorNode.PROVIDER)
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.hqy.cloud.**.mapper", "com.hqy.cloud.**.**.mapper"})
public class IdServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(IdServiceMain.class, args);
    }


}
