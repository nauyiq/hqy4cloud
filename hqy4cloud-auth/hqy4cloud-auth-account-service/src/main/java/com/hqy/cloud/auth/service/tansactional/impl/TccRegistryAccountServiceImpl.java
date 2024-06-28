package com.hqy.cloud.auth.service.tansactional.impl;

import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.AccountProfile;
import com.hqy.cloud.auth.account.entity.AccountRole;
import com.hqy.cloud.auth.account.service.AccountProfileService;
import com.hqy.cloud.auth.account.service.AccountRoleService;
import com.hqy.cloud.auth.account.service.AccountService;
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

import java.util.List;

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
    private final AccountService accountService;
    private final AccountRoleService accountRoleService;
    private final AccountProfileService accountProfileService;
    private final TransactionTemplate template;

    @Override
    @TwoPhaseBusinessAction(name = "registerAccountInfo", useTCCFence = true)
    public boolean register(@BusinessActionContextParameter(paramName = "account") Account account,
                            @BusinessActionContextParameter(paramName = "accountProfile") AccountProfile profile) {
        // 新增用户，并且用户状态设置为不可用
        String xid = RootContext.getXID();
        account.setStatus(false);
        return accountService.save(account);
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
        Account queryAccount = accountService.getById(account.getId());
        if (queryAccount == null) {
            return false;
        }
        queryAccount.setStatus(true);
        List<AccountRole> accountRoles = accountRoleService.registerAccountRole(account);
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(accountService.updateById(queryAccount), "Failed execute to update account.");
                AssertUtil.isTrue(accountProfileService.save(profile), "Failed execute to insert account profiles.");
                AssertUtil.isTrue(accountRoleService.saveBatch(accountRoles), "Failed execute to insert account roleJsonObjects.");
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
            accountService.removeById(account.getId());
        }
        return true;
    }


}
