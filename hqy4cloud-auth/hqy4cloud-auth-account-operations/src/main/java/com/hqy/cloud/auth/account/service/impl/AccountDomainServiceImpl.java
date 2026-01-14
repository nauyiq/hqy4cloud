package com.hqy.cloud.auth.account.service.impl;

import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheManager;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.mapper.AccountMapper;
import com.hqy.cloud.auth.account.service.AccountDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
@RequiredArgsConstructor
public class AccountDomainServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountDomainService {

    @Override
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.MINUTES)
    @Cached(name = AccountAuthCacheManager.ACCOUNT_USER_CACHE_KEY, expire = 1440,  cacheType = CacheType.BOTH, key = "#id", cacheNullValue = true)
    public Account findById(Long id) {
        return getBaseMapper().findById(id);
    }

    @Override
    public Account queryAccountByUniqueIndex(String uniqueIndex) {
        return getBaseMapper().queryAccountByUniqueIndex(uniqueIndex);
    }
}
