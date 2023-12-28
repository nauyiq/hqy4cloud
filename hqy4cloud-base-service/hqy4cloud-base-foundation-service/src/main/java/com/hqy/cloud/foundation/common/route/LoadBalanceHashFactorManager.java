package com.hqy.cloud.foundation.common.route;

import cn.hutool.core.map.MapUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.foundation.redis.support.SmartRedisManager;
import com.hqy.cloud.rpc.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 借助redis和guava来标记某个socket服务的hash节点
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 15:52
 */
public class LoadBalanceHashFactorManager {

    private static final Logger log = LoggerFactory.getLogger(LoadBalanceHashFactorManager.class);

    private LoadBalanceHashFactorManager() {}


    private static final Cache<String, String> HASH_CACHE =
            CacheBuilder.newBuilder().initialCapacity(1024).expireAfterAccess(10L, TimeUnit.MINUTES).build();


    private static String genKey(String module, int hash) {
        return SocketClusterStatus.class.getSimpleName().concat(StringConstants.Symbol.COLON)
                .concat(module).concat(StringConstants.Symbol.COLON).concat(hash + "");
    }

    /**
     * 根据模块和hash值获取哈希因子
     * @param module 模块名
     * @param hash 哈希值
     * @return 哈希因子
     */
    public static String queryHashFactor(String module, int hash) {
        String key = genKey(module, hash);
        String hashFactor = HASH_CACHE.getIfPresent(key);
        if (StringUtils.isBlank(hashFactor)) {
            hashFactor = SmartRedisManager.getInstance().get(key);
            if (StringUtils.isBlank(hashFactor)) {
                log.warn("@@@ Not found hashFactor, module:{}, hash:{}", module, hash);
                hashFactor = CommonConstants.DEFAULT_HASH_FACTOR;
            } else {
                HASH_CACHE.put(key, hashFactor);
            }
        }
        return hashFactor;
    }

    public static Map<Integer, String> queryHashFactorMap(String serviceName, List<Integer> hashList) {
        Map<Integer, String> resultMap = MapUtil.newHashMap(hashList.size());
        Map<String, Integer> keysMap = hashList.parallelStream().collect(Collectors.toMap(h -> genKey(serviceName, h), h -> h));
        Map<Integer, String> searchMap = MapUtil.newHashMap();
        Set<String> keys = keysMap.keySet();
        for (String key : keys) {
            String hashFactor = HASH_CACHE.getIfPresent(key);
            Integer hash = keysMap.get(key);
            if (StringUtils.isNotBlank(hashFactor)) {
                resultMap.put(hash, hashFactor);
            } else {
                searchMap.put(hash, key);
            }
        }
        if (!searchMap.isEmpty()) {
            // search from redis.
            List<String> values = searchMap.values().stream().toList();
            List<Object> result = SmartRedisManager.getInstance().getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
                StringRedisConnection redisConnection = (StringRedisConnection) connection;
                values.forEach(redisConnection::get);
                return null;
            });
            for (int i = 0; i < result.size(); i++) {
                Object o = result.get(i);
                String key = values.get(i);
                if (o instanceof String resultHashFactor) {
                    HASH_CACHE.put(key, resultHashFactor);
                    resultMap.put(keysMap.get(key), resultHashFactor);
                } else {
                    resultMap.put(keysMap.get(key), StringConstants.DEFAULT);
                }
            }
        }
        return resultMap;
    }

    public static void registry(String module, int hash, String hashFactor) {
        String key = genKey(module, hash);
        SmartRedisManager.getInstance().set(key, hashFactor);
        HASH_CACHE.put(key, hashFactor);
    }


}
