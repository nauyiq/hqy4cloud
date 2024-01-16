package com.hqy.cloud.coll;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.registry.config.deploy.EnableDeployClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 提供各个模块的采集服务 <br>
 * 或对外暴露RPC服务 接收各模块的数据上报...
 * @author qy
 * @date 2021/8/19 22:13
 */
@SpringBootApplication
@EnableDeployClient(actuatorType = ActuatorNode.PROVIDER)
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.hqy.cloud.**.mapper"})
public class CollectorServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(CollectorServiceMain.class, args);
    }


}
