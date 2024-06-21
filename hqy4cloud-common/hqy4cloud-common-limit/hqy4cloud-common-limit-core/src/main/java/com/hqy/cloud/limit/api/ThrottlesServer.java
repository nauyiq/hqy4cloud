package com.hqy.cloud.limit.api;


/**
 * @author qy
 * @create 2021-07-30
 */
public interface ThrottlesServer {

    /**
     * 解析字符串内容， 判断是否黑客攻击 比如xss
     * @param paramStringOrUri 请求url或者请求参数
     * @param mode 0：参数校验模式 1：uri校验模式
     * @return
     */
    boolean isHackAccess(String paramStringOrUri, int mode);

    /**
     * 是否是白名单
     * @param remoteAddress
     * @return
     */
    boolean isWhiteIp(String remoteAddress);

    /**
     * 是否是白名单uri
     * @param uri
     * @return
     */
    boolean isWhiteUri(String uri);


    /**
     ** 是否是行为分析的黑名单ip？
     * @param remoteAddr
     * @return
     */
    boolean isBiBlockedIp(String remoteAddr);

    /**
     * bi行为分析 添加黑名单 并设置封禁的时间
     * @param remoteAddr
     * @param blockSeconds
     */
    void addBiBlockIp(String remoteAddr, Integer blockSeconds);

    /**
     * 添加人工手动黑名单 并设置封禁的时间
     * @param remoteAddr
     * @param blockSeconds
     */
    void addManualBlockIp(String remoteAddr, Integer blockSeconds);


    /**
     * 是否是人工指定的拒绝访问的黑名单ip ？
     * @param remoteAddr
     * @return
     */
    boolean isManualBlockedIp(String remoteAddr);




}
