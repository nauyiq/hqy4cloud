package com.hqy.cloud.rpc.nacos.utils;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.model.RegistryInfo;
import com.hqy.cloud.rpc.registry.discovery.RPCMetadata;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/28 16:44
 */
public class NacosInstanceUtils {

    private static final Logger log = LoggerFactory.getLogger(NacosInstanceUtils.class);

    public static Map<String, String> buildMetadata(RPCModel rpcModel) throws Exception {
        AssertUtil.notNull(rpcModel, "build metadata failed, rpcContext is null.");
        RPCMetadata RPCMetadata = new RPCMetadata(rpcModel);
        return RPCMetadata.toMetadataMap();
    }

    public static List<RPCModel> instancesConvert(RegistryInfo registryInfo, String group, List<Instance> instances) {
        if (CollectionUtils.isEmpty(instances)) {
            return Collections.emptyList();
        }
        return instances.stream().map(instance -> instanceConvert(group, registryInfo, instance)).collect(Collectors.toList());
    }

    public static RPCModel instanceConvert(String group, RegistryInfo registryInfo, Instance instance) {
        AssertUtil.notNull(instance, "Failed execute to instance convert to rpcContext, instance is null.");
        AssertUtil.notNull(registryInfo, "Failed execute to instance convert to rpcContext, registryInfo is null.");

        Map<String, String> instanceMetadata = instance.getMetadata();
        RPCMetadata RPCMetadata = toMetadataFromMap(instanceMetadata);
        if (RPCMetadata == null) {
            throw new RpcException("map convert metadata error, metadata map " + JsonUtil.toJson(instanceMetadata));
        }
        return new RPCModel(instance.getServiceName(), instance.getPort(), group, registryInfo, RPCMetadata.getRpcServerAddress(), instanceMetadata);
    }

    public static RPCMetadata toMetadataFromMap(Map<String, String> metadataMap) {
        try {
            return new RPCMetadata(metadataMap);
        } catch (Throwable t) {
            log.warn("Failed execute to map convert to metadata, cause {}", t.getMessage(), t);
            return null;
        }
    }

}
