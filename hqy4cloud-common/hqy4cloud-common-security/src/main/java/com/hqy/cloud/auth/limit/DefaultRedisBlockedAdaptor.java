package com.hqy.cloud.auth.limit;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.common.swticher.HttpGeneralSwitcher;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import com.hqy.foundation.limit.BlockDTO;
import com.hqy.foundation.limit.service.BlockedIpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
@Slf4j
public abstract class DefaultRedisBlockedAdaptor implements BlockedIpService {
    private final LRUCache<String, BlockDTO> localCache;
    private final RMapCache<String, BlockDTO> rCache;

    public DefaultRedisBlockedAdaptor(String key, RedissonClient redissonClient) {
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
        long now = System.currentTimeMillis();
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            long timeout = blockSeconds * 1000L;
            BlockDTO config = localCache.containsKey(ip) ? localCache.get(ip) : new BlockDTO(timeout, now);
            localCache.put(ip, config, timeout);
        } else {
            BlockDTO config = rCache.getOrDefault(ip, new BlockDTO(blockSeconds * 1000L, now));
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
    public Map<String, BlockDTO> getAllBlocked() {
        Map<String, BlockDTO> allBlockIpMap;
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            allBlockIpMap = MapUtil.newHashMap(localCache.size());
            for (String ip : localCache.keySet()) {
                allBlockIpMap.put(ip, localCache.get(ip));
            }
        } else {
            try {
                allBlockIpMap = rCache.readAllMapAsync().get(5L, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("Async get all blocked ip exception: {}.", e.getMessage(), e);
                allBlockIpMap = MapUtil.newHashMap();
            }
        }
        return allBlockIpMap;
    }

    @Override
    public boolean isBlockIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        ip = ip.trim();
        BlockDTO blockDTO;
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            blockDTO = localCache.get(ip);
        } else {
            blockDTO = rCache.get(ip);
        }
        return !Objects.isNull(blockDTO);
    }

}
