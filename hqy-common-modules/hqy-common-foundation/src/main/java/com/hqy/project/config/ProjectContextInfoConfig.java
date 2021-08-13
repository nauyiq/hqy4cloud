package com.hqy.project.config;

import com.hqy.project.ProjectContextInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-10 19:54
 */
@Configuration
@RefreshScope
public class ProjectContextInfoConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${env}")
    private String env;

    @Value("${server.port}")
    private Integer port;

    @Bean
    public ProjectContextInfo iniContextInfo() {

        return null;
    }





}
