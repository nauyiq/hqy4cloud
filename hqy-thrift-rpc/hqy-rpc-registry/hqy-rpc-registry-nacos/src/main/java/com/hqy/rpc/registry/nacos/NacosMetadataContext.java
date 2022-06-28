package com.hqy.rpc.registry.nacos;

import cn.hutool.core.map.MapUtil;
import com.hqy.rpc.registry.node.Node;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

import static com.hqy.rpc.registry.nacos.NacosMetadataContext.MetadataKey.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/28 16:44
 */
public class NacosMetadataContext {

    static class MetadataKey {
        public final static String NAME = "name";
        public final static String HASH_FACTOR = "hashFactor";
        public final static String ACTUATOR_NODE = "actuatorNode";
        public final static String UIP = "uip";
        public final static String PUB_MODE = "pubMode";
    }

    public static Map<String, String> buildMetadata(Node node) {
        AssertUtil.notNull(node, "build metadata failed, node is null.");
        return MapUtil.builder(new HashMap<String, String>(6))
                .put(NAME, node.getName())
                .put(HASH_FACTOR, node.getHashFactor())
                .put(ACTUATOR_NODE, node.getActuatorNode().name())
                .put(UIP, JsonUtil.toJson(node.getUip()))
                .put(PUB_MODE, String.valueOf(node.getPubMode())).build();
    }



}
