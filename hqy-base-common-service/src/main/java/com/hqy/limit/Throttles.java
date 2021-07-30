package com.hqy.limit;

import com.hqy.util.IpUtil;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-30 16:29
 */
public interface Throttles {

    /**
     * 根据ip是否限制
     * @param remoteAddress
     * @return
     */
     default boolean needLimit(String remoteAddress) {
         return !IpUtil.isIP(remoteAddress);
     }

    /**
     * 解析字符串内容， 判断是否黑客攻击 比如xss
     * @param paramStringOrUri
     * @return
     */
    boolean isHackAccess(String paramStringOrUri);

    /**
     * 是否是白名单
     * @param remoteAddress
     * @return
     */
    boolean isWhiteIp(String remoteAddress);

    /**
     * 是否是黑名单
     * @param remoteAddress
     * @return
     */
    boolean isBlockedIp(String remoteAddress);
}
