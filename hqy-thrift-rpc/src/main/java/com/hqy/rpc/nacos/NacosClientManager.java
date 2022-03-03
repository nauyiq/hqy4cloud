package com.hqy.rpc.nacos;

import com.hqy.fundation.common.base.project.MicroServiceHelper;
import com.hqy.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 静态的获取各个节点的RegistryClient
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/24 15:58
 */
@Slf4j
public class NacosClientManager {

    /**
     * key: 服务的注册名 即注册进注册中心的名字
     * value: 对应的注册服务的客户端
     */
    private static final Map<String, RegistryClient> REGISTRY_MAP = new HashMap<>();


    public static Map<String, RegistryClient> getRegistryMap() {
        return REGISTRY_MAP;
    }


    public static RegistryClient getNacosClient(String serviceName) {
        if (!MicroServiceHelper.checkClusterExist(serviceName)) {
            log.warn("@@@ Registry Nacos client error, invalid serviceName: {}", serviceName);
            return null;
        }
        if (REGISTRY_MAP.containsKey(serviceName)) {
            return REGISTRY_MAP.get(serviceName);
        } else {
            RegistryClient client = new AbstractNacosClient() {
                @Override
                public String getServiceNameEn() {
                    return serviceName;
                }
            };
            log.info("@@@ create new nacosRegistryClient for [{}].", serviceName);
            REGISTRY_MAP.put(serviceName, client);
            return client;
        }
    }




}
