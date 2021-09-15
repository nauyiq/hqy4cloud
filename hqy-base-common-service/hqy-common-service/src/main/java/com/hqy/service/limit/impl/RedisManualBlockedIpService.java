package com.hqy.service.limit.impl;

import com.hqy.fundation.cache.redis.LettuceRedis;
import com.hqy.service.limit.ManualBlockedIpService;
import com.hqy.service.limit.config.ManualLimitListProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-02 10:51
 */
@Slf4j
@Component
@EnableConfigurationProperties(ManualLimitListProperties.class)
public class RedisManualBlockedIpService implements ManualBlockedIpService {

    @Autowired
    private ManualLimitListProperties manualLimitListProperties;

    //redis key
    private static final String KEY_BLOCKED = "MANUAL_BLOCK_IP";

    //过期时间集合
    private static final Map<String, Long> timestampMap = new ConcurrentHashMap<>();

    //内存存放被人工指定限制的ip
    private static final Set<String> cache = new CopyOnWriteArraySet<>();

    //定时load redis数据到内存中, 减少网络请求 提高效率
    private static final Timer timer = new Timer(RedisManualBlockedIpService.class.getName());


    public RedisManualBlockedIpService() {
        long delay = 3 * 60 * 1000;
        long period = 15 * 60 * 1000;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    cache.clear();
                    cache.addAll(getAllBlockIpSet());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, delay, period);
    }


    @Override
    public void addBlockIp(String ip, int blockSeconds) {
        ip = ip.trim();
        cache.add(ip);
        timestampMap.put(ip, new Date().getTime() + blockSeconds * 1000L);
        LettuceRedis.getInstance().strSAdd(KEY_BLOCKED, Integer.MAX_VALUE, ip);
    }

    @Override
    public void removeBlockIp(String ip) {
        LettuceRedis.getInstance().sMove(KEY_BLOCKED, ip);
        cache.remove(ip);
        timestampMap.remove(ip);
    }

    @Override
    public void clearAllBlockIp() {
        LettuceRedis.getInstance().del(KEY_BLOCKED);
        cache.clear();
        timestampMap.clear();
    }

    @Override
    public Set<String> getAllBlockIpSet() {
        Set<String> ips = LettuceRedis.getInstance().strSMembers(KEY_BLOCKED);
        if (CollectionUtils.isEmpty(ips)) {
            return new HashSet<>();
        } else {
            Date nowDate = new Date();
            boolean foundTimeoutItems = false;
            List<String> removeString = new ArrayList<>();
            for (String ip : ips) {
                if (timestampMap.containsKey(ip)) {
                    if (nowDate.getTime() > timestampMap.get(ip)) {
                        timestampMap.remove(ip);
                        removeString.add(ip);
                        foundTimeoutItems = true;
                    }
                }
            }
            if (foundTimeoutItems) {
                LettuceRedis.getInstance().sMove(KEY_BLOCKED, removeString.toArray(new String[0]));
                ips = LettuceRedis.getInstance().strSMembers(KEY_BLOCKED);
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
        boolean blocked = cache.contains(ip);
        if (blocked) {
            if (!LettuceRedis.getInstance().sIsMember(KEY_BLOCKED, ip)) {
                timestampMap.remove(ip);
                cache.remove(ip);
                return false;
            } else {
                //如果包含，有可能是已经过期了，受限于redis，被识别为没有过期
                int x = new Random().nextInt(100);
                if (x % 4 == 0) {
                    Date nowDate = new Date();
                    if (timestampMap.containsKey(ip)) {
                        if (nowDate.getTime() > timestampMap.get(ip)) {
                            timestampMap.remove(ip);
                            cache.remove(ip);
                            LettuceRedis.getInstance().sMove(KEY_BLOCKED, ip);
                        }
                    }
                }
            }
        }
        return blocked;
    }


}
