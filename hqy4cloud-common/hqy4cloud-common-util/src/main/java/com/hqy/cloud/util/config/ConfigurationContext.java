package com.hqy.cloud.util.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置文件上下文
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/4 10:45
 */
public class ConfigurationContext {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationContext.class);

    private ConfigurationContext() {}
    public static Map<PropertiesEnum, PropertyStrategy> propertiesMap = new ConcurrentHashMap<>();
    public static Map<YamlEnum, YamlStrategy> yamlMap = new ConcurrentHashMap<>();
    private static String baseUploadFilesDirectory = null;


    public static Properties getProperties(PropertiesEnum propertiesEnum) {
        PropertyStrategy propertyStrategy = propertiesMap.get(propertiesEnum);
        if (Objects.isNull(propertyStrategy)) {
            propertyStrategy =  new PropertyStrategy(propertiesEnum.fineName);
            propertiesMap.put(propertiesEnum, propertyStrategy);
        }
        return propertyStrategy.getProperties();
    }

    public static String getProperty(PropertiesEnum propertiesEnum, String key) {
        Properties properties = getProperties(propertiesEnum);
        return properties.getProperty(key);
    }

    public static String getProperty(PropertiesEnum propertiesEnum, String key, String defaultValue) {
        Properties properties = getProperties(propertiesEnum);
        return properties.getProperty(key, defaultValue);
    }


    public static Map<String, String> getYaml(YamlEnum yamlEnum) {
        YamlStrategy strategy = yamlMap.get(yamlEnum);
        if (Objects.isNull(strategy)) {
            strategy = new YamlStrategy(yamlEnum.fineName);
            yamlMap.put(yamlEnum, strategy);
        }
        return strategy.getData();
    }

    public static String getString(String filename, String key) {
        YamlEnum yamlEnum = YamlEnum.valueOf(filename);
        YamlStrategy strategy = yamlMap.get(yamlEnum);
        if (strategy != null) {
            return strategy.getData().get(key);
        }

        PropertiesEnum propertiesEnum = PropertiesEnum.valueOf(filename);
        PropertyStrategy propertyStrategy = propertiesMap.get(propertiesEnum);
        if (propertyStrategy != null) {
            return propertyStrategy.getProperties().getProperty(key);
        }

        return StrUtil.EMPTY;
    }


    public static String getString(YamlEnum yamlEnum, String key) {
        Map<String, String> yamlValues = getYaml(yamlEnum);
        return yamlValues.get(key);
    }


    public static String getString(YamlEnum yamlEnum, String key, String defaultValue) {
        String string = getString(yamlEnum, key);
        return StringUtils.isBlank(string) ? defaultValue : string;
    }


    public static String getConfigPath() {
        if (StringUtils.isBlank(baseUploadFilesDirectory)) {
            baseUploadFilesDirectory = SystemUtil.getOsInfo().isWindows() ? "C:/hongqy/data" : "/hongqy/share/data";
        }
        return baseUploadFilesDirectory;
    }


    public enum PropertiesEnum {

        /**
         * server.properties
         */
        SERVER_PROPERTIES("server.properties"),

        /**
         * application.properties
         */
        APPLICATION_PROPERTIES("application.properties"),

        /**
         * bootstrap.properties
         */
        BOOTSTRAP_PROPERTIES("bootstrap.properties"),

        /**
         * bootstrap-dev.properties
         */
        BOOTSTRAP_DEV_PROPERTIES("bootstrap-dev.properties"),

        /**
         * bootstrap-test.properties
         */
        BOOTSTRAP_TEXT_PROPERTIES("bootstrap-test.properties"),

        /**
         * bootstrap-prod.properties
         */
        BOOTSTRAP_PROD_PROPERTIES("bootstrap-prod.properties"),

        ;

        public final String fineName;

        PropertiesEnum(String fineName) {
            this.fineName = fineName;
        }


    }


    public enum YamlEnum {

        /**
         * server.yml
         */
        SERVER_YAML("server.yml"),

        /**
         * application.yml
         */
        APPLICATION_YAML("application.yml"),

        /**
         * bootstrap.yml
         */
        BOOTSTRAP_YAML("bootstrap.yml"),

        /**
         * bootstrap-dev.yml
         */
        BOOTSTRAP_DEV_YAML("bootstrap-dev.yml"),

        /**
         * bootstrap-test.yml
         */
        BOOTSTRAP_TEST_YAML("bootstrap-test.yml"),

        /**
         * bootstrap-prod.yml
         */
        BOOTSTRAP_PROD_YAML("bootstrap-prod.yml"),

        ;

        public final String fineName;

        YamlEnum(String fineName) {
            this.fineName = fineName;
        }

        public static YamlEnum getYaml(String fileName) {
            for (YamlEnum value : YamlEnum.values()) {
                if (fileName.equalsIgnoreCase(value.fineName)) {
                    return value;
                }
            }
            throw new IllegalStateException("@@@ 没有找到对应的Yaml文件枚举. 文件名:" + fileName);
        }

    }


}
