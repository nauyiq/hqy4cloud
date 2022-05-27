package com.hqy.account.service.impl;

import com.hqy.account.dao.AccountOauthClientDao;
import com.hqy.account.entity.AccountOauthClient;
import com.hqy.account.service.AccountOauthClientTkService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:53
 */
@Slf4j
@Service
public class AccountOauthClientTkServiceImpl extends BaseTkServiceImpl<AccountOauthClient, Long> implements AccountOauthClientTkService {

    @Resource
    private AccountOauthClientDao accountOauthClientDao;

    @Override
    public BaseDao<AccountOauthClient, Long> selectDao() {
        return accountOauthClientDao;
    }

}
