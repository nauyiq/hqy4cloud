package com.hqy.cloud.util.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;

/**
 * @author hongqy
 * @date 2026/1/28
 */
@Slf4j
public class SysPropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {

    @Override
    protected Properties mergeProperties() throws IOException {
        Properties properties = super.mergeProperties();
        SysProperty.init(properties);
        return properties;
    }




}
