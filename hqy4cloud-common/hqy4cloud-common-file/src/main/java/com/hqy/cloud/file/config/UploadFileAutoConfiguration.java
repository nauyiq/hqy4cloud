package com.hqy.cloud.file.config;

import com.hqy.cloud.file.api.UploadFileService;
import com.hqy.cloud.file.core.DefaultUploadFileService;
import com.hqy.cloud.file.domain.DomainServer;
import com.hqy.cloud.file.domain.support.DefaultDomainServer;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.unit.DataSize;


/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/4 14:27
 */
@Configuration
@EnableConfigurationProperties(UploadFileProperties.class)
public class UploadFileAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MultipartConfigElement multipartConfigElement(UploadFileProperties properties) {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        DataSize maxRequestSize = DataSize.ofBytes(properties.getMaxSize().toMillis());
        //文件最大
        factory.setMaxFileSize(maxRequestSize);
        //设置总上传数据总大小
        factory.setMaxRequestSize(maxRequestSize);
        return factory.createMultipartConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public DomainServer domainServer(Environment environment) {
        return new DefaultDomainServer(environment);
    }


    @Bean
    @ConditionalOnMissingBean
    public UploadFileService uploadFileService(UploadFileProperties uploadFileProperties) {
        return new DefaultUploadFileService(uploadFileProperties);
    }


}
