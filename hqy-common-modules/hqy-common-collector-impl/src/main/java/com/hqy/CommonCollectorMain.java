package com.hqy;

import com.hqy.util.JsonUtil;
import com.hqy.util.spring.EnableOrderContext;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 启动类必须放在包com.hqy下 不然很多bean会扫描不到 导致程序启动抛出not found bean
 * @EnableOrderContext 表示当前服务需要Spring容器优先创建SpringApplicationHolder bean.
 *
 * 提供各个模块的采集服务 <br>
 * 或对外暴露rest风格接口/RPC等服务 接收各模块的数据上报...
 * @author qy
 * @date  2021/8/19 22:13
 */
@Slf4j
@EnableOrderContext
@MapperScan(basePackages = "com.hqy.coll.*.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class CommonCollectorMain {


    public static void main(String[] args) {

        SpringApplication.run(CommonCollectorMain.class, args);

        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        log.info("############################## ############### ############### ###############");
        log.info("##### Server Started OK : uip = {} ", JsonUtil.toJson(projectContextInfo.getUip()));
        log.info("##### Server Started OK.");
        log.info("############################## ############### ############### ###############");

    }

}
