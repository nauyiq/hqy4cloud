package com.hqy.rpc.nacos;

import com.hqy.base.common.base.project.MicroServiceManager;
import lombok.extern.slf4j.Slf4j;

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

    /**
     * 注册自定义的nacos服务客户端 封装了对节点数据的操作
     * @param serviceName 服务名
     * @return boolean
     */
    public static boolean registryNacosClient(String serviceName) {
        if (!MicroServiceManager.checkClusterExist(serviceName)) {
            log.warn("@@@ Registry Nacos client error, invalid serviceName: {}", serviceName);
            return false;
        }
        if (!REGISTRY_MAP.containsKey(serviceName)) {
            RegistryClient client = new AbstractNacosClient() {
                @Override
                public String getServiceNameEn() {
                    return serviceName;
                }
            };
            log.info("@@@ create new nacosRegistryClient for [{}].", serviceName);
            REGISTRY_MAP.put(serviceName, client);
        }
        return true;
    }

    public static RegistryClient getNacosClient(String serviceName) {
        if (!MicroServiceManager.checkClusterExist(serviceName)) {
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
