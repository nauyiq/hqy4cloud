package com.hqy.cloud.auth.service.impl;

import com.hqy.cloud.account.constants.AccountResultCode;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.OauthClient;
import com.hqy.cloud.auth.account.service.AccountDomainService;
import com.hqy.cloud.auth.account.service.SysOauthClientDomainService;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.common.result.BsResultCode;
import com.hqy.cloud.common.result.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountOperationServiceImpl implements AccountOperationService {
    private final AccountDomainService accountDomainService;
    private final SysOauthClientDomainService sysOauthClientDomainService;


    @Override
    public R<Void> registryAccount(Account account) {
        OauthClient oauthClient = sysOauthClientDomainService.findByClientId(account.getClientId());
        if (oauthClient == null) {
            return R.failed(AccountResultCode.AUTH_CLIENT_NOT_EXIST);
        }
        return accountDomainService.save(account) ? R.success() : R.failed(BsResultCode.INSERT_FAILED);
    }



}
