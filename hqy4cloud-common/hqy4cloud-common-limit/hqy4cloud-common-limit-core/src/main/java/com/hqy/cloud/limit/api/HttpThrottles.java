package com.hqy.cloud.limit.api;


import com.hqy.foundation.common.HttpRequestInfo;
import com.hqy.cloud.limit.core.LimitResult;

/**
 * http节流器
 * @author qy
 * @date 2021-08-02 20:27
 */
public interface HttpThrottles {

    /**
     * 是否允许本次客户端的请求？
     * 同一ip：每秒允许同一ip请求数. 默认8
     * @param request {@link HttpRequestInfo}
     * @return        {@link LimitResult}
     */
    LimitResult limitValue(HttpRequestInfo request);

    /**
     * 检查是否是黑客访问.....
     * @param uri              请求uri
     * @param url              请求url
     * @param QueryString QueryString
     * @param requestBody      请求的body
     * @param requestIp        请求ip
     * @return                 LimitResult.
     */
    LimitResult checkHackAccess(String uri, String url, String QueryString, String requestBody, String requestIp);

    /**
     * 是否是uri白名单
     * @param uri request uri
     * @return    result.
     */
    boolean isWhiteURI(String uri);

    /**
     * 当前ip 是否是人工白名单
     * @param remoteAddr request ip
     * @return           result.
     */
    boolean isManualWhiteIp(String remoteAddr);
}
