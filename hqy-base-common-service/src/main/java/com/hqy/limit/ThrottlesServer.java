package com.hqy.limit;


/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-30 16:29
 */
public interface ThrottlesServer {

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
     ** 是否是行为分析的黑名单ip？
     * @param remoteAddr
     * @return
     */
    boolean isBIBlockedIp(String remoteAddr);


    /**
     * 是否是人工指定的拒绝访问的黑名单ip ？
     * @param remoteAddr
     * @return
     */
    boolean isManualBlockedIp(String remoteAddr);
}
