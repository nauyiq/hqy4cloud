package com.hqy.mq.collector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 提供各个模块的采集服务 <br>
 * 可基于rabbitmq监听服务上报到队列的数据。
 * 或对外暴露rest风格接口/RPC等服务 接收各模块的数据上报...
 * @author qy
 * @create 2021/8/19 22:13
 */

@Slf4j
@MapperScan(basePackages = "com.hqy.mq.collector.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class MqCollectorMain {


    public static void main(String[] args) {
        SpringApplication.run(MqCollectorMain.class, args);
        log.info("############################## ############### ############### ###############");
//        log.info("##### Server Started OK : listen on {} ,contextPath={}",port,contextPath);
        log.info("##### Server Started OK ");
        log.info("############################## ############### ############### ###############");

    }

}
