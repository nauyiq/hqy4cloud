/*
package com.hqy.rpc.registry.discovery;

import com.hqy.rpc.common.Metadata;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

*/
/**
 * Abstract {@link ServerDiscoveryFactory}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 15:39
 *//*

public abstract class AbstractServerDiscoveryFactory implements ServerDiscoveryFactory {

    private final Map<String, ServerDiscovery> discoveries = new ConcurrentHashMap<>();

    public List<ServerDiscovery> getAllServiceDiscoveries() {
        return Collections.unmodifiableList(new LinkedList<>(discoveries.values()));
    }

    @Override
    public ServerDiscovery gerServerDiscovery(Metadata registryMetadata) {
        String key = registryMetadata.buildString(true);
        return discoveries.computeIfAbsent(key, k -> createDiscovery(registryMetadata));
    }


    */
/**
     * 交给子类工厂去创建客户端
     * @param registryMetadata 注册信息
     * @return            客户端调用视角
     *//*

    protected abstract ServerDiscovery createDiscovery(Metadata registryMetadata);
}
*/
