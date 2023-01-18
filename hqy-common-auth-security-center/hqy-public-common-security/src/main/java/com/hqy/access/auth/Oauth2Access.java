package com.hqy.access.auth;

/**
 * Oauth2Access.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 13:37
 */
public interface Oauth2Access {

    /**
     * 此次请求是否允许放行.
     * @param request {@link AuthenticationRequest}
     * @return         result.
     */
    boolean isPermitRequest(AuthenticationRequest request);


}
