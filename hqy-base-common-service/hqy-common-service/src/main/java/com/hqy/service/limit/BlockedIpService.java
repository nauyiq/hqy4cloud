package com.hqy.service.limit;

import java.util.Set;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-02 10:23
 */
public interface BlockedIpService {

    /**
     * ip 添加到黑名单
     * @param ip
     * @param blockSeconds
     */
    public void addBlockIp(String ip,int blockSeconds);

    /**
     * ip 移除黑名单
     * @param ip
     */
    public void removeBlockIp(String ip);

    /**
     * 移除黑名单(所有ip)
     */
    public void clearAllBlockIp();

    /**
     * @return 查询所有的黑名单集合
     */
    public Set<String> getAllBlockIpSet();

    /**
     * 是否是黑名单的阻塞ip
     * @param ip
     * @return
     */
    public boolean isBlockIp(String ip);

}
