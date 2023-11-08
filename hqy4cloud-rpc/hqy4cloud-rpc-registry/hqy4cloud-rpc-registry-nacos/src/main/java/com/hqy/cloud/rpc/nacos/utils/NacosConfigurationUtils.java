package com.hqy.cloud.rpc.nacos.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.hqy.cloud.util.config.ConfigurationContext;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hqy.cloud.util.config.ConfigurationContext.YamlEnum.APPLICATION_YAML;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7 15:34
 */
public class NacosConfigurationUtils {
    private static final Logger log = LoggerFactory.getLogger(NacosConfigurationUtils.class);

    public static final String NACOS_ADDRESS_KEY = "spring.cloud.nacos.discovery.server-addr";
    public static final String NACOS_GROUP_KEY = "spring.cloud.nacos.discovery.group";
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    public static String getServerAddress() {
        return getServerAddress(APPLICATION_YAML, NACOS_ADDRESS_KEY);
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

        //尝试从System properties中获取
        if (StringUtils.isBlank(serviceAddress)) {
            serviceAddress = System.getProperty(NACOS_ADDRESS_KEY);
        }

        if (serviceAddress == null) {
            throw new NacosException(-1, "Failed execute to obtain nacos service address configuration.");
        }

        return serviceAddress;
    }

    public static String getNacosGroup() {
        String group = null;
        try {
            group = ConfigurationContext.getString(APPLICATION_YAML, NACOS_GROUP_KEY);
            if (StringUtils.isNotBlank(group)) {
                return group;
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        //尝试从System properties中获取
        if (StringUtils.isBlank(group)) {
            group = System.getProperty(NACOS_GROUP_KEY, DEFAULT_GROUP);
        }

        return group;
    }
}
