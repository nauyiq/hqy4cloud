package com.hqy.cloud.auth.service.tansactional.impl;

import com.alibaba.fastjson.JSONObject;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.auth.entity.AccountProfile;
import com.hqy.cloud.auth.entity.AccountRole;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.service.tansactional.TccRegistryAccountService;
import com.hqy.cloud.auth.service.tk.AccountProfileTkService;
import com.hqy.cloud.auth.service.tk.AccountRoleTkService;
import com.hqy.cloud.auth.service.tk.AccountTkService;
import com.hqy.cloud.foundation.common.account.AccountAvatarUtil;
import com.hqy.cloud.util.AssertUtil;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    private final AccountTkService accountTkService;
    private final AccountRoleTkService accountRoleTkService;
    private final AccountProfileTkService accountProfileTkService;
    private final TransactionTemplate template;

    @Override
    @TwoPhaseBusinessAction(name = "registerAccountInfo", useTCCFence = true)
    public boolean register(@BusinessActionContextParameter(paramName = "account") Account account,
                            @BusinessActionContextParameter(paramName = "user") UserDTO user,
                            @BusinessActionContextParameter(paramName = "roles") List<Role> roles) {
        // 新增用户，并且用户状态设置为不可用
        String xid = RootContext.getXID();
        account.setStatus(false);
        return accountTkService.insert(account);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean commit(BusinessActionContext actionContext) {
        Account account = actionContext.getActionContext("account", Account.class);
        UserDTO user = actionContext.getActionContext("user", UserDTO.class);
        List<JSONObject> roleJsonObjects = actionContext.getActionContext("roles", List.class);
        if (account == null || user == null || CollectionUtils.isEmpty(roleJsonObjects)) {
            return false;
        }
        // 查找try阶段锁定的用户
        Long id = account.getId();
        Account queryAccount = accountTkService.queryById(id);
        if (queryAccount == null) {
            return false;
        }
        queryAccount.setStatus(true);
        AccountProfile profile = new AccountProfile(id,
                StringUtils.isBlank(user.getNickname()) ? user.getUsername() : user.getNickname(),
                StringUtils.isBlank(user.getAvatar()) ? AccountAvatarUtil.DEFAULT_AVATAR: user.getAvatar());

        List<AccountRole> accountRoles = roleJsonObjects.stream().map(r -> {
            Role role = r.toJavaObject(Role.class);
            return new AccountRole(id, role.getId(), role.getLevel());
        }).toList();
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(accountTkService.update(queryAccount), "Failed execute to update account.");
                AssertUtil.isTrue(accountProfileTkService.insert(profile), "Failed execute to insert account profiles.");
                AssertUtil.isTrue(accountRoleTkService.insertList(accountRoles), "Failed execute to insert account roleJsonObjects.");
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
            accountTkService.deleteByPrimaryKey(account.getId());
        }
        return true;
    }


}
