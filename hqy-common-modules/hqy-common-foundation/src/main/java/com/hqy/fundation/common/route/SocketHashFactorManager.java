package com.hqy.fundation.common.route;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.fundation.cache.redis.RedisUtil;
import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 借助redis和guava来标记某个socket服务的hash节点
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 15:52
 */
public class SocketHashFactorManager {

    private static final Logger log = LoggerFactory.getLogger(SocketHashFactorManager.class);

    private SocketHashFactorManager() {}

    private static final int DB = 1;

    private static final Cache<String, String> HASH_CACHE =
            CacheBuilder.newBuilder().initialCapacity(1024).expireAfterAccess(10L, TimeUnit.MINUTES).build();


    private static String genKey(String module, int hash) {
        return SocketClusterStatus.class.getSimpleName().concat(BaseStringConstants.Symbol.COLON)
                .concat(module).concat(BaseStringConstants.Symbol.COLON).concat(hash + "");
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
            hashFactor = RedisUtil.instance().selectDb(DB, true).getString(key);
            if (StringUtils.isBlank(hashFactor)) {
                hashFactor = ThriftRpcHelper.DEFAULT_HASH_FACTOR;
            }
            HASH_CACHE.put(key, hashFactor);
        }
        return hashFactor;
    }

    public static void registry(String module, int hash, String hashFactor) {
        String key = genKey(module, hash);
        RedisUtil.instance().selectDb(DB, true).set(key, hashFactor, Long.MAX_VALUE);
        HASH_CACHE.put(key, hashFactor);
    }





}
