package com.hqy.access.limit.service;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.common.swticher.HttpGeneralSwitcher;
import com.hqy.foundation.limit.service.BlockedIpService;
import com.hqy.fundation.cache.redis.support.SmartRedisManager;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import com.hqy.util.thread.NamedThreadFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * DefaultBlockedAdaptor.
 * @see com.hqy.foundation.limit.service.BlockedIpService
 * 基于Redis的IP黑名单管理，内部为了优化redis 采用内存加定时器进行管理
 * 如果不启用ENABLE_SHARED_BLOCK_IP_LIST的场合，不使用redis来存储，而是使用本地缓存
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/27 17:08
 */
public abstract class DefaultRedisBlockedAdaptor implements BlockedIpService {

    private final String KEY;

    public DefaultRedisBlockedAdaptor(String KEY, boolean startScheduled) {
        this.KEY = KEY;
        timestampMap = new ConcurrentHashMap<>();
        ipSetCache = new CopyOnWriteArraySet<>();
        if (startScheduled) {
            ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory(KEY));
            executorService.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    reloadDataToCache();
                }
            }, DELAY, PERIOD, TimeUnit.SECONDS);
        }

    }

    /**
     * 过期时间集合
     */
    private final Map<String, Long> timestampMap;

    /**
     * 内存存放指定限制的ip
     */
    private final Set<String> ipSetCache;

    /**
     * 将要被移除的的元素
     */
    private  final Set<String> removingIps = new ConcurrentHashSet<>();

    private void reloadDataToCache() {
        ipSetCache.clear();
        ipSetCache.addAll(getAllBlockIpSet());
        if (CollectionUtils.isNotEmpty(removingIps)) {
            for (String removingIp : removingIps) {
                timestampMap.remove(removingIp);
            }
            if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOn()) {
                SmartRedisManager.getInstance().hDel(KEY, removingIps.toArray(new Object[0]));
            }
            removingIps.clear();
        }
    }


    @Override
    public void addBlockIp(String ip, int blockSeconds) {
        ip = ip.trim();
        ipSetCache.add(ip);
        long data = System.currentTimeMillis() + blockSeconds * 1000L;
        timestampMap.put(ip, data);
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOn()) {
            SmartRedisManager.getInstance().hSet(KEY, ip, data + "");
        }
    }

    @Override
    public void removeBlockIp(String ip) {
        ip = ip.trim();
        ipSetCache.remove(ip);
        timestampMap.remove(ip);
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOn()) {
            SmartRedisManager.getInstance().hDel(KEY, ip);
        }
    }

    @Override
    public void clearAllBlockIp() {
        ipSetCache.clear();
        timestampMap.clear();
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOn()) {
            SmartRedisManager.getInstance().del(KEY);
        }
    }

    @Override
    public Set<String> getAllBlockIpSet() {
        //添加静态黑名单
        addStaticIpBlackList();
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            //未开启redis共享封禁列表 直接return.
            return ipSetCache;
        }
        Map<String, String> ipTimeMap = SmartRedisManager.getInstance().hGetAll(KEY);
        if (MapUtils.isEmpty(ipTimeMap)) {
            return ipSetCache;
        }

        //过滤已经封禁到期的集合
        long now = System.currentTimeMillis();
        Set<String> ipBlockedSet = ipTimeMap.entrySet().stream().filter(entry -> !checkExpire(entry.getKey(), Long.parseLong(entry.getValue()), now))
                .map(Map.Entry::getKey).collect(Collectors.toSet());
        ipSetCache.addAll(ipBlockedSet);

        return ipSetCache;
    }

    private void addStaticIpBlackList() {
        // 静态IP黑名单列表.
        Set<String> attributeSetString =
                SpringContextHolder.getProjectContextInfo().getAttributeSetString(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY);

        if (CollectionUtils.isNotEmpty(attributeSetString)) {
            ipSetCache.addAll(attributeSetString);
        }
    }

    @Override
    public Map<String, Long> getAllBlockIp() {
        //添加静态黑名单
        addStaticIpBlackList();
        Map<String, Long> map = MapUtil.newHashMap(ipSetCache.size());
        ipSetCache.forEach(s -> map.put(s, null));
        if (CommonSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            return map;
        }

        Map<String, String> ipTimeMap = SmartRedisManager.getInstance().hGetAll(KEY);
        if (MapUtil.isEmpty(ipTimeMap)) {
            return map;
        }

        long now = System.currentTimeMillis();
        Map<String, Long> stringMap = ipTimeMap.entrySet().stream().filter(entry -> !checkExpire(entry.getKey(), Long.parseLong(entry.getValue()), now))
                .map(Map.Entry::getKey).collect(Collectors.toMap(key -> key, Convert::toLong));
        map.putAll(stringMap);
        return map;
    }

    @Override
    public boolean isBlockIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        ip = ip.trim();
        if (HttpGeneralSwitcher.ENABLE_SHARED_BLOCK_IP_LIST.isOff()) {
            return ipSetCache.contains(ip);
        }
        boolean contains = ipSetCache.contains(ip);
        if (contains) {
            if (SmartRedisManager.getInstance().hExists(KEY, ip)) {
                // 如果包含，有可能是已经过期了，受限于redis，被识别为没有过期
                if (timestampMap.containsKey(ip) && System.currentTimeMillis() > timestampMap.get(ip)) {
                    removeBlockIp(ip);
                    return false;
                }
                return true;
            } else {
                removeBlockIp(ip);
            }
        }
        return false;
    }

    /**
     * 检查是否失效 不再对此ip进行封禁
     * @param ip    ip
     * @param time  redis封禁的时间
     * @param now   当前时间戳
     * @return      是否失效
     */
    protected boolean checkExpire(String ip, long time, long now) {
        Long expired = timestampMap.get(ip);
        if (expired != null) {
            if (now > expired) {
                removingIps.add(ip);
                return true;
            } else {
                return false;
            }
        }

        if (now > time) {
            removingIps.add(ip);
            return true;
        }

        return false;
    }
}
