package com.hqy.auth.access.service;

import com.hqy.fundation.cache.redis.RedisUtil;
import com.hqy.base.common.swticher.HttpGeneralSwitcher;
import com.hqy.fundation.limit.service.BiBlockedIpService;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import com.hqy.util.thread.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
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
public class RedisBiBlockedIpServiceImpl implements BiBlockedIpService {

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
        if (StringUtils.isBlank(ip)) {
            return;
        }
        ip = ip.trim();
        long expireTimeStamp = System.currentTimeMillis() + blockSeconds * 1000L;
        //放到redis
        RedisUtil.instance().strSAdd(ProjectContextInfo.BI_BLOCKED_IP_KEY, blockSeconds, ip);
        //放到内存
        TIMESTAMP_MAP.put(ip, expireTimeStamp);
        SET_CACHE.add(ip);
    }

    @Override
    public void removeBlockIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return;
        }
        ip = ip.trim();
        RedisUtil.instance().sMove(ProjectContextInfo.BI_BLOCKED_IP_KEY, ip);
        TIMESTAMP_MAP.remove(ip);
        SET_CACHE.remove(ip);
    }

    @Override
    public void clearAllBlockIp() {
        TIMESTAMP_MAP.clear();
        SET_CACHE.clear();
    }

    @Override
    public Set<String> getAllBlockIpSet() {
        Set<String> attributeSetString =
                SpringContextHolder.getProjectContextInfo().getAttributeSetString(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY);

        if (CollectionUtils.isNotEmpty(attributeSetString)) {
            SET_CACHE.addAll(attributeSetString);
        }

        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //不开启共享黑名单
            return SET_CACHE;
        } else {
            //加载redis中的黑名单列表
            Set<String> ips = RedisUtil.instance().keys(ProjectContextInfo.BI_BLOCKED_IP_KEY);
            if (CollectionUtils.isEmpty(ips)) {
                return SET_CACHE;
            }
            Date now = new Date();
            Set<String> ipBlockedSet = ips.stream().filter(ip -> !checkExpire(ip, now)).collect(Collectors.toSet());
            ipBlockedSet.addAll(SET_CACHE);
            return ipBlockedSet;
        }
    }



    @Override
    public boolean isBlockIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        ip = ip.trim();
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            return SET_CACHE.contains(ip);
        } else {
            boolean blocked = SET_CACHE.contains(ip);
            if (blocked) {
                Boolean isMember = RedisUtil.instance().sIsMember(ProjectContextInfo.BI_BLOCKED_IP_KEY, ip);
                if (!isMember) {
                    TIMESTAMP_MAP.remove(ip);
                    SET_CACHE.remove(ip);
                    return false;
                } else {
                    // 如果包含，有可能是已经过期了，受限于redis，被识别为没有过期
                    if (TIMESTAMP_MAP.containsKey(ip)) {
                        if (System.currentTimeMillis() > TIMESTAMP_MAP.get(ip)) {
                            TIMESTAMP_MAP.remove(ip);
                            RedisUtil.instance().sMove(ProjectContextInfo.BI_BLOCKED_IP_KEY, ip);
                            SET_CACHE.remove(ip);
                            return false;
                        }
                    }
                }
            } else {
                return false;
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
            SET_CACHE.remove(ProjectContextInfo.BI_BLOCKED_IP_KEY);
            TIMESTAMP_MAP.remove(ip);
            return true;
        } else {
            return false;
        }
    }
}
