package com.hqy.cloud.rpc.nacos.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.hqy.cloud.util.config.ConfigurationContext;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7 15:34
 */
public class NacosConfigurationUtils {

    private static final Logger log = LoggerFactory.getLogger(NacosConfigurationUtils.class);

    public static final String NACOS_ADDRESS_KEY = "spring.cloud.nacos.discovery.server-addr";
    public static final String NACOS_GROUP_KEY = "spring.cloud.nacos.discovery.group";

    public static String getServerAddress() {
        return getServerAddress(ConfigurationContext.YamlEnum.APPLICATION_YAML, NACOS_ADDRESS_KEY);
    }

    @SneakyThrows
    public static String getServerAddress(ConfigurationContext.YamlEnum yaml, String serverAddressKey) {
        String serviceAddress = null;
        try {
            serviceAddress = ConfigurationContext.getString(yaml, serverAddressKey);
            if (StringUtils.isNotBlank(serviceAddress)) {
                return serviceAddress;
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        if (StringUtils.isBlank(serviceAddress)) {
            //尝试从System properties中获取
            serviceAddress = System.getProperty(NACOS_ADDRESS_KEY);
        }

        if (serviceAddress == null) {
            throw new NacosException();
        }

        return serviceAddress;
    }

    public static String getNacosGroup() {
        String group = null;
        try {
            group = ConfigurationContext.getString(ConfigurationContext.YamlEnum.APPLICATION_YAML, NACOS_GROUP_KEY);
            if (StringUtils.isNotBlank(group)) {
                return group;
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        if (StringUtils.isBlank(group)) {
            //尝试从System properties中获取
            group = System.getProperty(NACOS_GROUP_KEY);
            if (StringUtils.isBlank(group)) {
                group = "DEV_GROUP";
            }
        }

        return group;
    }
}
