package com.hqy.cloud.service.impl;

import com.hqy.cloud.mapper.AccountOauthClientTkMapper;
import com.hqy.cloud.entity.AccountOauthClient;
import com.hqy.cloud.service.AccountOauthClientTkService;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.support.BaseTkServiceImpl;
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
    private AccountOauthClientTkMapper accountOauthClientDao;

    @Override
    public BaseTkMapper<AccountOauthClient, Long> getTkDao() {
        return accountOauthClientDao;
    }

}
