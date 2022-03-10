package com.hqy.account.service.impl;

import com.hqy.account.entity.Account;
import com.hqy.account.dao.AccountDao;
import com.hqy.account.service.AccountService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
public class AccountServiceImpl extends BaseTkServiceImpl<Account, Long> implements AccountService {

    @Resource
    private AccountDao accountDao;

    @Override
    public BaseDao<Account, Long> selectDao() {
        return accountDao;
    }
}
