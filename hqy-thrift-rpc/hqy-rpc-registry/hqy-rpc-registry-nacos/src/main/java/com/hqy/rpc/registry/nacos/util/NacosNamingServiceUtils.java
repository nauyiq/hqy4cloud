package com.hqy.rpc.registry.nacos.util;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.nacos.naming.NamingServiceWrapper;
import com.hqy.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

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
        StringBuilder serverAddressBuilder =
                new StringBuilder(connectionMetadata.getHost())
                        .append(':')
                        .append(connectionMetadata.getPort());
    }


}
