package com.hqy.rpc.registry.nacos.util;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.common.support.RegistryInfo;
import com.hqy.rpc.registry.nacos.node.Metadata;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
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
        Metadata metadata = new Metadata(rpcModel);
        return metadata.toMetadataMap();
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
        Metadata metadata = toMetadataFromMap(instanceMetadata);
        if (metadata == null) {
            throw new RpcException("map convert metadata error, metadata map " + JsonUtil.toJson(instanceMetadata));
        }
        return new RPCModel(instance.getServiceName(), instance.getPort(), group, registryInfo, metadata.getRpcServerAddress(), instanceMetadata);
    }

    public static Metadata toMetadataFromMap(Map<String, String> metadataMap) {
        try {
            return new Metadata(metadataMap);
        } catch (Throwable t) {
            log.warn("Failed execute to map convert to metadata, cause {}", t.getMessage(), t);
            return null;
        }
    }

}
