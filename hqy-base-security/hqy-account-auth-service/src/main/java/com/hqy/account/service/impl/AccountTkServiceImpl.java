package com.hqy.account.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.account.dao.AccountDao;
import com.hqy.account.entity.Account;
import com.hqy.account.service.AccountTkService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
public class AccountTkServiceImpl extends BaseTkServiceImpl<Account, Long> implements AccountTkService {

    private static final Cache<String, Account>  USER_CACHE =
            CacheBuilder.newBuilder().initialCapacity(1024).expireAfterAccess(10, TimeUnit.MINUTES).build();

    @Resource
    private AccountDao accountDao;

    @Override
    public BaseDao<Account, Long> selectDao() {
        return accountDao;
    }


    @Override
    public Account queryAccountByUsernameOrEmail(String usernameOrEmail) {
        Account account = USER_CACHE.getIfPresent(usernameOrEmail);
        if (account == null) {
            account = accountDao.queryAccountByUsernameOrEmail(usernameOrEmail);
            if (account != null) {
                USER_CACHE.put(usernameOrEmail, account);
            }
        }
        return account;
    }


}
