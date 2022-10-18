package com.hqy.account.service.impl;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.service.AccountAuthService;
import com.hqy.account.service.AccountOauthClientTkService;
import com.hqy.account.service.AccountProfileTkService;
import com.hqy.account.service.AccountTkService;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Account Auth Service Crud.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
@Service
@RequiredArgsConstructor
public class AccountAuthServiceImpl implements AccountAuthService {
    private static final Logger log = LoggerFactory.getLogger(AccountAuthServiceImpl.class);

    private final AccountTkService accountTkService;
    private final AccountProfileTkService accountProfileTkService;
    private final AccountOauthClientTkService accountOauthClientTkService;


    @Override
    public AccountInfoDTO getAccountInfo(Long id) {
        AssertUtil.notNull(id, "Account id should not be null.");
        AccountInfoDTO accountInfo = accountTkService.getAccountInfo(id);
        if (accountInfo == null) {
            return null;
        }
        settingAvatar(accountInfo);
        return accountInfo;
    }

    private void settingAvatar(AccountInfoDTO accountInfo) {
        String avatar = accountInfo.getAvatar();
        if (StringUtils.isNotBlank(avatar) && !avatar.startsWith(StringConstants.HTTP)) {
            accountInfo.setAvatar(StringConstants.Host.HTTPS_FILE_ACCESS + avatar);
        }
    }

    @Override
    public List<AccountInfoDTO> getAccountInfo(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<AccountInfoDTO> accountInfos = accountTkService.getAccountInfos(ids);
        if (CollectionUtils.isNotEmpty(accountInfos)) {
            accountInfos = accountInfos.stream().peek(this::settingAvatar).collect(Collectors.toList());
        }
        return accountInfos;
    }

    @Override
    public AccountTkService getAccountTkService() {
        return accountTkService;
    }

    @Override
    public AccountProfileTkService getAccountProfileTkService() {
        return accountProfileTkService;
    }

    @Override
    public AccountOauthClientTkService getAccountOauthClientTkService() {
        return accountOauthClientTkService;
    }
}
