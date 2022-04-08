package com.hqy;

import com.hqy.util.JsonUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:36
 */
@Slf4j
@EnableDiscoveryClient
@MapperScan(basePackages = "com.hqy.account.dao")
@SpringBootApplication
public class AccountMain {


    public static void main(String[] args) {
              /*
        必须禁用springboot热部署 否则会导致 thrift rpc 调用时thriftMethodManager 进行codec 编码struct类时 抛出类转换异常。
        原因就是springboot 热部署会破坏类加载器的双亲委派机制 即springboot通过强行干预-- “截获”了用户自定义类的加载。
        （由jvm的加载器AppClassLoader变为springboot自定义的加载器RestartClassLoader，一旦发现类路径下有文件的修改，
        springboot中的spring-boot-devtools模块会立马丢弃原来的类文件及类加载器，重新生成新的类加载器来加载新的类文件，从而实现热部署。
        导致需要两个类对象的类全称虽然一致 但是类加载器不一致
         */
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(AccountMain.class, args);

        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        log.info("############################## ############### ############### ###############");
        log.info("##### Server Started OK : uip = {} ", JsonUtil.toJson(projectContextInfo.getUip()));
        log.info("##### Server Started OK. serviceName = {}", projectContextInfo.getNameEn());
        log.info("############################## ############### ############### ###############");

    }






}
