package com.hqy.fundation.common.route;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.fundation.cache.redis.RedisUtil;
import com.hqy.base.common.base.lang.BaseStringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 借助guava和redis来标记某个socket项目是否启用集群模式
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 11:44
 */
public class SocketClusterStatusManager {

    private static final Logger log = LoggerFactory.getLogger(SocketClusterStatusManager.class);

    private SocketClusterStatusManager() {}

    private static final int DB = 1;

    private static final Cache<String, SocketClusterStatus> CLUSTER_STATUS_CACHE =
            CacheBuilder.newBuilder().initialCapacity(1024).expireAfterAccess(10L, TimeUnit.MINUTES).build();

    private static String genKey(String env, String module) {
        return SocketClusterStatus.class.getSimpleName().concat(BaseStringConstants.Symbol.COLON)
                .concat(env).concat(BaseStringConstants.Symbol.COLON).concat(module);
    }

    /**
     * 根据项目名 获取socket.io hash配置
     * @param env 环境
     * @param serviceName 项目名
     * @return SocketHashContext
     */
    public static SocketClusterStatus query(String env, String serviceName) {
        String key = genKey(env, serviceName);
        SocketClusterStatus hashContext = CLUSTER_STATUS_CACHE.getIfPresent(key);
        if (Objects.isNull(hashContext)) {
            //内存中没有 则去redis中获取
            hashContext = RedisUtil.instance().selectDb(DB, false).get(key);
            if (Objects.isNull(hashContext)) {
                log.info("@@@ 获取不到当前服务：{} 的socketHashContext.", serviceName);
                hashContext = new SocketClusterStatus(env, serviceName);

            }
            CLUSTER_STATUS_CACHE.put(serviceName, hashContext);
        }
        return hashContext;
    }

    public static void registry(SocketClusterStatus socketClusterStatus) {
        String key = genKey(socketClusterStatus.getEnv(), socketClusterStatus.getModule());
        CLUSTER_STATUS_CACHE.put(key, socketClusterStatus);
        RedisUtil.instance().selectDb(DB, false).set(key, socketClusterStatus, 0L);
    }


}
