package com.hqy.cloud.actuator;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/15 15:36
 */

@EnableAdminServer
@SpringBootApplication
public class ActuatorMain {

    public static void main(String[] args) {
        SpringApplication.run(ActuatorMain.class, args);
    }

}
