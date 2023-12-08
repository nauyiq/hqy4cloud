package com.hqy.cloud.actuator.service.impl;

import com.hqy.cloud.account.service.RemoteAuthService;
import com.hqy.cloud.actuator.service.BasicAuthorizationService;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import com.hqy.cloud.foundation.common.authentication.UsernamePasswordAuthentication;
import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/7 13:48
 */
@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class BasicAuthorizationServiceImpl implements BasicAuthorizationService {
    @Value("${spring.boot.admin.client.username}")
    private String username;
    @Value("${spring.boot.admin.client.password}")
    private String password;

    @Override
    public boolean isAuth(String authorizationHeader) {
        UsernamePasswordAuthentication basicAuthorization = AuthenticationRequestContext.getBasicAuthorization(authorizationHeader);
        if (basicAuthorization == null) {
            return false;
        }
        // 校验BASIC用户名和密码
        if (!StringUtils.isAllBlank(username, password) &&
                username.equals(basicAuthorization.getUsername()) && password.equals(basicAuthorization.getPassword())) {
            return true;
        }
        // 采用账号RPC进行认证
        if (CommonSwitcher.ENABLE_ACCOUNT_RPC_QUERY_ACTUATOR_BASIC_AUTHORIZATION.isOn()) {
            RemoteAuthService authService = RPCClient.getRemoteService(RemoteAuthService.class);
            CommonResultStruct struct = authService.basicAuth(basicAuthorization.getUsername(), basicAuthorization.getUsername());
            return struct.result;
        }
        return false;
    }




}
