package com.hqy.rpc.registry.nacos.util;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.util.config.ConfigurationContext;
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

    private static final String NACOS_ADDRESS_KEY = "spring.cloud.nacos.config.server-addr";
    private static final String ACTIVE = "spring.profiles.active";


    public static String getServerAddress() {
        return getServerAddress(ConfigurationContext.YamlEnum.BOOTSTRAP_YAML, NACOS_ADDRESS_KEY);
    }

    public static String getServerAddress(ConfigurationContext.YamlEnum yaml, String serverAddressKey) {
        try {
            String serviceAddress;
            String active = ConfigurationContext.getString(yaml, ACTIVE);
            if (StringUtils.isNotBlank(active)) {
                serviceAddress = ConfigurationContext.
                        getString(ConfigurationContext.YamlEnum.getYaml(StringConstants.BOOTSTRAP + StringConstants.Symbol.RAIL + active + StringConstants.File.YML), serverAddressKey);
            } else {
                serviceAddress = ConfigurationContext.getString(yaml, serverAddressKey);
            }
            return serviceAddress;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw t;
        }
    }

}
