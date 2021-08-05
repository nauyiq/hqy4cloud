package com.hqy.server;

import com.hqy.common.swticher.HttpGeneralSwitcher;
import com.hqy.limit.BiBlockedIpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 基于Redis的IP黑名单管理；注意，内部为了优化redis，如果不启用ENABLE_SHARED_BLOCK_IP_LIST的场合，不使用redis来存储，而是使用本地缓存
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-03 11:25
 */
@Slf4j
@Component
public class RedisBiBlockedIpService implements BiBlockedIpService {

    private static final String KEY = "BI_BLOCK_IP";

//    private static final RedisBiBlockedIpService instance = new RedisBiBlockedIpService();

//    public static RedisBiBlockedIpService getInstance() {
//        return instance;
//    }

    //过期时间集合
    private static final Map<String, Long> timestampMap = new ConcurrentHashMap<>();

    private static final Set<String> cache = new CopyOnWriteArraySet<>();

    //定时load redis数据到内存中, 减少网络请求 提高效率
    private static final Timer timer = new Timer(RedisBiBlockedIpService.class.getName());

    private RedisBiBlockedIpService() {
        long delay = 3 * 60 * 1000;
        long period = 10 * 60 * 1000;
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

    private int x  = 0;

    @Override
    public void addBlockIp(String ip, int blockSeconds) {
        ip = ip.trim();
        Date nowDate = new Date();
        long expireTimeStamp = nowDate.getTime() + blockSeconds * 1000L;
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {

        }
    }

    @Override
    public void removeBlockIp(String ip) {

    }

    @Override
    public void clearAllBlockIp() {

    }

    @Override
    public Set<String> getAllBlockIpSet() {
        return new HashSet<>();
    }

    @Override
    public boolean isBlockIp(String ip) {
        return false;
    }
}
