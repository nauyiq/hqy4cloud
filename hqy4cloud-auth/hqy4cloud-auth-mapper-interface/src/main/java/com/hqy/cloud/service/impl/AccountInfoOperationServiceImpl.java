package com.hqy.cloud.service.impl;

import com.hqy.cloud.common.cache.AccountBaseInfoCacheService;
import com.hqy.cloud.common.dto.UserDTO;
import com.hqy.cloud.service.AccountAuthService;
import com.hqy.cloud.service.AccountInfoOperationService;
import com.hqy.cloud.service.AccountRoleTkService;
import com.hqy.cloud.entity.*;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.identity.ProjectSnowflakeIdWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.COMMA;
import static com.hqy.cloud.common.result.CommonResultCode.INVALID_UPLOAD_FILE;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/13 16:24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountInfoOperationServiceImpl implements AccountInfoOperationService {

    private final AccountAuthService accountAuthService;
    private final AccountRoleTkService accountRoleTkService;
    private final PasswordEncoder passwordEncoder;
    private final AccountBaseInfoCacheService accountBaseInfoCacheService;

    @Override
    public boolean checkParamExist(String username, String email, String phone) {
        if (!StringUtils.isEmpty(username)) {
            Account account = accountAuthService.getAccountTkService().queryOne(new Account(username));
            if (account != null) {
                return true;
            }
        }

        if (!StringUtils.isEmpty(email)) {
            Account account = accountAuthService.getAccountTkService().queryOne(new Account(email));
            if (account != null) {
                return true;
            }
        }

        if (!StringUtils.isEmpty(phone)) {
            Account account = new Account();
            account.setPhone(phone);
            account = accountAuthService.getAccountTkService().queryOne(account);
            return account != null;
        }

        return false;
    }

    @Override
    public boolean checkEnableModifyRoles(Long id, List<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        List<Integer> collect = roles.stream().map(Role::getLevel).collect(Collectors.toList());
        return getAccountMaxAuthorityRoleLevel(id) <= Collections.min(collect);
    }

    @Override
    public int getAccountMaxAuthorityRoleLevel(Long id) {
        Account account = accountAuthService.getAccountTkService().queryById(id);
        AssertUtil.notNull(account, "Account should no be null.");

        String[] roleArrays = StringUtils.tokenizeToStringArray(account.getRoles(), COMMA);
        List<Role> roles = accountAuthService.getRoleTkService().queryRolesByNames(Arrays.asList(roleArrays));
        AssertUtil.notEmpty(roles, "Account Roles should no be empty.");

        List<Integer> levelList = roles.stream().map(Role::getLevel).collect(Collectors.toList());
        return Collections.min(levelList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registryAccount(UserDTO userDTO, List<Role> roles) {
        // encode password
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // insert account.
        Account account = buildAccount(userDTO, roles);
        if (!accountAuthService.getAccountTkService().insert(account)) {
            return false;
        }

        // insert role.
        List<AccountRole> accountRoles = buildAccountRole(account, roles);
        AssertUtil.isTrue(accountRoleTkService.insertList(accountRoles), "Failed execute to insert accountRoles, data: " + JsonUtil.toJson(accountRoles));

        // insert profile.
        AccountProfile accountProfile = buildAccountProfile(account, userDTO);
        AssertUtil.isTrue(accountAuthService.getAccountProfileTkService().insert(accountProfile), "Failed execute to insert account profile, data: " + JsonUtil.toJson(accountProfile));

        // insert account oauth2 client
        AccountOauthClient accountOauthClient = buildAccountOauthClient(account);
        AssertUtil.isTrue(accountAuthService.getAccountOauthClientTkService().insert(accountOauthClient),
                "Failed execute to insert to oauth2 client, data: " + JsonUtil.toJson(accountOauthClient));

        return true;
    }

    private Account buildAccount(UserDTO userDTO, List<Role> roles) {
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());
        String role = org.apache.commons.lang3.StringUtils.join(roleNames, COMMA);
        Account account = new Account(userDTO.getUsername(), userDTO.getPassword(), userDTO.getEmail(), role);
        account.setPhone(userDTO.getPhone());
        account.setId(ProjectSnowflakeIdWorker.getInstance().nextId());
        if (userDTO.getStatus() != null) {
            account.setStatus(userDTO.getStatus());
        }
        return account;
    }

    private List<AccountRole> buildAccountRole(Account account, List<Role> roles) {
        return roles.stream().map(e -> new AccountRole(account.getId(), e.getId(), e.getLevel())).collect(Collectors.toList());
    }

    private AccountProfile buildAccountProfile(Account account, UserDTO userDTO) {
        return new AccountProfile(account.getId(), userDTO.getNickname(), userDTO.getAvatar());
    }

    private AccountOauthClient buildAccountOauthClient(Account account) {
        return new AccountOauthClient(account.getId(), account.getUsername(), account.getPassword());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editAccount(UserDTO userDTO, List<Role> roles, Account account, List<Role> oldRoles) {
        // update account.
        setAccountInfo(account, userDTO, roles);
        AssertUtil.isTrue(accountAuthService.getAccountTkService().update(account), INVALID_UPLOAD_FILE.message);

        // update account roles.
        if (CollectionUtils.isNotEmpty(oldRoles)) {
            AssertUtil.isTrue(accountRoleTkService.delete(new AccountRole(account.getId())), "Failed execute to delete old account roles.");
            AssertUtil.isTrue(accountRoleTkService.insertList(buildAccountRole(account, roles)), "Failed execute to insert new account roles.");
        }

        //update oauth client
        AccountOauthClient client = accountAuthService.getAccountOauthClientTkService().queryById(account.getId());
        AssertUtil.notNull(client, "Oauth client should not be null.");
        setOauthClient(client, account);
        AssertUtil.isTrue(accountAuthService.getAccountOauthClientTkService().update(client), "Failed execute to update oauth client.");

        //delete cache
        accountBaseInfoCacheService.invalid(account.getId());
    }

    private void setOauthClient(AccountOauthClient client, Account account) {
        client.setClientSecret(account.getPassword());
        client.setStatus(account.getStatus());
    }

    private void setAccountInfo(Account account, UserDTO userDTO, List<Role> roles) {
        if (!StringUtils.isEmpty(userDTO.getPassword())) {
            account.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        String email = userDTO.getEmail();
        if (!StringUtils.isEmpty(email)) {
            account.setEmail(email);
        }
        if (!StringUtils.isEmpty(account.getUsername())) {
            account.setUsername(account.getUsername());
        }
        if (!StringUtils.isEmpty(account.getPhone())) {
            account.setPhone(account.getPhone());
        }
        account.setStatus(userDTO.getStatus());
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());
        account.setRoles(org.apache.commons.lang3.StringUtils.join(roleNames, COMMA));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Account account) {
        account.setDeleted(true);
        AssertUtil.isTrue(accountAuthService.getAccountTkService().update(account), "Failed execute to update account.");
        AccountOauthClient accountOauthClient = accountAuthService.getAccountOauthClientTkService().queryById(account.getId());
        if (accountOauthClient != null) {
            accountOauthClient.setStatus(false);
            AssertUtil.isTrue(accountAuthService.getAccountOauthClientTkService().update(accountOauthClient), "Failed execute to update oauth client.");
        }
    }

}
