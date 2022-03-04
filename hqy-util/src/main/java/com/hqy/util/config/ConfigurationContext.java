package com.hqy.util.config;

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

    /**
     * .properties 文件map
     */
    public static Map<PropertiesEnum, PropertyStrategy> propertiesMap = new ConcurrentHashMap<>();

    /**
     * .yal 文件map
     */
    public static Map<YamlEnum, YamlStrategy> yamlMap = new ConcurrentHashMap<>();


    static {
        propertiesMap.put(PropertiesEnum.SERVER_PROPERTIES, new PropertyStrategy(PropertiesEnum.SERVER_PROPERTIES.fineName));
        propertiesMap.put(PropertiesEnum.APPLICATION_PROPERTIES, new PropertyStrategy(PropertiesEnum.APPLICATION_PROPERTIES.fineName));
        propertiesMap.put(PropertiesEnum.BOOTSTRAP_PROPERTIES, new PropertyStrategy(PropertiesEnum.BOOTSTRAP_PROPERTIES.fineName));

        yamlMap.put(YamlEnum.SERVER_YAML, new YamlStrategy(YamlEnum.SERVER_YAML.fineName));
        yamlMap.put(YamlEnum.APPLICATION_YAML, new YamlStrategy(YamlEnum.APPLICATION_YAML.fineName));
        yamlMap.put(YamlEnum.BOOTSTRAP_YAML, new YamlStrategy(YamlEnum.BOOTSTRAP_YAML.fineName));

        System.out.println("\r\n############ propertiesMap And yamlMap initialize. ###############\r\n");
    }

    public static Properties getProperties(PropertiesEnum propertiesEnum) {
        PropertyStrategy propertyStrategy = propertiesMap.get(propertiesEnum);
        if (Objects.isNull(propertyStrategy)) {
            throw new IllegalStateException("@@@ Initialize propertiesMap missing " + propertiesEnum.fineName);
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
            throw new IllegalStateException("@@@ Initialize yamlMap missing " + yamlEnum.fineName);
        }
        return strategy.getData();
    }


    public static String getString(YamlEnum yamlEnum, String key) {
        Map<String, String> yamlValues = getYaml(yamlEnum);
        return yamlValues.get(key);
    }


    public static String getString(YamlEnum yamlEnum, String key, String defaultValue) {
        String string = getString(yamlEnum, key);
        return StringUtils.isBlank(string) ? defaultValue : string;
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

        ;

        public final String fineName;

        YamlEnum(String fineName) {
            this.fineName = fineName;
        }
    }


}
