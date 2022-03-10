package com.hqy.security.access.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.fundation.cache.redis.LettuceRedis;
import com.hqy.fundation.limit.service.ManualWhiteIpService;
import com.hqy.security.access.config.ManualLimitListProperties;
import com.hqy.util.spring.SpringContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis ip人工白名单 服务
 * 单例模式
 * @author qy
 * @date  2021/9/14 23:30
 */
@Lazy
@Component
@EnableConfigurationProperties(ManualLimitListProperties.class)
public class RedisManualWhiteIpServiceImpl implements ManualWhiteIpService, InitializingBean {

    @Resource
    private ManualLimitListProperties manualLimitListProperties;

    private static final String KEY_WHITE = "MANUAL_WHITE_IP";

    /**
     * 内存缓存的 白名单
     */
    private static final Cache<String, Long> CACHE_WHITE = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.HOURS)
            .initialCapacity(2048).maximumSize(1024 * 64).build();

    private static volatile ManualWhiteIpService instance = null;

    public static ManualWhiteIpService getInstance() {
        if (instance == null) {
            synchronized (RedisManualWhiteIpServiceImpl.class) {
                if (instance == null) {
                    try {
                        instance = SpringContextHolder.getBean(RedisManualWhiteIpServiceImpl.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        instance = new RedisManualWhiteIpServiceImpl();
                    }
                }
            }
        }
        return instance;
    }


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
        Set<String> whiteIps = manualLimitListProperties.getWhiteIps();
        if (whiteIps.contains(ip)) {
            return true;
        }
        Long timestamp = CACHE_WHITE.getIfPresent(ip);
        if (timestamp != null) {
            if (System.currentTimeMillis() - timestamp > 2 * 60 * 60 * 1000) {
                CACHE_WHITE.invalidate(ip);
            }
            return true;
        }
        Boolean exist = LettuceRedis.getInstance().sIsMember(KEY_WHITE, ip);
        if (exist) {
            CACHE_WHITE.put(ip, System.currentTimeMillis());
        }
        return exist;
    }

    @Override
    public void initializeWhiteIp(boolean reset) {
        //TODO 可加载固定写死的ip 逻辑... 比如服务器内网地址...特殊ip等
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initializeWhiteIp(false);
    }
}
