package com.hqy.cloud.auth.limit;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.util.Set;

/**
 * DefaultManualWhiteIpAdaptor.
 * @see com.hqy.foundation.limit.service.ManualWhiteIpService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/27 18:04
 */
public abstract class DefaultManualWhiteIpAdaptor implements ManualWhiteIpService {
    private final RSet<String> setCache;

    public DefaultManualWhiteIpAdaptor(String key, RedissonClient redisson) {
        RedisNamedKey namedKey = new RedisNamedKey(key, ManualWhiteIpService.class.getSimpleName());
        this.setCache = redisson.getSet(namedKey.getKey());
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
        if (CommonSwitcher.ENABLE_PROJECT_CONTEXT_WHITE.isOn()) {
            Set<String> contextInfoWhiteSet = SpringContextHolder.getProjectContextInfo().getAttributeSetString(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY);
            if (CollectionUtils.isNotEmpty(contextInfoWhiteSet)) {
                setCache.addAll(contextInfoWhiteSet);
            }
        }

    }
}
