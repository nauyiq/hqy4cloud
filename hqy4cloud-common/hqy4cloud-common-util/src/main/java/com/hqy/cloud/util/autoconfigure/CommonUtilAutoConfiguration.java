package com.hqy.cloud.util.autoconfigure;

import com.hqy.cloud.util.authentication.AuthorizationService;
import com.hqy.cloud.util.authentication.support.JwtAuthorizationService;
import com.hqy.cloud.util.config.SysPropertyPlaceholderConfigurer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/1
 */
@Configuration(proxyBeanMethods = false)
public class CommonUtilAutoConfiguration {

    private static final Log log = LogFactory.getLog(CommonUtilAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationService authorizationService() {
        return new JwtAuthorizationService();
    }

    @Bean
    @ConditionalOnMissingBean
    public SysPropertyPlaceholderConfigurer sysPropertyPlaceholderConfigurer() throws IOException {
        SysPropertyPlaceholderConfigurer configurer = new SysPropertyPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:properties/*.properties");
            if (resources.length > 0) {
                configurer.setLocations(resources);
            }
        } catch (Exception e) {
            log.info("未发现properties文件");
        }
        configurer.setFileEncoding("UTF-8");
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }


}
