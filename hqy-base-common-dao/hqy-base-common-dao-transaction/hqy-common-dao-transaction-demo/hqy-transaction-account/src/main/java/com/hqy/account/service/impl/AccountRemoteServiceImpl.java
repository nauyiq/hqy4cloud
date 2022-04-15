package com.hqy.account.service.impl;

import com.hqy.account.service.AccountService;
import com.hqy.order.common.entity.Account;
import com.hqy.order.common.service.AccountRemoteService;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.util.JsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean modifyAccount(String account) {
        Account bean = JsonUtil.toBean(account, Account.class);
        if (bean == null) {
            return false;
        }
        return accountService.update(bean);
    }

    @Override
    public String queryById(Long account) {
        Account data = accountService.queryById(account);
        return data == null ? "" : JsonUtil.toJson(data);
    }
}
