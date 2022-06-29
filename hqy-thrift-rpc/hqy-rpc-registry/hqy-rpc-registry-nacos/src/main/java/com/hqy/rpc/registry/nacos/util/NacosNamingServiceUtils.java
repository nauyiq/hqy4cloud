package com.hqy.rpc.registry.nacos.util;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.hqy.foundation.common.StringConstantFieldValuePredicate;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.nacos.naming.NamingServiceWrapper;
import com.hqy.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

import static com.alibaba.nacos.api.PropertyKeyConst.*;
import static com.alibaba.nacos.client.naming.utils.UtilAndComs.NACOS_NAMING_LOG_NAME;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 15:48
 */
public class NacosNamingServiceUtils {

    private static final Logger log = LoggerFactory.getLogger(NacosNamingServiceUtils.class);

    public static NamingServiceWrapper createNamingService(Metadata connectionMetadata) {
        Properties nacosProperties = buildNacosProperties(connectionMetadata);
        NamingService namingService;
        try {
            namingService = NacosFactory.createNamingService(nacosProperties);
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        return new NamingServiceWrapper(namingService);
    }

    private static Properties buildNacosProperties(Metadata connectionMetadata) {
        AssertUtil.notNull(connectionMetadata, "Fail build nacos properties, connection metadata is null.");
        Properties properties = new Properties();
        setServerAddress(connectionMetadata, properties);
        setProperties(connectionMetadata, properties);
        return properties;
    }

    private static void setServerAddress(Metadata connectionMetadata, Properties properties) {
        String serverAddressBuilder = connectionMetadata.getHost() + ':' + connectionMetadata.getPort();
        properties.put(SERVER_ADDR, serverAddressBuilder);
    }

    private static void setProperties(Metadata connectionMetadata, Properties properties) {
        putPropertyIfAbsent(connectionMetadata, properties, NACOS_NAMING_LOG_NAME, null);
        Map<String, String> parameters = connectionMetadata.getParameters(StringConstantFieldValuePredicate.of(PropertyKeyConst.class));
        // Put all parameters
        properties.putAll(parameters);
        if (StringUtils.isNotEmpty(connectionMetadata.getUsername())){
            properties.put(USERNAME, connectionMetadata.getUsername());
        }
        if (StringUtils.isNotEmpty(connectionMetadata.getPassword())){
            properties.put(PASSWORD, connectionMetadata.getPassword());
        }
        putPropertyIfAbsent(connectionMetadata, properties, NAMING_LOAD_CACHE_AT_START, "true");
    }

    private static void putPropertyIfAbsent(Metadata connectionMetadata, Properties properties, String propertyName, String defaultValue) {
        String propertyValue = connectionMetadata.getParameter(propertyName);
        if (StringUtils.isNotBlank(propertyValue)) {
            properties.put(propertyName, propertyValue);
        } else {
            // when defaultValue is empty, we should not set empty value
            if (StringUtils.isNotEmpty(defaultValue)) {
                properties.setProperty(propertyName, defaultValue);
            }
        }
    }


}
