package com.hqy.account.service.impl;

import com.hqy.account.service.AccountService;
import com.hqy.account.service.TccAccountService;
import com.hqy.order.common.entity.Account;
import com.hqy.order.common.service.AccountRemoteService;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.util.JsonUtil;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AccountRemoteServiceImpl extends AbstractRPCService implements AccountRemoteService {

    @Resource
    private AccountService accountService;
    @Resource
    private TccAccountService tccAccountService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyAccount(String account) {

        log.info("@@@ tcc 修改账号余额, xid:{}", RootContext.getXID());
        Account bean = JsonUtil.toBean(account, Account.class);
        if (bean == null) {
            return false;
        }
        //TODO 同理正常应该加乐观锁或者悲观锁
        return accountService.update(bean);
    }

    @Override
    public String getAccountById(Long account) {
        Account data = accountService.queryById(account);
        return data == null ? "" : JsonUtil.toJson(data);
    }

    @Override
    public boolean tccModifyAccount(String beforeAccount, String afterAccount) {
        return tccAccountService.modifyAccount(JsonUtil.toBean(beforeAccount, Account.class), JsonUtil.toBean(afterAccount, Account.class));
    }
}
