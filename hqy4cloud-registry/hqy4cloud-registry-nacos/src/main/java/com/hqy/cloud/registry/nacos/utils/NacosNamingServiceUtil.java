package com.hqy.cloud.registry.nacos.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.common.exeception.RegisterDiscoverException;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.registry.nacos.naming.NamingServiceWrapper;
import com.hqy.cloud.util.StringConstantFieldValuePredicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

import static com.alibaba.nacos.api.PropertyKeyConst.NAMING_LOAD_CACHE_AT_START;
import static com.alibaba.nacos.api.PropertyKeyConst.SERVER_ADDR;
import static com.alibaba.nacos.client.naming.utils.UtilAndComs.NACOS_NAMING_LOG_NAME;

/**
 * NacosNamingServiceUtil.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/3
 */
public class NacosNamingServiceUtil {
    private static final Logger log = LoggerFactory.getLogger(NacosNamingServiceUtil.class);

    public static NamingServiceWrapper getNamingService(ProjectInfoModel model) {
        NamingService namingService = BeanRepository.getInstance().getBean(NamingService.class);
        NamingMaintainService namingMaintainService = BeanRepository.getInstance().getBean(NamingMaintainService.class);
        if (namingService == null) {
            try {
                namingService = SpringUtil.getBean(NamingService.class);
            } catch (Throwable cause) {
                log.warn("Get namingService failed from spring context.");
            }
        }
        if (namingMaintainService == null) {
            try {
                namingMaintainService = SpringUtil.getBean(NamingMaintainService.class);
            } catch (Throwable cause) {
                log.warn("Get namingMaintainService failed from spring context.");
            }
        }
        if (namingService == null || namingMaintainService == null) {
            Properties properties = buildNacosProperties(model);
            try {
                namingService = namingService == null ? NacosFactory.createNamingService(properties) : namingService;
                namingMaintainService = namingMaintainService == null ? NacosFactory.createMaintainService(properties) : namingMaintainService;
                BeanRepository.getInstance().register(NamingService.class, namingService);
                BeanRepository.getInstance().register(NamingMaintainService.class, namingMaintainService);
            } catch (Throwable cause) {
                log.error(cause.getMessage());
                throw new RegisterDiscoverException(cause.getMessage());
            }

        }
        return new NamingServiceWrapper(namingService, namingMaintainService, model.getGroup());
    }

    private static Properties buildNacosProperties(ProjectInfoModel model) {
        Properties properties = new Properties();
        setServerAddress(model, properties);
        setProperties(model, properties);
        return properties;
    }

    private static void setServerAddress(ProjectInfoModel model, Properties properties) {
        properties.put(SERVER_ADDR, model.getRegistryInfo().getAddress());
    }

    private static void setProperties(ProjectInfoModel model, Properties properties) {
        putPropertyIfAbsent(model, properties, NACOS_NAMING_LOG_NAME, null);
        Map<String, String> parameters = model.getParameters(StringConstantFieldValuePredicate.of(PropertyKeyConst.class));
        properties.putAll(parameters);
        putPropertyIfAbsent(model, properties, NAMING_LOAD_CACHE_AT_START, "true");
    }

    private static void putPropertyIfAbsent(ProjectInfoModel model, Properties properties, String propertyName, String defaultValue) {
        String propertyValue = model.getParameter(propertyName);
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
