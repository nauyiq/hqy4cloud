package com.hqy.cloud.auth.service.impl;

import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.service.AccountDomainService;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountOperationServiceImpl implements AccountOperationService {
    private final AccountDomainService accountDomainService;
    private final TransactionTemplate transactionTemplate;


    @Override
    public boolean registryAccount(Account account) {
        // 密码加密
        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountDomainService.save(account), "Failed execute to insert Account: " + account);
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        return Boolean.TRUE.equals(result);
    }



}
