package com.hqy.auth.access.service;

import com.hqy.fundation.cache.redis.RedisUtil;
import com.hqy.fundation.common.swticher.HttpGeneralSwitcher;
import com.hqy.fundation.limit.service.BiBlockedIpService;
import com.hqy.auth.access.config.ManualLimitListProperties;
import com.hqy.util.thread.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 基于Redis的IP黑名单管理；注意，内部为了优化redis，
 * 如果不启用ENABLE_SHARED_BLOCK_IP_LIST的场合，不使用redis来存储，而是使用本地缓存
 * @author qy
 * @date 2021-08-03 11:25
 */
@Slf4j
@Component
@EnableConfigurationProperties(ManualLimitListProperties.class)
public class RedisBiBlockedIpServiceImpl implements BiBlockedIpService {

    @Resource
    private ManualLimitListProperties manualLimitListProperties;

    /**
     * bi分析黑名单 redis key
     */
    private static final String KEY = "BI_BLOCK_IP";

    /**
     * 过期时间集合
     */
    private static final Map<String, Long> TIMESTAMP_MAP = new ConcurrentHashMap<>();

    /**
     * 本地缓存集合
     */
    private static final Set<String> SET_CACHE = new CopyOnWriteArraySet<>();


    /**
     * 定时load redis数据到内存中, 减少网络请求 提高效率
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE
            = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("BiBlock"));


    private RedisBiBlockedIpServiceImpl() {
        long delay = 3 * 60;
        long period = 10 * 60;
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    SET_CACHE.clear();
                    SET_CACHE.addAll(getAllBlockIpSet());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, delay, period, TimeUnit.SECONDS);
    }


    @Override
    public void addBlockIp(String ip, int blockSeconds) {
        ip = ip.trim();
        Date nowDate = new Date();
        long expireTimeStamp = nowDate.getTime() + blockSeconds * 1000L;
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //TODO 基于本地内存缓存的实现
        } else {
            RedisUtil.redisTemplate().set(KEY, ip, (long) blockSeconds);
        }
        TIMESTAMP_MAP.put(ip, expireTimeStamp);
        SET_CACHE.add(ip);
    }

    @Override
    public void removeBlockIp(String ip) {
        ip = ip.trim();
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //TODO 移除内存缓存中的数据
        } else {
            RedisUtil.redisTemplate().del(KEY);
        }
        TIMESTAMP_MAP.remove(ip);
        SET_CACHE.remove(ip);
    }

    @Override
    public void clearAllBlockIp() {
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //TODO 移除内存缓存中的数据
        }
        TIMESTAMP_MAP.clear();
        SET_CACHE.clear();
    }

    @Override
    public Set<String> getAllBlockIpSet() {
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //TODO 返回内存缓存中的数据
            return new HashSet<>();
        } else {
            Set<String> ips = RedisUtil.redisTemplate().keys(KEY);
            if (CollectionUtils.isEmpty(ips)) {
                return new HashSet<>();
            }
            Date now = new Date();
            return ips.stream().filter(ip -> !checkExpire(ip, now)).collect(Collectors.toSet());
        }
    }



    @Override
    public boolean isBlockIp(String ip) {
        Set<String> blockedIps = manualLimitListProperties.getBlockedIps();
        if (blockedIps.contains(ip)) {
            return true;
        }
        ip = ip.trim();

        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //TODO 判断内存缓存中的数据
            return false;
        } else {
            boolean blocked = SET_CACHE.contains(KEY);
            if (blocked) {
                String redisData = RedisUtil.redisTemplate().getString(KEY);
                if (StringUtils.isBlank(redisData)) {
                    TIMESTAMP_MAP.remove(ip);
                    SET_CACHE.remove(KEY);
                    return false;
                } else {
                    // 如果包含， 有可能是已经过期了，受限于redis，被识别为没有过期
                    if (TIMESTAMP_MAP.containsKey(ip)) {
                        if (System.currentTimeMillis() > TIMESTAMP_MAP.get(ip)) {
                            TIMESTAMP_MAP.remove(ip);
                            RedisUtil.redisTemplate().del(KEY);
                            SET_CACHE.remove(KEY);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * 检查是否过期
     * @param ip ip
     * @param now 当前时间
     * @return 是否过期
     */
    private boolean checkExpire(String ip, Date now) {
        if (!TIMESTAMP_MAP.containsKey(ip)) {
            return true;
        }
        if (now.getTime() > TIMESTAMP_MAP.get(ip)) {
            SET_CACHE.remove(KEY);
            TIMESTAMP_MAP.remove(ip);
            return true;
        } else {
            return false;
        }
    }
}
