package com.hqy.cloud.foundation.common.route.support;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.redis.key.RedisKey;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import com.hqy.cloud.foundation.cache.redis.support.SmartRedisManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 长连接端口路由管理器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/2 15:53
 */
@Slf4j
public class SocketPortRouterManager {
    private static final Cache<String, Integer> LOCAL_CACHE = CacheBuilder.newBuilder().initialCapacity(1024).expireAfterAccess(1L, TimeUnit.HOURS).build();
    private static final RedisKey KEY = new RedisNamedKey(MicroServiceConstants.FOUNDATION_SERVICE, SocketPortRouterManager.class.getSimpleName());


    private static String genKey(String serviceName, String ipAddr) {
        return KEY.getKey(serviceName.concat(StringConstants.Symbol.UNION).concat(ipAddr));
    }

    public static Integer getPort(String serviceName, String ipAddr) {
        String key = genKey(serviceName, ipAddr);
        Integer port = LOCAL_CACHE.getIfPresent(key);
        if (port == null) {
            String portStr = SmartRedisManager.getInstance().get(key);
            if (StringUtils.isBlank(portStr)) {
                throw new IllegalArgumentException("Not found socket port by redis, service name = " + serviceName);
            }
            port = Integer.valueOf(portStr);
            LOCAL_CACHE.put(key, port);
        }
        return port;
    }


    public static void registryPort(String serviceName, String ipAddr, int port) {
        String key = genKey(serviceName, ipAddr);
        SmartRedisManager.getInstance().set(key, port);
    }




}
