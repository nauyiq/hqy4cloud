package com.hqy.cloud.actuator.service.impl;

import com.hqy.cloud.account.service.RemoteAuthService;
import com.hqy.cloud.actuator.service.BasicAuthorizationService;
import com.hqy.cloud.auth.common.UsernamePasswordAuthentication;
import com.hqy.cloud.auth.utils.AuthUtils;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/7 13:48
 */
@Slf4j
@RequiredArgsConstructor
public class BasicAuthorizationServiceImpl implements BasicAuthorizationService {
    @Value("${spring.boot.admin.client.username}")
    private String username;
    @Value("${spring.boot.admin.client.password}")
    private String password;

    @Override
    public boolean isAuth(String authorizationHeader) {
        UsernamePasswordAuthentication basicAuthorization = AuthUtils.getBasicAuthorization(authorizationHeader);
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
            RemoteAuthService authService = RpcClient.getRemoteService(RemoteAuthService.class);
            CommonResultStruct struct = authService.basicAuth(basicAuthorization.getUsername(), basicAuthorization.getUsername());
            return struct.result;
        }
        return false;
    }




}
