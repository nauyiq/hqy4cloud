package com.hqy;

import com.hqy.util.JsonUtil;
import com.hqy.util.spring.EnableOrderContext;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 启动类必须放在包com.hqy下 不然很多bean会扫描不到 导致程序启动抛出not found bean
 * @EnableOrderContext 表示当前服务需要Spring容器优先创建SpringApplicationHolder bean.
 *
 * 账号授权服务 提供auth2授权、用户相关服务等<br>
 * @author qiyuan.hong
 * @date 2022-03-10 21:43
 */
@MapperScan(basePackages = "com.hqy.account.dao")
@EnableOrderContext
@SpringBootApplication
@EnableDiscoveryClient
public class AccountServiceMain {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceMain.class);

    public static void main(String[] args) {

        /*
        必须禁用springboot热部署 否则会导致 thrift rpc 调用时thriftMethodManager 进行codec 编码struct类时 抛出类转换异常。
        原因就是springboot 热部署会破坏类加载器的双亲委派机制 即springboot通过强行干预-- “截获”了用户自定义类的加载。
        （由jvm的加载器AppClassLoader变为springboot自定义的加载器RestartClassLoader，一旦发现类路径下有文件的修改，
        springboot中的spring-boot-devtools模块会立马丢弃原来的类文件及类加载器，重新生成新的类加载器来加载新的类文件，从而实现热部署。
        导致需要两个类对象的类全称虽然一致 但是类加载器不一致
         */
        System.setProperty("spring.devtools.restart.enabled", "false");
        //启动springboot
        SpringApplication.run(AccountServiceMain.class, args);

        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        log.info("############################## ############### ############### ###############");
        log.info("##### Server Started OK : uip = {} ", JsonUtil.toJson(projectContextInfo.getUip()));
        log.info("##### Server Started OK.");
        log.info("############################## ############### ############### ###############");

    }

}
