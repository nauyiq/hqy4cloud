package com.hqy.cloud.util.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * properties配置文件加载策略
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/4 10:20
 */
public class PropertyStrategy extends AbstractConfigStrategy {

    private static final Logger log = LoggerFactory.getLogger(PropertyStrategy.class);

    private Properties properties;

    protected PropertyStrategy(String propertyName) {
        super(propertyName);
    }

    @Override
    protected void loadConfig(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
