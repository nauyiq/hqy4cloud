package com.hqy.fundation.limit.service;

import java.util.Set;

/**
 * 人工指定ip白名单 service
 * @author qy
 * @create 2021/9/14 23:20
 */
public interface ManualWhiteIpService {

    /**
     * ip 添加到白名单（永久有效）
     * @param ip
     */
    void addWhiteIp(String ip);

    /**
     * ip 移除白名单
     * @param ip
     */
    void removeWhiteIp(String ip);

    /**
     * 获取所有的ip 白名单
     * @return
     */
    Set<String> getAllWhiteIp();

    /**
     * 是否是ip白名单
     * @param ip
     * @return
     */
    boolean isWhiteIp(String ip);

    /**
     * 初始化ip白名单...
     * @param reset 是否重置白名单？(true 表示清除旧的手工设置的白名单.)
     */
    void initializeWhiteIp(boolean reset);

}
