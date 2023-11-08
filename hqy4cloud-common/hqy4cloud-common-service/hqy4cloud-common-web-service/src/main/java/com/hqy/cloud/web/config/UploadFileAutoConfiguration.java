package com.hqy.cloud.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/4 14:27
 */
@Configuration
@RequiredArgsConstructor
public class UploadFileAutoConfiguration {

    private final UploadFileProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        DataSize maxRequestSize = DataSize.ofBytes(properties.getMaxSize().toMillis());
        //文件最大
        factory.setMaxFileSize(maxRequestSize);
        //设置总上传数据总大小
        factory.setMaxRequestSize(maxRequestSize);
        return factory.createMultipartConfig();
    }

}
