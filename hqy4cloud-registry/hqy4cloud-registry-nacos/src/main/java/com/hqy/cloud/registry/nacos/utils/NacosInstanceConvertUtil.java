package com.hqy.cloud.registry.nacos.utils;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.MetadataInfo;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import com.hqy.cloud.registry.nacos.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * NacosInstanceConvertUtil.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class NacosInstanceConvertUtil {

    public static Instance convert(ApplicationModel model, Map<String, String> metadataMap) {
        Instance instance = new Instance();
        instance.setServiceName(model.getApplicationName());
        instance.setIp(model.getIp());
        instance.setPort(model.getPort());
        instance.setMetadata(metadataMap);
        // setting params
        String instanceId = model.getParameter(Constants.INSTANCE_ID);
        if (StringUtils.isNotBlank(instanceId)) {
            instance.setInstanceId(instanceId);
        }
        double weight = model.getParameter(Constants.WEIGHT, Constants.DEFAULT_WEIGHT);
        instance.setWeight(weight);
        boolean ephemeral = model.getParameter(Constants.EPHEMERAL, Constants.DEFAULT_EPHEMERAL);
        instance.setEphemeral(ephemeral);
        return instance;
    }

    public static ApplicationModel convert(Instance instance, String group, RegistryInfo registryInfo, MetadataInfo metadataInfo) {
        ApplicationModel model = new ApplicationModel();
        model.setApplicationName(instance.getServiceName());
        model.setGroup(group);
        model.setHealthy(instance.isHealthy());
        model.setPort(instance.getPort());
        model.setIp(instance.getIp());
        model.setRegistryInfo(registryInfo);
        model.setMetadataInfo(metadataInfo);
        return model;
    }


    public static void updateInstance(Instance instance, ApplicationModel model, Map<String, String> metadataMap) {
        boolean ephemeral = model.getParameter(Constants.EPHEMERAL, Constants.DEFAULT_EPHEMERAL);
        instance.setEphemeral(ephemeral);
        double weight = model.getParameter(Constants.WEIGHT, Constants.DEFAULT_WEIGHT);
        instance.setWeight(weight);
        instance.setMetadata(metadataMap);
    }
}
