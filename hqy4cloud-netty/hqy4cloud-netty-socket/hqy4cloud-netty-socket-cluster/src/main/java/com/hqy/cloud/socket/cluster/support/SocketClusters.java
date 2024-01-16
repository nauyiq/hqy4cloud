package com.hqy.cloud.socket.cluster.support;

import com.hqy.cloud.socket.cluster.SocketCluster;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
@Slf4j
public class SocketClusters {
    private static final Map<String, SocketCluster> CLUSTERS = new ConcurrentHashMap<>(2);

    /**
     * 注册某个socket服务使用的socket路由
     * @param clusterName 服务名.
     * @param router      路由.
     */
    public static void registerCluster(String clusterName, SocketCluster router) {
        AssertUtil.notEmpty(clusterName, "Cluster name should not be empty.");
        AssertUtil.notNull(router, "Socket cluster should not be null.");
        log.info("Socket server {} register cluster, cluster name {}.", clusterName, router.getClusterName());
        CLUSTERS.put(clusterName, router);
    }

    /**
     * 获取一个socket服务路由
     * @param clusterType socket服务名
     * @return            socket服务路由
     */
    public static SocketCluster cluster(String clusterType) {
        return CLUSTERS.get(clusterType);
    }











}
