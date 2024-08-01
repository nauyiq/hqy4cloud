package com.hqy.cloud.auth.api;

import com.hqy.cloud.common.base.lang.StringConstants;

import java.util.Collections;
import java.util.List;

/**
 * AuthenticationRequest.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 13:56
 */
public interface AuthenticationRequest {

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

    /**
     * 获取当前请求的方法
     * @return HTTP_METHOD
     */
    default String method() {
        return StringConstants.EMPTY;
    }

    /**
     * 获取当前请求 携带的权限角色
     * @return 携带的权限角色
     */
    default List<String> authorities() {
        return Collections.emptyList();
    }





}
