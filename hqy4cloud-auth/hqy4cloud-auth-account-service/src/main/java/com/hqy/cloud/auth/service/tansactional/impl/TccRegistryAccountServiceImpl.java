package com.hqy.cloud.auth.service.tansactional.impl;

import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.AccountProfile;
import com.hqy.cloud.auth.account.service.AccountProfileService;
import com.hqy.cloud.auth.account.service.AccountDomainService;
import com.hqy.cloud.auth.service.tansactional.TccRegistryAccountService;
import com.hqy.cloud.util.AssertUtil;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/19
 */
@Slf4j
@Service
@LocalTCC
@RequiredArgsConstructor
public class TccRegistryAccountServiceImpl implements TccRegistryAccountService {
    private final AccountDomainService accountDomainService;
    private final AccountProfileService accountProfileService;
    private final TransactionTemplate template;

    @Override
    @TwoPhaseBusinessAction(name = "registerAccountInfo", useTCCFence = true)
    public boolean register(@BusinessActionContextParameter(paramName = "account") Account account,
                            @BusinessActionContextParameter(paramName = "accountProfile") AccountProfile profile) {
        // 新增用户，并且用户状态设置为不可用
        String xid = RootContext.getXID();
        account.setStatus(false);
        return accountDomainService.save(account);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean commit(BusinessActionContext actionContext) {
        Account account = actionContext.getActionContext("account", Account.class);
        AccountProfile profile = actionContext.getActionContext("accountProfile", AccountProfile.class);
        if (account == null || profile == null) {
            log.warn("BusinessActionContext not found user params.");
            return false;
        }
        // 查找try阶段锁定的用户
        Account queryAccount = accountDomainService.getById(account.getId());
        if (queryAccount == null) {
            return false;
        }
        queryAccount.setStatus(true);
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(accountDomainService.updateById(queryAccount), "Failed execute to update account.");
                AssertUtil.isTrue(accountProfileService.save(profile), "Failed execute to insert account profiles.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });
        return Boolean.TRUE.equals(execute);
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        // 回滚
        Account account = actionContext.getActionContext("account", Account.class);
        if (account != null) {
            accountDomainService.removeById(account.getId());
        }
        return true;
    }


}
