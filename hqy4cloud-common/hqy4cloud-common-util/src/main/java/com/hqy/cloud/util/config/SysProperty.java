package com.hqy.cloud.util.config;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Properties;

/**
 * @author hongqy
 * @date 2026/1/28
 */
@Slf4j
public class SysProperty {
    private static Properties property;

    protected static void init(Properties property) {
        Assert.notNull(property, "Properties cannot be null");
        SysProperty.property = property;
    }

    public static String get(String key) {
        return get(key, null);
    }

    public static String get(String key, String defaultValue) {
        if (StringUtils.isBlank(key)) {
            return defaultValue;
        }
        String value = property.getProperty(key);
        if (value == null) {
            return getByEnv(key, String.class, defaultValue);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> clazz, T defaultValue) {
        if (StringUtils.isBlank(key)) {
            return defaultValue;
        }
        Object value = property.get(key);
        if (value == null) {
            return getByEnv(key, clazz, defaultValue);
        }
        return (T) value;
    }



    private static <T> T getByEnv(String key, Class<T> targetClass, T defaultValue) {
        ApplicationContext context = SpringUtil.getApplicationContext();
        if (context == null) {
            return defaultValue;
        }
        Environment environment = context.getEnvironment();
        return environment.getProperty(key, targetClass, defaultValue);
    }




}
