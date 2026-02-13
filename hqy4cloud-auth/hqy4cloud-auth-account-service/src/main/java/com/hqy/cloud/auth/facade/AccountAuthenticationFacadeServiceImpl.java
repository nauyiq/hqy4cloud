package com.hqy.cloud.auth.facade;

import com.hqy.cloud.account.request.AuthenticateRequest;
import com.hqy.cloud.account.request.RefreshTokenRequest;
import com.hqy.cloud.account.response.TokenInfo;
import com.hqy.cloud.account.service.AccountAuthenticationFacadeService;
import com.hqy.cloud.auth.application.AuthenticationApplicationService;
import com.hqy.cloud.common.result.R;
import com.hqy.cloud.rpc.dubbo.DubboConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * RPC 认证服务 Dubbo 实现
 * @author qiyuan.hong
 */
@Slf4j
@RequiredArgsConstructor
@DubboService(version = DubboConstants.DEFAULT_DUBBO_SERVICE_VERSION)
public class AccountAuthenticationFacadeServiceImpl implements AccountAuthenticationFacadeService {
    private final AuthenticationApplicationService authenticationApplicationService;


    @Override
    public R<TokenInfo> authenticate(AuthenticateRequest request) {
        log.info("RPC authenticate request, clientId: {}, grantType: {}, username: {}",
                request.getClientId(), request.getGrantType(), request.getAccessAccount());
        TokenInfo tokenInfo = authenticationApplicationService.authenticate(request);
        return R.ok(tokenInfo);
    }

    @Override
    public R<TokenInfo> refreshToken(RefreshTokenRequest request) {
        log.info("RPC refreshToken request, clientId: {}", request.getClientId());
        TokenInfo tokenInfo = authenticationApplicationService.refreshToken(request);
        return R.ok(tokenInfo);
    }

    @Override
    public R<Boolean> revokeToken(String accessToken) {
        log.info("RPC revokeToken request");
        boolean result = authenticationApplicationService.revokeToken(accessToken);
        return R.ok(result);
    }

}
