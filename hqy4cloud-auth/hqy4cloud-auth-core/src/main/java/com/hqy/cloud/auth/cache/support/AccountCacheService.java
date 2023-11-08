package com.hqy.cloud.auth.cache.support;

import com.hqy.cloud.auth.base.dto.AccountDTO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.auth.service.tk.AccountTkService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import com.hqy.cloud.foundation.cache.support.RedisHashCacheDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AccountInfoCacheService.
 * @see RedisHashCacheDataService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 16:24
 */
@Slf4j
@Service
public class AccountCacheService extends RedisHashCacheDataService<AccountDTO, Long> {
    private final AccountTkService accountTkService;
    public AccountCacheService(AccountTkService accountTkService, RedissonClient redissonClient) {
        super(new RedisNamedKey(MicroServiceConstants.ACCOUNT_SERVICE, AccountDTO.class.getSimpleName()), redissonClient);
        this.accountTkService = accountTkService;
    }

    @Override
    protected List<AccountDTO> getDataBySource(List<Long> ids) {
        List<Account> accounts = accountTkService.queryByIds(ids);
        if (CollectionUtils.isEmpty(accounts)) {
            log.warn("Accounts is empty by ids:{}.", ids);
            return Collections.emptyList();
        }
        return accounts.parallelStream().map(this::build).collect(Collectors.toList());
    }

    @Override
    protected boolean updateData(Long id, AccountDTO cache) {
        Account account = new Account();
        account.setId(id);
        account.setPassword(cache.getPassword());
        account.setRoles(cache.getRoles());
        return accountTkService.updateSelective(account);
    }

    @Override
    protected AccountDTO getDataBySource(Long pk) {
        Account account = accountTkService.queryById(pk);
        if (account == null) {
            return null;
        }
        return build(account);
    }

    private AccountDTO build(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .email(account.getEmail())
                .phone(account.getPhone())
                .roles(account.getRoles())
                .status(account.getStatus())
                .created(account.getCreated().getTime())
                .build();
    }



}
