package com.hqy.cloud.auth.api;

/**
 * 用户认证service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/19
 */
public interface AuthUserService {

    /**
     * 通用账号id获取认证的用户
     * @param token access token
     * @return      认证的用户
     */
    AuthUser getAuthUserByToken(String token);

}
