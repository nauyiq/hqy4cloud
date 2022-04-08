package com.hqy.account.service.impl;

import com.hqy.account.service.AccountService;
import com.hqy.order.common.entity.Account;
import com.hqy.order.common.service.AccountRemoteService;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.util.JsonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:45
 */
@Service
public class AccountRemoteServiceImpl extends AbstractRPCService implements AccountRemoteService {

    @Resource
    private AccountService accountService;

    @Override
    public boolean modifyAccount(String account) {
        Account bean = JsonUtil.toBean(account, Account.class);
        if (bean == null) {
            return false;
        }
        return accountService.update(bean);
    }
}
