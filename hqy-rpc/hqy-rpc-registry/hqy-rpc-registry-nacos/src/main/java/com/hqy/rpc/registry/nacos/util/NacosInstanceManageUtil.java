package com.hqy.rpc.registry.nacos.util;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/5 16:37
 */
public class NacosInstanceManageUtil {

    /**
     * serviceName -> refreshed instance list
     */
    private static final Map<String, List<Instance>> SERVICE_INSTANCE_LIST_MAP = Maps.newConcurrentMap();


    public static void initOrRefreshServiceInstanceList(String serviceName, List<Instance> instanceList) {
        SERVICE_INSTANCE_LIST_MAP.put(serviceName, instanceList);
    }


    public static List<Instance> getAllServiceInstanceList(String serviceName) {
        return SERVICE_INSTANCE_LIST_MAP.computeIfAbsent(serviceName, s -> Collections.emptyList());
    }


}
