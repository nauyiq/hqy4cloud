package com.hqy.cloud.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheDelayRemoveService;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheManager;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.AccountMenu;
import com.hqy.cloud.auth.account.entity.AccountProfile;
import com.hqy.cloud.auth.account.service.AccountMenuService;
import com.hqy.cloud.auth.account.service.AccountProfileService;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.auth.service.AccountOperationService;
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
    private final AccountMenuService accountMenuService;
    private final AccountAuthCacheDelayRemoveService cacheDelayRemoveService;
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
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.eq("username", username);
        }
        if (StringUtils.isNotBlank(email)) {
            queryWrapper.eq("email", email);
        }
        if (StringUtils.isNotBlank(phone)) {
            queryWrapper.eq("phone", phone);
        }
        queryWrapper.eq("deleted", 0);
        return !accountService.exists(queryWrapper);
    }

    @Override
    public boolean registryAccount(Account account, AccountProfile accountProfile) {
        // 密码加密
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountService.save(account), "Failed execute to insert Account: " + account);
                AssertUtil.isTrue(accountProfileService.save(accountProfile), "Failed execute to insert account profile, data: " + JsonUtil.toJson(accountProfile));
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        return Boolean.TRUE.equals(result);
    }


    @Override
    public boolean editAccount(UserDTO userDTO, Account account) {
        // 第一次删除缓存
        AccountAuthCacheManager.getInstance().remove(account.getId());
        // update account.
        setAccountInfo(account, userDTO);
        // 第二次删除缓存
        cacheDelayRemoveService.removeAccountAuthCache(account.getId());
        return accountService.updateById(account);
    }

    @Override
    public boolean deleteUser(Account account) {
        Long id = account.getId();
        // 第一次删除缓存
        AccountAuthCacheManager.getInstance().remove(id);
        account.setDeleted(true);
        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountService.updateById(account), "Failed execute to update account.");
                QueryWrapper<AccountMenu> wrapper = Wrappers.query();
                wrapper.eq("account_id", id);
                AssertUtil.isTrue(accountMenuService.remove(wrapper), "Failed execute to remove account menu by accountId: " + id);
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });

        // 第二次删除缓存
        cacheDelayRemoveService.removeAccountAuthCache(id);
        return Boolean.TRUE.equals(result);
    }

    private void setAccountInfo(Account account, UserDTO userDTO) {
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
        account.setRole(UserRole.valueOf(userDTO.getRole()));
        account.setStatus(userDTO.getStatus());
    }

}
