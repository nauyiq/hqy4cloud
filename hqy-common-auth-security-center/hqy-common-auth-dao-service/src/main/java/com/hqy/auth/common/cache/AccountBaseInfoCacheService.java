package com.hqy.auth.common.cache;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.auth.entity.AccountProfile;
import com.hqy.auth.service.AccountAuthService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.fundation.cache.support.RedisHashCache;
import com.hqy.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AccountBaseInfoCacheService.
 * @see RedisHashCache
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 16:24
 */
@Slf4j
@Service
public class AccountBaseInfoCacheService extends RedisHashCache<AccountBaseInfoDTO, Long> {

    public AccountBaseInfoCacheService(AccountAuthService accountAuthService, RedissonClient redissonClient) {
        super(MicroServiceConstants.ACCOUNT_SERVICE, AccountBaseInfoDTO.class.getSimpleName(), redissonClient);
        this.accountAuthService = accountAuthService;
    }

    private final AccountAuthService accountAuthService;

    @Override
    protected List<AccountBaseInfoDTO> getCachesFromDb(List<Long> ids) {
        List<AccountInfoDTO> accountInfos = accountAuthService.getAccountInfo(ids);
        if (CollectionUtils.isEmpty(accountInfos)) {
            return null;
        }
        return accountInfos.stream().map(info ->
                new AccountBaseInfoDTO(info.getId(), info.getNickname(), info.getUsername(), info.getEmail(), info.getAvatar(), info.getRoles())).collect(Collectors.toList());
    }

    @Override
    protected AccountBaseInfoDTO getCacheFromDb(Long pk) {
        AccountInfoDTO accountInfo = accountAuthService.getAccountInfo(pk);
        if (accountInfo == null) {
            return null;
        }
        return new AccountBaseInfoDTO(accountInfo.getId(), accountInfo.getNickname(), accountInfo.getUsername(), accountInfo.getEmail(), accountInfo.getAvatar(), accountInfo.getRoles());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected void updateDb(Long pk, AccountBaseInfoDTO cache) {
        AccountProfile accountProfile = accountAuthService.getAccountProfileTkService().queryById(pk);
        if (accountProfile == null) {
            log.warn("Not found account profile, id: {}.", pk);
            return;
        }
        accountProfile.setNickname(cache.getNickname());
        AssertUtil.isTrue(accountAuthService.getAccountProfileTkService().update(accountProfile), CommonResultCode.SYSTEM_ERROR_UPDATE_FAIL.message);
    }

}
