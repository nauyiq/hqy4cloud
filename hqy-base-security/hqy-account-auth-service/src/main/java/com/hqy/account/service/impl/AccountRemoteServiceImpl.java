package com.hqy.account.service.impl;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.service.AccountAuthService;
import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import com.hqy.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 11:18
 */
@Service
@RequiredArgsConstructor
public class AccountRemoteServiceImpl extends AbstractRPCService implements AccountRemoteService {
    private static final Logger log = LoggerFactory.getLogger(AccountRemoteServiceImpl.class);

    private final AccountAuthService accountAuthService;

    @Override
    public String getAccountInfoJson(Long id) {
        AccountInfoDTO accountInfo = accountAuthService.getAccountInfo(id);
        return accountInfo == null ? StringConstants.EMPTY : JsonUtil.toJson(accountInfo);
    }
}
