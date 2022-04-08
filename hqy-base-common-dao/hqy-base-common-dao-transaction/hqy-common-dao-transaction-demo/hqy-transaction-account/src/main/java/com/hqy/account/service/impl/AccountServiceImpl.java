package com.hqy.account.service.impl;

import com.hqy.account.dao.AccountDao;
import com.hqy.account.service.AccountService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.order.common.entity.Account;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:43
 */
@Service
public class AccountServiceImpl extends BaseTkServiceImpl<Account, Long> implements AccountService {

    @Resource
    private AccountDao accountDao;

    @Override
    public BaseDao<Account, Long> selectDao() {
        return accountDao;
    }
}
