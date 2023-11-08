package com.hqy.cloud.rpc.nacos.utils;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.foundation.common.StringConstantFieldValuePredicate;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.nacos.naming.NamingServiceWrapper;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
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

    public static NamingServiceWrapper createNamingService(RPCModel rpcModel) {
        NamingService namingService = null;
        try {
            NacosServiceManager nacosServiceManager = SpringContextHolder.getBean(NacosServiceManager.class);
            namingService = nacosServiceManager.getNamingService();
        } catch (Throwable t) {
            log.warn("Get namingService failed from spring context.");
        }

        if (namingService != null) {
            return new NamingServiceWrapper(namingService, rpcModel.getGroup());
        }
        Properties nacosProperties = buildNacosProperties(rpcModel);
        try {
            namingService = NacosFactory.createNamingService(nacosProperties);
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        return new NamingServiceWrapper(namingService, rpcModel.getGroup());
    }

    private static Properties buildNacosProperties(RPCModel rpcModel) {
        AssertUtil.notNull(rpcModel, "Fail build nacos properties, connection rpcContext is null.");
        Properties properties = new Properties();
        setServerAddress(rpcModel, properties);
        setProperties(rpcModel, properties);
        return properties;
    }

    private static void setServerAddress(RPCModel rpcModel, Properties properties) {
        properties.put(SERVER_ADDR, rpcModel.getRegistryAddress());
    }

    private static void setProperties(RPCModel rpcModel, Properties properties) {
        putPropertyIfAbsent(rpcModel, properties, NACOS_NAMING_LOG_NAME, null);
        Map<String, String> parameters = rpcModel.getParameters(StringConstantFieldValuePredicate.of(PropertyKeyConst.class));
        // Put all parameters
        properties.putAll(parameters);
        if (StringUtils.isNotEmpty(rpcModel.getUsername())){
            properties.put(USERNAME, rpcModel.getUsername());
        }
        if (StringUtils.isNotEmpty(rpcModel.getPassword())){
            properties.put(PASSWORD, rpcModel.getPassword());
        }
        //获取名称空间
        properties.put(NAMESPACE, rpcModel.getParameter(NAMESPACE, Environment.ENV_DEV));
        putPropertyIfAbsent(rpcModel, properties, NAMING_LOAD_CACHE_AT_START, "true");
    }

    private static void putPropertyIfAbsent(RPCModel rpcModel, Properties properties, String propertyName, String defaultValue) {
        String propertyValue = rpcModel.getParameter(propertyName);
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
