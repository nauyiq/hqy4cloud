package com.hqy.cloud.auth.application;

import com.hqy.cloud.account.request.AuthenticateRequest;
import com.hqy.cloud.account.request.RefreshTokenRequest;
import com.hqy.cloud.account.response.TokenInfo;

/**
 * 认证核心服务
 * <pre>
 *     脱离 HTTP 上下文的认证逻辑实现
 * </pre>
 * @author hongqy
 * @date 2026/1/14
 */
public interface AuthenticationApplicationService {

    /**
     * 认证并生成Token
     * @param request 认证请求
     * @return        Token信息
     */
    TokenInfo authenticate(AuthenticateRequest request);

    /**
     * 刷新Token
     * @param request 刷新Token请求
     * @return        新的Token信息
     */
    TokenInfo refreshToken(RefreshTokenRequest request);

    /**
     * 吊销Token
     * @param accessToken 访问Token
     * @return            是否吊销成功
     */
    boolean revokeToken(String accessToken);
}
