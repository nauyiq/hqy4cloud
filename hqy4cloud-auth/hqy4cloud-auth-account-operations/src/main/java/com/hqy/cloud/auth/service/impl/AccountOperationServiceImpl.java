package com.hqy.cloud.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheManager;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.cache.support.AccountCacheService;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.AccountProfile;
import com.hqy.cloud.auth.account.entity.AccountRole;
import com.hqy.cloud.auth.account.entity.Role;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.auth.account.service.AccountProfileService;
import com.hqy.cloud.auth.account.service.AccountRoleService;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.auth.account.service.RoleService;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.account.AccountAvatarUtil;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.COMMA;
import static com.hqy.cloud.common.result.ResultCode.INVALID_UPLOAD_FILE;
import static com.hqy.cloud.foundation.common.account.AccountAvatarUtil.DEFAULT_AVATAR;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountOperationServiceImpl implements AccountOperationService {
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final AccountProfileService accountProfileService;
    private final RoleService roleService;
    private final AccountRoleService accountRoleService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public AccountInfoDTO getAccountInfo(Long id) {
        AssertUtil.notNull(id, "Account id should not be null.");
        AccountInfoDTO accountInfo = accountService.getAccountInfo(id);
        if (Objects.isNull(accountInfo)) {
            return null;
        }
        accountInfo.setAvatar(AccountAvatarUtil.getAvatar(accountInfo.getAvatar()));
        return accountInfo;
    }

    @Override
    public AccountInfoDTO getAccountInfo(String usernameOrEmail) {
        if (StringUtils.isBlank(usernameOrEmail)) {
            return null;
        }
        AccountInfoDTO accountInfo = accountService.getAccountInfoByUsernameOrEmail(usernameOrEmail);
        if (accountInfo != null) {
            accountInfo.setAvatar(AccountAvatarUtil.getAvatar(accountInfo.getAvatar()));
        }
        return accountInfo;
    }

    @Override
    public List<AccountInfoDTO> getAccountInfo(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<AccountInfoDTO> accountInfos = accountService.getAccountInfos(ids);
        if (CollectionUtils.isNotEmpty(accountInfos)) {
            accountInfos = accountInfos.stream().peek(e -> e.setAvatar(AccountAvatarUtil.getAvatar(e.getAvatar()))).collect(Collectors.toList());
        }
        return accountInfos;
    }

    @Override
    public List<AccountInfoDTO> getAccountProfilesByName(String name) {
        if (StringUtils.isBlank(name)) {
            return Collections.emptyList();
        }
        List<AccountInfoDTO> accountInfos = accountService.getAccountInfosByName(name);
        if (CollectionUtils.isNotEmpty(accountInfos)) {
            accountInfos = accountInfos.stream().peek(e -> e.setAvatar(AccountAvatarUtil.getAvatar(e.getAvatar()))).collect(Collectors.toList());
        }
        return accountInfos;
    }

    @Override
    public boolean checkParamExist(String username, String email, String phone) {
        if (StringUtils.isAllEmpty(username, email, phone)) {
            return true;
        }
        Account account = new Account();
        if (StringUtils.isNotBlank(username)) {
            account.setUsername(username);
        }
        if (StringUtils.isNotBlank(email)) {
            account.setUsername(email);
        }
        if (StringUtils.isNotBlank(phone)) {
            account.setUsername(phone);
        }
        return CollectionUtils.isNotEmpty(accountService.queryList(account));
    }

    @Override
    public boolean registryAccount(UserDTO userDTO, List<Role> roles) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Account account = Account.of(userDTO, roles);
        List<AccountRole> accountRoles = buildAccountRole(account, roles);
        AccountProfile accountProfile = buildAccountProfile(account, userDTO);
        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountService.insert(account), "Failed execute to insert Account: " + account);
                AssertUtil.isTrue(accountRoleService.insertList(accountRoles), "Failed execute to insert accountRoles, data: " + JsonUtil.toJson(accountRoles));
                AssertUtil.isTrue(accountProfileService.insert(accountProfile), "Failed execute to insert account profile, data: " + JsonUtil.toJson(accountProfile));
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        return Boolean.TRUE.equals(result);
    }


    private List<AccountRole> buildAccountRole(Account account, List<Role> roles) {
        return roles.stream().map(e -> new AccountRole(account.getId(), e.getId(), e.getLevel())).collect(Collectors.toList());
    }

    private AccountProfile buildAccountProfile(Account account, UserDTO userDTO) {
        return new AccountProfile(account.getId(),
                StringUtils.isBlank(userDTO.getNickname()) ? userDTO.getUsername() : userDTO.getNickname(),
                StringUtils.isBlank(userDTO.getAvatar()) ? DEFAULT_AVATAR : AccountAvatarUtil.extractAvatar(userDTO.getAvatar()));
    }

    @Override
    public boolean deleteAccountRole(Role role) {
        // 第一次删除缓存
        AccountAuthCacheManager.getInstance().remove(role.getName());

        Boolean result = transactionTemplate.execute(status -> {
            try {
                // 伪删除用户
                role.setDeleted(true);
                AssertUtil.isTrue(roleService.updateById(role), ResultCode.SYSTEM_ERROR_UPDATE_FAIL.message);
                // 删除用户角色中间表数据.
                AssertUtil.isTrue(accountRoleService.deleteByAccountRoleIds(List.of(role.getId())), "Failed execute to delete account role.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });

        // 第二次删除缓存
        AccountAuthCacheManager.getInstance().remove(role.getName());
        return Boolean.TRUE.equals(result);
    }


    @Override
    public boolean editAccount(UserDTO userDTO, List<Role> roles, Account account, List<Role> oldRoles) {
        // update account.
        setAccountInfo(account, userDTO, roles);
        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountService.update(account), INVALID_UPLOAD_FILE.message);
                if (CollectionUtils.isNotEmpty(oldRoles)) {
                    AssertUtil.isTrue(accountRoleService.delete(new AccountRole(account.getId())), "Failed execute to delete old account roles.");
                    AssertUtil.isTrue(accountRoleService.insertList(buildAccountRole(account, roles)), "Failed execute to insert new account roles.");
                    accountCacheService.invalid(account.getId());
                }
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        SpringUtil.getBean(AccountCacheService.class).invalid(account.getId());
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean deleteUser(Account account) {
        account.setDeleted(true);
        List<AccountRole> accountRoles = accountRoleService.queryList(new AccountRole(account.getId()));
        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountService.update(account), "Failed execute to update account.");
                if (CollectionUtils.isNotEmpty(accountRoles)) {
                    AssertUtil.isTrue(accountRoleService.deleteByAccountRoles(accountRoles), "Failed execute to deleted account roles.");
                }
                accountCacheService.invalid(account.getId());
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        return Boolean.TRUE.equals(result);
    }

    private void setAccountInfo(Account account, UserDTO userDTO, List<Role> roles) {
        if (StrUtil.isNotBlank(userDTO.getPassword())) {
            account.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (StrUtil.isNotBlank(userDTO.getEmail())) {
            account.setEmail(userDTO.getEmail());
        }
        if (StrUtil.isNotBlank(account.getUsername())) {
            account.setUsername(account.getUsername());
        }
        if (StrUtil.isNotBlank(account.getPhone())) {
            account.setPhone(account.getPhone());
        }
        account.setStatus(userDTO.getStatus());
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());
        account.setRoles(StrUtil.join(COMMA, roleNames));
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public RoleService getRoleService() {
        return roleService;
    }

    public AccountRoleService getAccountRoleService() {
        return accountRoleService;
    }
}
