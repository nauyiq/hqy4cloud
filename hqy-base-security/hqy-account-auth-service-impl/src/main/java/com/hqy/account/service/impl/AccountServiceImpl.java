package com.hqy.account.service.impl;

import com.hqy.account.entity.Account;
import com.hqy.account.dao.AccountDao;
import com.hqy.account.service.AccountService;
import com.hqy.auth.dto.UserInfoDTO;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
public class AccountServiceImpl extends BaseTkServiceImpl<Account, Long> implements AccountService {

    @Resource
    private AccountDao accountDao;

    @Override
    public BaseDao<Account, Long> selectDao() {
        return accountDao;
    }

    @Override
    public UserInfoDTO queryUserInfo(String usernameOrEmail) {
        return accountDao.queryUserInfo(usernameOrEmail);
    }
}
