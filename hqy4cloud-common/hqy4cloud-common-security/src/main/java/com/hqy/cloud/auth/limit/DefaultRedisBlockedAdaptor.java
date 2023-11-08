package com.hqy.cloud.auth.limit;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.common.swticher.HttpGeneralSwitcher;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import com.hqy.foundation.limit.service.BlockedIpService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * DefaultBlockedAdaptor.
 * @see com.hqy.foundation.limit.service.BlockedIpService
 * 基于Redis的IP黑名单管理，内部为了优化redis 采用内存加定时器进行管理
 * 如果不启用ENABLE_SHARED_BLOCK_IP_LIST的场合，不使用redis来存储，而是使用本地缓存
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/27 17:08
 */
public abstract class DefaultRedisBlockedAdaptor implements BlockedIpService {
    private volatile int frequency;
    private final LRUCache<String, BlockConfig> localCache;
    private final RMapCache<String, BlockConfig> rCache;


    public DefaultRedisBlockedAdaptor(String key, RedissonClient redissonClient) {
       this(key, redissonClient, 1);
    }

    public DefaultRedisBlockedAdaptor(String key, RedissonClient redissonClient, int frequency) {
        this.frequency = frequency;
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            localCache = CacheUtil.newLRUCache(1024);
            rCache = null;
        } else {
            localCache = null;
            rCache = redissonClient.getMapCache(new RedisNamedKey(MicroServiceConstants.GATEWAY, key).getKey());
        }
    }

    @Override
    public void addBlockIp(String ip, int blockSeconds) {
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            long timeout = blockSeconds * 1000L;
            BlockConfig config = localCache.containsKey(ip) ? localCache.get(ip) : new BlockConfig(0, timeout);
            config.increment();
            localCache.put(ip, config, timeout);
        } else {
            BlockConfig config = rCache.getOrDefault(ip, new BlockConfig(0, blockSeconds * 1000L));
            config.increment();
            rCache.put(ip, config, blockSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public void removeBlockIp(String ip) {
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            localCache.remove(ip);
        } else {
            rCache.remove(ip);
        }
    }

    @Override
    public void clearAllBlockIp() {
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            localCache.clear();
        } else {
            rCache.clear();
        }
    }

    @Override
    public Set<String> getAllBlockIpSet() {
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //未开启redis共享封禁列表 直接return.
            return localCache.keySet();
        }
        return rCache.keySet();
    }

    @Override
    public Map<String, Long> getAllBlockIp() {
        Map<String, Long> allBlockIpMap;
       if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
           //未开启redis共享封禁列表 直接return.
           allBlockIpMap = MapUtil.newHashMap(localCache.size());
           for (String ip : localCache.keySet()) {
               allBlockIpMap.put(ip, localCache.get(ip).getBlockedMillis());
           }
       } else {
           allBlockIpMap = rCache.readAllMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getBlockedMillis()));
       }
       return allBlockIpMap;
    }

    @Override
    public boolean isBlockIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        ip = ip.trim();
        BlockConfig blockConfig;
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            blockConfig = localCache.get(ip);
        } else {
            blockConfig = rCache.get(ip);
        }

        if (Objects.isNull(blockConfig)) {
            return false;
        }

        if (HttpGeneralSwitcher.ENABLE_IP_RATE_LIMIT_HACK_CHECK_RULE.isOff()) {
            return blockConfig.getFrequency() >= this.frequency;
        }
        return true;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }
}
