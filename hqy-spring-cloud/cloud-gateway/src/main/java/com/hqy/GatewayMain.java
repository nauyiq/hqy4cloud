package com.hqy;

import com.hqy.util.JsonUtil;
import com.hqy.util.spring.EnableOrderContext;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 启动类必须放在包com.hqy下 不然很多bean会扫描不到 导致程序启动抛出not found bean
 * @EnableOrderContext 表示当前服务需要Spring容器优先创建SpringApplicationHolder bean.
 * 全局网关服务...启动类...
 * @author qiyuan.hong
 * @date 2021/7/25 19:08
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableOrderContext
public class GatewayMain {

    public static void main(String[] args) {    

        SpringApplication.run(GatewayMain.class, args);

        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        log.info("############################## ############### ############### ###############");
        log.info("##### Server Started OK : uip = {} ", JsonUtil.toJson(projectContextInfo.getUip()));
        log.info("##### Server Started OK.");
        log.info("############################## ############### ############### ###############");
    }

}
