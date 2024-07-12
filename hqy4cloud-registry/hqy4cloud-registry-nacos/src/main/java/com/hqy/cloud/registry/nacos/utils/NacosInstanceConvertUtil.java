package com.hqy.cloud.registry.nacos.utils;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import com.hqy.cloud.registry.nacos.Constants;
import com.hqy.cloud.util.AssertUtil;

import java.util.Map;

/**
 * NacosInstanceConvertUtil.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class NacosInstanceConvertUtil {

    public static Instance convert(ProjectInfoModel model, Map<String, String> metadataMap) {
        Instance instance = new Instance();
        instance.setInstanceId(model.getId());
        instance.setServiceName(model.getApplicationName());
        instance.setIp(model.getIp());
        instance.setPort(model.getPort());
        instance.setMetadata(metadataMap);
        double weight = model.getParameter(Constants.WEIGHT, Constants.DEFAULT_WEIGHT);
        instance.setWeight(weight);
        boolean ephemeral = model.getParameter(Constants.EPHEMERAL, Constants.DEFAULT_EPHEMERAL);
        instance.setEphemeral(ephemeral);
        return instance;
    }

    public static ProjectInfoModel convert(Instance instance, String group, RegistryInfo registryInfo, MetadataInfo metadataInfo) {
        ProjectInfoModel model = ProjectInfoModel.of(getInstanceServiceName(instance), metadataInfo.getEnv(), group);
        model.setId(instance.getInstanceId());
        model.setHealthy(instance.isHealthy());
        model.setPort(instance.getPort());
        model.setIp(instance.getIp());
        model.setRegistryInfo(registryInfo);
        model.setMetadataInfo(metadataInfo);
        return model;
    }


    public static void updateInstance(Instance instance, ProjectInfoModel model, Map<String, String> metadataMap) {
        boolean ephemeral = model.getParameter(Constants.EPHEMERAL, Constants.DEFAULT_EPHEMERAL);
        instance.setEphemeral(ephemeral);
        double weight = model.getParameter(Constants.WEIGHT, Constants.DEFAULT_WEIGHT);
        instance.setWeight(weight);
        instance.setMetadata(metadataMap);
    }


    public static String getInstanceServiceName(Instance instance) {
        AssertUtil.notNull(instance, "Instance should not be null.");
        String serviceName = instance.getServiceName();
        String[] split = serviceName.split(StringConstants.Symbol.AT_AT);
        if (split.length != 1) {
            return split[1];
        }
        return serviceName;
    }
}
