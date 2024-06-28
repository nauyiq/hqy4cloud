package com.hqy.cloud.auth.service;

import com.hqy.cloud.account.service.RemoteAuthService;
import com.hqy.cloud.auth.account.entity.SysOauthClient;
import com.hqy.cloud.auth.account.service.SysOauthClientService;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.rpc.thrift.service.AbstractRPCService;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 13:50
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteAuthServiceImpl extends AbstractRPCService implements RemoteAuthService {
    private final AccountOperationService accountOperationService;
    private final AuthOperationService authOperationService;
    private final SysOauthClientService sysOauthClientService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public CommonResultStruct basicAuth(String clientId, String clientSecret) {
        if (StringUtils.isAnyBlank(clientId, clientSecret)) {
            return CommonResultStruct.of(ResultCode.INVALID_CLIENT_OR_SECRET);
        }
        SysOauthClient oauthClient = sysOauthClientService.getById(clientId);
        if (oauthClient == null
                || !clientId.equals(oauthClient.getClientId())
                || passwordEncoder.matches(clientSecret, oauthClient.getClientSecret())) {
            return CommonResultStruct.of(ResultCode.INVALID_CLIENT_OR_SECRET);
        }
        return CommonResultStruct.of();
    }
}
