package com.hqy.rpc.registry.nacos;

import cn.hutool.core.map.MapUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.rpc.common.CommonConstants;
import com.hqy.rpc.common.ConnectionInfo;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.common.Node;
import com.hqy.rpc.registry.nacos.node.NacosNode;
import com.hqy.util.AssertUtil;
import com.hqy.util.CommonDateUtil;
import com.hqy.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.rpc.registry.nacos.MetadataContext.MetadataKey.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/28 16:44
 */
public class MetadataContext {

    private static final Logger log = LoggerFactory.getLogger(MetadataContext.class);

    public static class MetadataKey {
        public final static String METADATA_INFO_KEY = "metadataInfo";

        public final static String WEIGHT = CommonConstants.WEIGHT;
        public final static String WARMUP = CommonConstants.WARMUP;

    }

    public static Map<String, String> buildMetadata(Node node) {
        AssertUtil.notNull(node, "build metadata failed, node is null.");
        NacosNode nacosNode = (NacosNode) node;
        return MapUtil.builder(new HashMap<String, String>(1)).put(METADATA_INFO_KEY, JsonUtil.toJson(nacosNode)).build();
    }

    public static List<Metadata> instancesConvert(ConnectionInfo connectionInfo, List<Instance> instances) {
        if (CollectionUtils.isEmpty(instances)) {
            return Collections.emptyList();
        }
        return instances.stream().map(instance -> instanceConvert(connectionInfo, instance)).collect(Collectors.toList());
    }

    public static Metadata instanceConvert(ConnectionInfo connectionInfo, Instance instance) {
        AssertUtil.notNull(instance, "Failed execute to instance convert to metadata, instance is null.");
        Map<String, String> instanceMetadata = instance.getMetadata();
        NacosNode nacosNode = JsonUtil.toBean(instanceMetadata.get(METADATA_INFO_KEY), NacosNode.class);
        return new Metadata(connectionInfo, nacosNode);
    }




}
