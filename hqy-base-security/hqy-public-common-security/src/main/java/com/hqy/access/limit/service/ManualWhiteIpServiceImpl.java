package com.hqy.access.limit.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import com.hqy.fundation.cache.redis.LettuceStringRedis;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 基于内存和redis ip人工白名单 服务
 * @author qy
 * @date 2021/9/14 23:30
 */
@Lazy
@Component
public class ManualWhiteIpServiceImpl implements ManualWhiteIpService, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ManualWhiteIpServiceImpl.class);

    /**
     * 内存缓存的 白名单
     */
    private static final Cache<String, Long> CACHE_WHITE = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.HOURS)
            .initialCapacity(2048).maximumSize(1024 * 64).build();

    private Set<String> contextInfoWhiteSet = new HashSet<>();


    @Override
    public void addWhiteIp(String ip) {
        LettuceStringRedis.getInstance().sAdd(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY, ip);
        CACHE_WHITE.put(ip, System.currentTimeMillis());
    }

    @Override
    public void removeWhiteIp(String ip) {
        LettuceStringRedis.getInstance().sRem(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY, ip);
        CACHE_WHITE.invalidate(ip);
        contextInfoWhiteSet.remove(ip);
    }

    @Override
    public Set<String> getAllWhiteIp() {
        Set<String> ips = LettuceStringRedis.getInstance().sMembers(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY);
        if (CollectionUtils.isEmpty(ips)) {
            return new HashSet<>();
        }
        return ips;
    }

    @Override
    public boolean isWhiteIp(String ip) {
        if (contextInfoWhiteSet.contains(ip)) {
            return true;
        }
        Long timestamp = CACHE_WHITE.getIfPresent(ip);
        if (timestamp != null) {
            if (System.currentTimeMillis() - timestamp > BaseMathConstants.ONE_MINUTES_4MILLISECONDS * 2 * 60) {
                CACHE_WHITE.invalidate(ip);
            }
            return true;
        }
        boolean exist = LettuceStringRedis.getInstance().sIsMember(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY, ip);
        if (exist) {
            CACHE_WHITE.put(ip, System.currentTimeMillis());
        }
        return exist;
    }

    @Override
    public void initializeWhiteIp(boolean reset) {
        log.info("@@@ ManualWhiteService Initialize, reset = {}", reset);
        if (CommonSwitcher.ENABLE_PROJECT_CONTEXT_WHITE.isOn()) {
            contextInfoWhiteSet = SpringContextHolder.getProjectContextInfo().getAttributeSetString(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY);
            if (Objects.isNull(contextInfoWhiteSet)) {
                contextInfoWhiteSet = new HashSet<>();
            }
        }
        if (reset) {
            LettuceStringRedis.getInstance().del(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initializeWhiteIp(false);
    }
}
