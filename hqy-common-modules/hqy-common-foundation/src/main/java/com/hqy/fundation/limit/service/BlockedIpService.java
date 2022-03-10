package com.hqy.fundation.limit.service;

import java.util.Set;

/**
 * @author qy
 * @date  2021-08-02 10:23
 */
public interface BlockedIpService {

    /**
     * ip 添加到黑名单
     * @param ip ip
     * @param blockSeconds 封禁多少秒
     */
    public void addBlockIp(String ip,int blockSeconds);

    /**
     * ip 移除黑名单
     * @param ip ip
     */
    public void removeBlockIp(String ip);

    /**
     * 移除黑名单(所有ip)
     */
    public void clearAllBlockIp();

    /**
     * 查询所有的黑名单集合
     * @return 黑名单集合
     */
    public Set<String> getAllBlockIpSet();

    /**
     * 是否是黑名单的阻塞ip
     * @param ip ip
     * @return 是否被阻塞
     */
    public boolean isBlockIp(String ip);

}
