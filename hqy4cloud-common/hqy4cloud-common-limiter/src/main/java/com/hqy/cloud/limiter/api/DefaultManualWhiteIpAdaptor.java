package com.hqy.cloud.limiter.api;

import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.util.Set;

/**
 * DefaultManualWhiteIpAdaptor.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/27 18:04
 */
public abstract class DefaultManualWhiteIpAdaptor implements ManualWhiteIpService {
    private final RSet<String> setCache;

    public DefaultManualWhiteIpAdaptor(String key, RedissonClient redisson) {
        this.setCache = redisson.getSet(key);
    }

    @Override
    public void addWhiteIp(String ip) {
        setCache.addAsync(ip);
    }

    @Override
    public void removeWhiteIp(String ip) {
        setCache.remove(ip);
    }

    @Override
    public Set<String> getAllWhiteIp() {
        return setCache.readAll();
    }

    @Override
    public boolean isWhiteIp(String ip) {
        return setCache.contains(ip);
    }

    @Override
    public void initializeWhiteIp(boolean reset) {
        if (reset) {
            setCache.clear();
        }
    }
}
