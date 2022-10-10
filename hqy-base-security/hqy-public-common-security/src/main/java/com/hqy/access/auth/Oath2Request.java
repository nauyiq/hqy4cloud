package com.hqy.access.auth;

import com.hqy.base.common.base.lang.StringConstants;

/**
 * OathRequest.
 * @see Oauth2Access
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 13:56
 */
public interface Oath2Request {

    /**
     * 当前请求的ip.
     * @return ip.
     */
    String requestIp();

    /**
     * 当前请求的uri
     * @return uri
     */
    default String requestUri() {
        return StringConstants.EMPTY;
    }

    /**
     * 当前请求的user agent
     * @return user agent.
     */
    default String requestUserAgent() {
        return StringConstants.EMPTY;
    }

    /**
     * 当前请求的oath access_token
     * @return access_token
     */
    default String requestAccessToken() {
        return StringConstants.EMPTY;
    }

}
