package com.hqy.foundation.limit.service;

import java.util.Map;
import java.util.Set;

/**
 * BlockedIpService.
 * @author qy
 * @date  2021-08-02 10:23
 */
public interface BlockedIpService {

    long DELAY = 30;
    long PERIOD = 3 * 60;

    /**
     * ip 添加到黑名单
     * @param ip ip
     * @param blockSeconds 封禁多少秒
     */
    void addBlockIp(String ip,int blockSeconds);

    /**
     * ip 移除黑名单
     * @param ip ip
     */
    void removeBlockIp(String ip);

    /**
     * 移除黑名单(所有ip)
     */
    void clearAllBlockIp();

    /**
     * 查询所有的黑名单集合
     * @return 黑名单集合
     */
    Set<String> getAllBlockIpSet();

    /**
     * 查询所有的黑名单集合 包括对应的过期时间
     * @return 黑名单集合
     */
    Map<String, Long> getAllBlockIp();

    /**
     * 是否是黑名单的阻塞ip
     * @param ip ip
     * @return 是否被阻塞
     */
    boolean isBlockIp(String ip);

}
