package com.hqy;

import com.hqy.util.JsonUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.HashSet;
import java.util.Set;

/**
 * 启动类必须放在包com.hqy下 不然很多bean会扫描不到 导致程序启动抛出not found bean
 * 全局网关服务...启动类...
 * @author qiyuan.hong
 * @date 2021/7/25 19:08
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class GatewayMain {

    public static void main(String[] args) {

        /*
        必须禁用springboot热部署 否则会导致 thrift rpc 调用时thriftMethodManager 进行codec 编码struct类时 抛出类转换异常。
        原因就是springboot 热部署会破坏类加载器的双亲委派机制 即springboot通过强行干预-- “截获”了用户自定义类的加载。
        （由jvm的加载器AppClassLoader变为springboot自定义的加载器RestartClassLoader，一旦发现类路径下有文件的修改，
        springboot中的spring-boot-devtools模块会立马丢弃原来的类文件及类加载器，重新生成新的类加载器来加载新的类文件，从而实现热部署。
        导致需要两个类对象的类全称虽然一致 但是类加载器不一致
         */
        System.setProperty("spring.devtools.restart.enabled", "false");

        SpringApplication.run(GatewayMain.class, args);

        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();

        //TODO 后续再抽出一个基于redis和配置的 白名单
        Set<String> whiteSet = new HashSet<>();
        whiteSet.add("/oauth/**");
        whiteSet.add("/auth/**");
        whiteSet.add("/message/websocket/**");
        whiteSet.add("/payment/**");
        projectContextInfo.setProperties(ProjectContextInfo.WHITE_URI_PROPERTIES_KEY, whiteSet);

        log.info("############################## ############### ############### ###############");
        log.info("##### Server Started OK : uip = {} ", JsonUtil.toJson(projectContextInfo.getUip()));
        log.info("##### Server Started OK. serviceName = {}", projectContextInfo.getNameEn());
        log.info("############################## ############### ############### ###############");
    }

}
