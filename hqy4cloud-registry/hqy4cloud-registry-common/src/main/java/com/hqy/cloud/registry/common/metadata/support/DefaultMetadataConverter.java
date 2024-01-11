package com.hqy.cloud.registry.common.metadata.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.registry.common.metadata.MetadataConverter;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.metadata.MetadataPropertyKeyConstants;
import com.hqy.cloud.registry.common.model.PubMode;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.foundation.common.StringConstantFieldValuePredicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * DefaultMetadataConverter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/4
 */
public class DefaultMetadataConverter implements MetadataConverter {
    private static final Logger log = LoggerFactory.getLogger(DefaultMetadataConverter.class);

    @Override
    public MetadataInfo convertMetadataInfo(String application, Map<String, String> metadataMap) {
        AssertUtil.notEmpty(application, "Application to metadata should not be empty.");
        MetadataInfo metadataInfo = new MetadataInfo(application);
        if (MapUtil.isEmpty(metadataMap)) {
            return metadataInfo;
        }
        metadataInfo.setParameters(metadataMap);
        try {
            metadataInfo.setActuatorNode(ActuatorNode.valueOf(metadataMap.get(MetadataPropertyKeyConstants.APPLICATION_ACTUATOR_TYPE)));
            metadataInfo.setMaster(Boolean.parseBoolean(metadataMap.getOrDefault(MetadataPropertyKeyConstants.APPLICATION_MASTER_NODE, Boolean.FALSE.toString())));
            metadataInfo.setEnv(metadataMap.get(MetadataPropertyKeyConstants.APPLICATION_ENV));
            metadataInfo.setRevision(metadataMap.get(MetadataPropertyKeyConstants.APPLICATION_REVISION));
            metadataInfo.setPubMode(PubMode.of(Integer.parseInt(metadataMap.get(MetadataPropertyKeyConstants.APPLICATION_PUB_MODE))));
            metadataInfo.setWeight(Integer.parseInt(metadataMap.get(MetadataPropertyKeyConstants.APPLICATION_WEIGHT)));
        } catch (Throwable cause) {
            log.error("Failed execute to convert metadataMap to metadataInfo, cause: {}.", cause.getMessage());
        }
        return metadataInfo;
    }
}
