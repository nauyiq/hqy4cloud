package com.hqy.cloud.auth.service.impl;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.AccountBaseInfoDTO;
import com.hqy.cloud.auth.entity.AccountProfile;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import com.hqy.cloud.foundation.cache.support.RedisHashCacheDataService;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AccountBaseInfoCacheService.
 * @see RedisHashCacheDataService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 16:24
 */
@Slf4j
@Service
public class AccountBaseInfoCacheDataServiceService extends RedisHashCacheDataService<AccountBaseInfoDTO, Long> {

    public AccountBaseInfoCacheDataServiceService(AccountOperationService accountOperationService, RedissonClient redissonClient) {
        super(new RedisNamedKey(MicroServiceConstants.ACCOUNT_SERVICE, AccountBaseInfoDTO.class.getSimpleName()), redissonClient);
        this.accountOperationService = accountOperationService;
    }

    private final AccountOperationService accountOperationService;

    @Override
    protected List<AccountBaseInfoDTO> getDataBySource(List<Long> ids) {
        List<AccountInfoDTO> accountInfos = accountOperationService.getAccountInfo(ids);
        if (CollectionUtils.isEmpty(accountInfos)) {
            return null;
        }
        return accountInfos.stream().map(info ->
                new AccountBaseInfoDTO(info.getId(), info.getNickname(), info.getUsername(), info.getEmail(), info.getAvatar(), info.getRoles())).collect(Collectors.toList());
    }

    @Override
    protected boolean updateData(Long pk, AccountBaseInfoDTO cache) {
        AccountProfile accountProfile = accountOperationService.getAccountProfileTkService().queryById(pk);
        if (accountProfile == null) {
            log.warn("Not found account profile, id: {}.", pk);
            return false;
        }

        accountProfile.setNickname(cache.getNickname());
        AssertUtil.isTrue(accountOperationService.getAccountProfileTkService().update(accountProfile), CommonResultCode.SYSTEM_ERROR_UPDATE_FAIL.message);
        return true;
    }

    @Override
    protected AccountBaseInfoDTO getDataBySource(Long pk) {
        AccountInfoDTO accountInfo = accountOperationService.getAccountInfo(pk);
        if (accountInfo == null) {
            return null;
        }
        return new AccountBaseInfoDTO(accountInfo.getId(), accountInfo.getNickname(), accountInfo.getUsername(), accountInfo.getEmail(), accountInfo.getAvatar(), accountInfo.getRoles());
    }



}
