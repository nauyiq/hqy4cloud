package com.hqy.gateway.server;

import com.hqy.cache.redis.LettuceRedis;
import com.hqy.common.swticher.HttpGeneralSwitcher;
import com.hqy.limit.BiBlockedIpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * 基于Redis的IP黑名单管理；注意，内部为了优化redis，如果不启用ENABLE_SHARED_BLOCK_IP_LIST的场合，不使用redis来存储，而是使用本地缓存
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-03 11:25
 */
@Slf4j
@Component
public class RedisBiBlockedIpService implements BiBlockedIpService {

    private static final String KEY = "BI_BLOCK_IP:";
    //过期时间集合
    private static final Map<String, Long> timestampMap = new ConcurrentHashMap<>();
    //本地缓存集合
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
        String key = KEY + ip;
        long expireTimeStamp = nowDate.getTime() + blockSeconds * 1000L;
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //TODO 基于本地内存缓存的实现
        } else {
            LettuceRedis.getInstance().set(key, ip, (long)blockSeconds);
        }
        timestampMap.put(ip, expireTimeStamp);
        cache.add(key);
    }

    @Override
    public void removeBlockIp(String ip) {
        ip = ip.trim();
        String key = KEY + ip;
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //TODO 移除内存缓存中的数据
        } else {
            LettuceRedis.getInstance().del(key);
        }
        timestampMap.remove(ip);
        cache.remove(ip);
    }

    @Override
    public void clearAllBlockIp() {
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //TODO 移除内存缓存中的数据
        }
        timestampMap.clear();
        cache.clear();
    }

    @Override
    public Set<String> getAllBlockIpSet() {
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //TODO 返回内存缓存中的数据
            return new HashSet<>();
        } else {
            Set<String> ips = LettuceRedis.getInstance().keys(KEY);
            if (CollectionUtils.isEmpty(ips)) {
                return new HashSet<>();
            }
            Date now = new Date();
            return ips.stream().filter(ip -> checkExpire(ip, now)).collect(Collectors.toSet());
        }
    }


    @Override
    public boolean isBlockIp(String ip) {
       ip = ip.trim();
       String key = KEY + ip;
       if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
           //TODO 判断内存缓存中的数据
           return false;
       } else {
           boolean blocked = cache.contains(key);
           if (blocked) {
               String redisData = LettuceRedis.getInstance().getString(key);
               boolean contains = StringUtils.isNotBlank(redisData);
               if (!contains) {
                   timestampMap.remove(ip);
                   cache.remove(key);
                   return false;
               } else {
                   // 如果包含， 有可能是已经过期了，受限于redis，被识别为没有过期
                   int x = new Random().nextInt(100);
                   if (x % 4 == 0) {
                       Date now = new Date();
                       if (timestampMap.containsKey(ip)) {
                           if (now.getTime() > timestampMap.get(ip)) {
                               timestampMap.remove(ip);
                               LettuceRedis.getInstance().del(key);
                               cache.remove(key);
                               return false;
                           }
                       }
                   }
               }
           }
           return blocked;
       }
    }


    /**
     * 检查是否过期
     * @param ip
     * @param now
     * @return
     */
    private boolean checkExpire(String ip, Date now) {
        if (!timestampMap.containsKey(ip)) {
            return true;
        }
        //把ip截取出来
        String[] split = ip.split(KEY);
        if (split.length > 1 && timestampMap.containsKey(split[1])) {
            ip = split[1];
            if (now.getTime() > timestampMap.get(ip)) {
                //清除过期缓存
                String key = KEY + ip;
                cache.remove(key);
                timestampMap.remove(ip);
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
}
