package com.hqy.security.access.service;

import com.hqy.fundation.cache.redis.RedisUtil;
import com.hqy.fundation.limit.service.ManualBlockedIpService;
import com.hqy.security.access.config.ManualLimitListProperties;
import com.hqy.util.thread.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;


/**
 * 基于redis ip人工 黑名单 服务
 * @author qy
 * @date 2021-08-02 10:51
 */
@Slf4j
@Component
@EnableConfigurationProperties(ManualLimitListProperties.class)
public class RedisManualBlockedIpServiceImpl implements ManualBlockedIpService {

    @Resource
    private ManualLimitListProperties manualLimitListProperties;

    /**
     * redis key
     */
    private static final String KEY_BLOCKED = "MANUAL_BLOCK_IP";

    /**
     * 过期时间集合
     */
    private static final Map<String, Long> TIMESTAMP_MAP = new ConcurrentHashMap<>();

    /**
     * 内存存放被人工指定限制的ip
     */
    private static final Set<String> SET_CACHE = new CopyOnWriteArraySet<>();

    /**
     * 定时load redis数据到内存中, 减少网络请求 提高效率
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE
            = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("ManualBlock"));


    public RedisManualBlockedIpServiceImpl() {
        long delay = 3 * 60;
        long period = 15 * 60;
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
        SET_CACHE.add(ip);
        TIMESTAMP_MAP.put(ip, System.currentTimeMillis() + blockSeconds * 1000L);
        RedisUtil.redisTemplate().strSAdd(KEY_BLOCKED, Integer.MAX_VALUE, ip);
    }

    @Override
    public void removeBlockIp(String ip) {
        RedisUtil.redisTemplate().sMove(KEY_BLOCKED, ip);
        SET_CACHE.remove(ip);
        TIMESTAMP_MAP.remove(ip);
    }

    @Override
    public void clearAllBlockIp() {
        RedisUtil.redisTemplate().del(KEY_BLOCKED);
        SET_CACHE.clear();
        TIMESTAMP_MAP.clear();
    }

    @Override
    public Set<String> getAllBlockIpSet() {
        Set<String> ips = RedisUtil.redisTemplate().strSMembers(KEY_BLOCKED);
        if (CollectionUtils.isEmpty(ips)) {
            return new HashSet<>();
        } else {
            Date nowDate = new Date();
            boolean foundTimeoutItems = false;
            List<String> removeString = new ArrayList<>();
            for (String ip : ips) {
                if (TIMESTAMP_MAP.containsKey(ip)) {
                    if (nowDate.getTime() > TIMESTAMP_MAP.get(ip)) {
                        TIMESTAMP_MAP.remove(ip);
                        removeString.add(ip);
                        foundTimeoutItems = true;
                    }
                }
            }
            if (foundTimeoutItems) {
                RedisUtil.redisTemplate().sMove(KEY_BLOCKED, removeString.toArray(new String[0]));
                ips = RedisUtil.redisTemplate().strSMembers(KEY_BLOCKED);
            }
            return ips;
        }

    }

    @Override
    public boolean isBlockIp(String ip) {
        Set<String> blockedIps = manualLimitListProperties.getBlockedIps();
        if (blockedIps.contains(ip)) {
            return true;
        }
        ip = ip.trim();
        boolean blocked = SET_CACHE.contains(ip);
        if (blocked) {
            if (!RedisUtil.redisTemplate().sIsMember(KEY_BLOCKED, ip)) {
                TIMESTAMP_MAP.remove(ip);
                SET_CACHE.remove(ip);
                return false;
            } else {
                //如果包含，有可能是已经过期了，受限于redis，被识别为没有过期
                if (TIMESTAMP_MAP.containsKey(ip)) {
                    if (System.currentTimeMillis() > TIMESTAMP_MAP.get(ip)) {
                        TIMESTAMP_MAP.remove(ip);
                        SET_CACHE.remove(ip);
                        RedisUtil.redisTemplate().sMove(KEY_BLOCKED, ip);
                    }
                }
            }
        }
        return blocked;
    }


}
