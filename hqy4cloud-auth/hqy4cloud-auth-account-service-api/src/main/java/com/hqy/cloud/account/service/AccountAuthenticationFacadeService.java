package com.hqy.cloud.account.service;

import com.hqy.cloud.account.request.AuthenticateRequest;
import com.hqy.cloud.account.request.RefreshTokenRequest;
import com.hqy.cloud.account.response.TokenInfo;
import com.hqy.cloud.common.result.R;

/**
 * RPC 认证服务接口
 * 提供给其他服务调用的认证方法，用于获取Token
 * @author qiyuan.hong
 */
public interface AccountAuthenticationFacadeService {


    /**
     * 认证并获取Token
     * @param request 认证请求参数
     * @return        Token信息
     */
    R<TokenInfo> authenticate(AuthenticateRequest request);

    /**
     * 刷新Token
     * @param request 刷新Token请求参数
     * @return        新的Token信息
     */
    R<TokenInfo> refreshToken(RefreshTokenRequest request);

    /**
     * 吊销Token
     * @param accessToken 访问令牌
     * @return            是否成功
     */
    R<Boolean> revokeToken(String accessToken);

}
