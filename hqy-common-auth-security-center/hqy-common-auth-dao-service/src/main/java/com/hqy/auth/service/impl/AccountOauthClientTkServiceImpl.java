package com.hqy.auth.service.impl;

import com.hqy.auth.dao.AccountOauthClientDao;
import com.hqy.auth.entity.AccountOauthClient;
import com.hqy.auth.service.AccountOauthClientTkService;
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
    public BaseDao<AccountOauthClient, Long> getTkDao() {
        return accountOauthClientDao;
    }

}