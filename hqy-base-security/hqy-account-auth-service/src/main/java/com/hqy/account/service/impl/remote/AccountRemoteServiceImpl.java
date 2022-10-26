package com.hqy.account.service.impl.remote;

import com.hqy.account.dto.AccountBaseInfoDTO;
import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.entity.Account;
import com.hqy.account.entity.AccountOauthClient;
import com.hqy.account.entity.AccountProfile;
import com.hqy.account.entity.AccountRole;
import com.hqy.account.service.AccountAuthService;
import com.hqy.account.service.impl.AccountBaseInfoCacheService;
import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.account.struct.*;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.base.lang.exception.UpdateDbException;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import com.hqy.rpc.thrift.struct.CommonResultStruct;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.ValidationUtil;
import com.hqy.util.identity.ProjectSnowflakeIdWorker;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 11:18
 */
@Service
@RequiredArgsConstructor
public class AccountRemoteServiceImpl extends AbstractRPCService implements AccountRemoteService {
    private static final Logger log = LoggerFactory.getLogger(AccountRemoteServiceImpl.class);

    private final AccountAuthService accountAuthService;
    private final AccountBaseInfoCacheService baseInfoCacheService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public String getAccountInfoJson(Long id) {
        AccountInfoDTO accountInfo = accountAuthService.getAccountInfo(id);
        return accountInfo == null ? StringConstants.EMPTY : JsonUtil.toJson(accountInfo);
    }

    @Override
    public Long getAccountIdByUsernameOrEmail(String usernameOrEmail) {
        if (StringUtils.isBlank(usernameOrEmail)) {
            return null;
        }
        Account account = accountAuthService.getAccountTkService().queryOne(new Account(usernameOrEmail));
        if (account == null) {
            return null;
        }
        return account.getId();
    }

    @Override
    public AccountStruct getAccountStructByUsernameOrEmail(String usernameOrEmail) {
        if (StringUtils.isBlank(usernameOrEmail)) {
            return new AccountStruct();
        }
        Account account = accountAuthService.getAccountTkService().queryOne(new Account(usernameOrEmail));
        if (account == null) {
            return new AccountStruct();
        }
        return new AccountStruct(account.getId(), account.getUsername(), account.getEmail(), account.getPhone(), account.getRoles(), account.getStatus());
    }

    @Override
    public AccountBaseInfoStruct getAccountBaseInfo(Long id) {
        AccountBaseInfoDTO accountBaseInfoDTO = baseInfoCacheService.getCache(id);
        if (accountBaseInfoDTO == null) {
            return new AccountBaseInfoStruct();
        }
        return new AccountBaseInfoStruct(accountBaseInfoDTO);
    }

    @Override
    public List<AccountBaseInfoStruct> getAccountBaseInfos(List<Long> ids) {
        List<AccountBaseInfoDTO> caches = baseInfoCacheService.getCaches(ids);
        if (CollectionUtils.isEmpty(caches)) {
            return Collections.emptyList();
        }

        return caches.stream().map(AccountBaseInfoStruct::new).collect(Collectors.toList());
    }

    @Override
    public CommonResultStruct checkRegistryInfo(String username, String email) {
        if (!ValidationUtil.validateEmail(email)) {
            return new CommonResultStruct(false, CommonResultCode.INVALID_EMAIL.code, CommonResultCode.INVALID_EMAIL.message);
        }
        Account account = accountAuthService.getAccountTkService().queryAccountByUsernameOrEmail(email);
        if (account != null) {
            return new CommonResultStruct(false, CommonResultCode.EMAIL_EXIST.code, CommonResultCode.EMAIL_EXIST.message);
        }
        account = accountAuthService.getAccountTkService().queryAccountByUsernameOrEmail(username);
        if (account != null) {
            return new CommonResultStruct(false, CommonResultCode.USERNAME_EXIST.code, CommonResultCode.USERNAME_EXIST.message);
        }

        return new CommonResultStruct(true, CommonResultCode.SUCCESS.code, CommonResultCode.SUCCESS.message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResultStruct registryAccount(RegistryAccountStruct struct) {
        if (StringUtils.isAnyBlank(struct.username, struct.email, struct.password)) {
            return new CommonResultStruct(false, CommonResultCode.ERROR_PARAM.code, CommonResultCode.ERROR_PARAM.message);
        }
        try {
            long id = ProjectSnowflakeIdWorker.getInstance().nextId();
            String nickname = StringUtils.isBlank(struct.nickname) ? struct.username : struct.nickname;
            String password = passwordEncoder.encode(struct.password);

            // insert account.
            Account account = new Account(id, struct.username, password, struct.email);
            if (!accountAuthService.getAccountTkService().insert(account)) {
                return new CommonResultStruct(false, CommonResultCode.SYSTEM_ERROR_INSERT_FAIL.code, "Failed execute to insert account.");
            }
            // insert oauth client.
            AccountOauthClient oauthClient = new AccountOauthClient(id, struct.username, password);
            if (!accountAuthService.getAccountOauthClientTkService().insert(oauthClient)) {
                throw new UpdateDbException("Failed execute to insert to oauth2 client.");
            }

            //insert Account profile
            AccountProfile accountProfile = new AccountProfile(id, nickname, struct.avatar);
            if (!accountAuthService.getAccountProfileTkService().insert(accountProfile)) {
                throw new UpdateDbException("Failed execute to insert to account profile.");
            }

            return new CommonResultStruct();
        } catch (Throwable cause) {
            log.error("Failed execute to registry account. struct: {}, cause: {}.", JsonUtil.toJson(struct), cause.getMessage());
            throw new UpdateDbException(CommonResultCode.SYSTEM_ERROR_INSERT_FAIL.message, cause);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResultStruct updateAccountPassword(String usernameOrEmail, String newPassword) {
        if (StringUtils.isAnyBlank(usernameOrEmail, newPassword)) {
            return new CommonResultStruct(false, CommonResultCode.ERROR_PARAM.code, "Request param should not be empty.");
        }
        Account account = accountAuthService.getAccountTkService().queryOne(new Account(usernameOrEmail));
        if (account == null) {
            return new CommonResultStruct(false, CommonResultCode.USER_NOT_FOUND.code, CommonResultCode.USER_NOT_FOUND.message);
        }
        //更新账号密码
        newPassword = passwordEncoder.encode(newPassword);
        account.setPassword(newPassword);
        accountAuthService.getAccountTkService().update(account);
        //更新oauth2表
        if (!accountAuthService.getAccountOauthClientTkService().updateSelective(new AccountOauthClient(account.getId(), account.getUsername(), newPassword))) {
            throw new UpdateDbException(CommonResultCode.SYSTEM_ERROR_UPDATE_FAIL.message);
        }
        return new CommonResultStruct();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResultStruct updateAccountPasswordByIdAndOldPassword(Long accountId, String oldPassword, String newPassword) {
        if (accountId == null || StringUtils.isAnyBlank(oldPassword, newPassword)) {
            return new CommonResultStruct(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        Account account = accountAuthService.getAccountTkService().queryById(accountId);
        if (account == null) {
            return new CommonResultStruct(CommonResultCode.USER_NOT_FOUND);
        }

        //校验密码是否正确
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            return new CommonResultStruct(CommonResultCode.PASSWORD_ERROR);
        }
        //更新账号密码
        newPassword = passwordEncoder.encode(newPassword);
        account.setPassword(newPassword);
        accountAuthService.getAccountTkService().update(account);
        //更新oauth2表
        if (!accountAuthService.getAccountOauthClientTkService().updateSelective(new AccountOauthClient(account.getId(), account.getUsername(), newPassword))) {
            throw new UpdateDbException(CommonResultCode.SYSTEM_ERROR_UPDATE_FAIL.message);
        }
        return new CommonResultStruct();
    }

    @Override
    public List<ResourcesInRoleStruct> getAuthoritiesResourcesByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return accountAuthService.getResourcesByRoles(roles);
    }

    @Override
    public void updateAuthoritiesResource(String role, List<ResourceStruct> resourceStructs) {
        if (StringUtils.isBlank(role) || CollectionUtils.isEmpty(resourceStructs)) {
            log.warn("Role or resourceStructs should not be empty.");
            return;
        }
        //获取对应role数据。
        AccountRole accountRole = accountAuthService.getAccountRoleTkService().queryOne(new AccountRole(role));
        AssertUtil.notNull(accountRole, "Not found role name: " + role);
        accountAuthService.getAuthoritiesTkService().insertOrUpdateAuthoritiesResource(accountRole.getId(), role, resourceStructs);

    }
}
