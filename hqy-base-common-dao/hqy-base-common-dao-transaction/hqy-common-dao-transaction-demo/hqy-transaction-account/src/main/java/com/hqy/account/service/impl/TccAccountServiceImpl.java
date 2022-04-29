package com.hqy.account.service.impl;

import com.hqy.account.service.AccountService;
import com.hqy.account.service.TccAccountService;
import com.hqy.order.common.entity.Account;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/29 14:35
 */
@Slf4j
@Service
public class TccAccountServiceImpl implements TccAccountService {

    @Resource
    private AccountService service;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean modifyAccount(Account beforeAccount, Account afterAccount) {
        log.info("xid = {}", RootContext.getXID());
        if (!service.update(afterAccount)) {
            throw new RuntimeException("更新数据库失败.");
        }
        return true;
    }

    @Override
    public boolean commitTcc(BusinessActionContext context) {
        return true;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        Account beforeAccount = (Account) context.getActionContext().get("beforeAccount");
        return service.update(beforeAccount);
    }
}
