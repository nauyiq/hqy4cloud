package com.hqy.service.limit.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.fundation.cache.redis.LettuceRedis;
import com.hqy.service.limit.ManualWhiteIpService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author qy
 * @create 2021/9/14 23:30
 */
@Lazy
@Component
public class RedisManualWhiteIpService implements ManualWhiteIpService, InitializingBean {

    private static final String KEY_WHITE = "MANUAL_WHITE_IP";

    private static final Cache<String, Long> CACHE_WHITE = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.HOURS)
            .initialCapacity(2048).maximumSize(1024 * 64).build();


    @Override
    public void addWhiteIp(String ip) {
        LettuceRedis.getInstance().strSAdd(KEY_WHITE, ip);
        CACHE_WHITE.put(ip, System.currentTimeMillis());
    }

    @Override
    public void removeWhiteIp(String ip) {
        LettuceRedis.getInstance().sMove(KEY_WHITE, ip);
        CACHE_WHITE.invalidate(ip);
    }

    @Override
    public Set<String> getAllWhiteIp() {
        Set<String> ips = LettuceRedis.getInstance().strSMembers(KEY_WHITE);
        if (CollectionUtils.isEmpty(ips)) {
            return new HashSet<>();
        }
        return ips;
    }

    @Override
    public boolean isWhiteIp(String ip) {
        return false;
    }

    @Override
    public void initializeWhiteIp(boolean reset) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
