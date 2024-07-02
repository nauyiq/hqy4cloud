package com.hqy.cloud.gateway.loadbalance.support;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.socket.cluster.HashRouterService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HashRouterServiceImpl implements HashRouterService {
    private final RedissonClient redissonClient;

    @Override
    public void updateHashRoute(String application, int hash, String hostAddress) {
        RMapCache<Integer, String> hashAddressCache = getHashAddressCache(application);
        hashAddressCache.put(hash, hostAddress);
    }

    @Override
    public String getAddress(String application, int hash) {
        RMapCache<Integer, String> hashAddressCache = getHashAddressCache(application);
        RSortedSet<Object> sortedSet = redissonClient.getSortedSet(application);

        return hashAddressCache.get(hash);
    }

    @Override
    public Map<Integer, String> getAddress(String application, Set<Integer> hashSet) {
        RMapCache<Integer, String> hashAddressCache = getHashAddressCache(application);
        return hashAddressCache.getAll(hashSet);
    }

    @Override
    public Map<Integer, String> getAllAddress(String application) {
        RMapCache<Integer, String> hashAddressCache = getHashAddressCache(application);
        return hashAddressCache.readAllMap();
    }

    private RMapCache<Integer, String> getHashAddressCache(String application) {
        AssertUtil.notEmpty(application, "Application name should not be empty.");
        String key = application + StringConstants.Symbol.UNION + ProjectContext.getContextInfo().getEnv() +
                StringConstants.Symbol.UNION + HashRouterService.class.getSimpleName();
        return redissonClient.getMapCache(key);
    }

}
