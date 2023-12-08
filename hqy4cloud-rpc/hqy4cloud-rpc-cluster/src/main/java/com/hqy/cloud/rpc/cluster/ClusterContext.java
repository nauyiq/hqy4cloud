package com.hqy.cloud.rpc.cluster;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.rpc.cluster.support.FailBackCluster;
import com.hqy.cloud.rpc.cluster.support.FailFastCluster;
import com.hqy.cloud.rpc.cluster.support.FailSafeCluster;
import com.hqy.cloud.rpc.cluster.support.FailoverCluster;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 14:35
 */
public class ClusterContext {

    private static final Logger log = LoggerFactory.getLogger(ClusterContext.class);

    private static final Map<String, ClusterMode> CLUSTER_MAP = MapUtil.newConcurrentHashMap();

    private static Cluster defaultCluster;

    static  {
        // Collector server use failSafe strategy.
        CLUSTER_MAP.put(MicroServiceConstants.COMMON_COLLECTOR, ClusterMode.FAILSAFE);
        // Account auth server use failover strategy.
        CLUSTER_MAP.put(MicroServiceConstants.ACCOUNT_SERVICE, ClusterMode.FAILOVER);
    }

    public static ClusterMode getClusterMode(String serviceName) {
      return getClusterMode(serviceName, Cluster.DEFAULT);
    }

    public static ClusterMode getClusterMode(String serviceName, ClusterMode defaultClusterMode) {
        if (defaultClusterMode == null) {
            defaultClusterMode = Cluster.DEFAULT;
        }
        return CLUSTER_MAP.getOrDefault(serviceName, defaultClusterMode);
    }

    public static void setCluster(String serviceName, ClusterMode clusterMode) {
        AssertUtil.notEmpty(serviceName, "Service name should not be empty.");
        CLUSTER_MAP.put(serviceName, clusterMode == null ? Cluster.DEFAULT : clusterMode);
    }


    public static Cluster getCluster(String serverName) {
        return getCluster(serverName, Cluster.DEFAULT);

    }

    public static Cluster getCluster(String serverName, ClusterMode defaultClusterMode) {
        ClusterMode mode = getClusterMode(serverName, defaultClusterMode);
        try {
            Cluster cluster;
            try {
                cluster = switch (mode) {
                    case FAILBACK -> SpringContextHolder.getBean(FailBackCluster.class);
                    case FAILFAST -> SpringContextHolder.getBean(FailFastCluster.class);
                    case FAILSAFE -> SpringContextHolder.getBean(FailSafeCluster.class);
                    default -> SpringContextHolder.getBean(FailoverCluster.class);
                };
            } catch (Exception e) {
                log.warn(e.getMessage());
                cluster = new FailoverCluster();
            }

            AssertUtil.notNull(cluster, "From the spring container cluster bean error, cluster mode " + mode);
            return cluster;
        } catch (Throwable t) {
            log.warn("Failed execute to get cluster, cluster mode: {}, return default cluster.", mode, t);
            if (defaultCluster == null) {
                synchronized (ClusterContext.class) {
                    try {
                        setDefault(SpringContextHolder.getBean(FailoverCluster.class));
                    } catch (Throwable throwable) {
                        log.warn("Spring container not start or not found failover cluster from container.");
                        setDefault(new FailoverCluster());
                    }
                }
            }
            return defaultCluster;
        }

    }



    public static void setDefault(Cluster defaultCluster) {
        ClusterContext.defaultCluster = defaultCluster;
    }








}
