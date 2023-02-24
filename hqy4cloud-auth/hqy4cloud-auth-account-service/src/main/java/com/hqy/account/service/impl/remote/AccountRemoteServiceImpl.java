package com.hqy.account.service.impl.remote;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.account.struct.*;
import com.hqy.cloud.common.cache.AccountBaseInfoCacheService;
import com.hqy.cloud.common.cache.AccountBaseInfoDTO;
import com.hqy.cloud.common.dto.UserDTO;
import com.hqy.cloud.entity.Account;
import com.hqy.cloud.entity.AccountOauthClient;
import com.hqy.cloud.entity.Role;
import com.hqy.cloud.service.AccountAuthService;
import com.hqy.cloud.service.AccountInfoOperationService;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.UpdateDbException;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import com.hqy.rpc.thrift.struct.CommonResultStruct;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.ValidationUtil;
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

import static com.hqy.cloud.common.Constants.DEFAULT_COMMON_ROLE;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 11:18
 */
@Service
@RequiredArgsConstructor
public class AccountRemoteServiceImpl extends AbstractRPCService implements AccountRemoteService {
    private static final Logger log = LoggerFactory.getLogger(AccountRemoteServiceImpl.class);

    private final AccountInfoOperationService accountInfoOperationService;
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
        return buildAccountBaseInfoStruct(accountBaseInfoDTO);
    }

    private AccountBaseInfoStruct buildAccountBaseInfoStruct(AccountBaseInfoDTO accountBaseInfoDTO) {
        return new AccountBaseInfoStruct(accountBaseInfoDTO.getId(), accountBaseInfoDTO.getNickname(), accountBaseInfoDTO.getUsername(),
                accountBaseInfoDTO.getEmail(), accountBaseInfoDTO.getAvatar(), accountBaseInfoDTO.getRoles());
    }


    @Override
    public List<AccountBaseInfoStruct> getAccountBaseInfos(List<Long> ids) {
        List<AccountBaseInfoDTO> caches = baseInfoCacheService.getCaches(ids);
        if (CollectionUtils.isEmpty(caches)) {
            return Collections.emptyList();
        }

        return caches.stream().map(this::buildAccountBaseInfoStruct).collect(Collectors.toList());
    }

    @Override
    public CommonResultStruct checkRegistryInfo(String username, String email) {
        if (!ValidationUtil.validateEmail(email)) {
            return new CommonResultStruct(false, CommonResultCode.INVALID_EMAIL.code, CommonResultCode.INVALID_EMAIL.message);
        }
        if (accountInfoOperationService.checkParamExist(null, email, null)) {
            return new CommonResultStruct(false, CommonResultCode.EMAIL_EXIST.code, CommonResultCode.EMAIL_EXIST.message);
        }
        if (accountInfoOperationService.checkParamExist(username, null, null)) {
            return new CommonResultStruct(false, CommonResultCode.USERNAME_EXIST.code, CommonResultCode.USERNAME_EXIST.message);
        }
        return new CommonResultStruct(true, CommonResultCode.SUCCESS.code, CommonResultCode.SUCCESS.message);
    }

    @Override
    public CommonResultStruct registryAccount(RegistryAccountStruct struct) {
        if (StringUtils.isAnyBlank(struct.username, struct.email, struct.password)) {
            return new CommonResultStruct(false, CommonResultCode.ERROR_PARAM.code, CommonResultCode.ERROR_PARAM.message);
        }

        //注册角色为空时 使用默认角色名
        if (CollectionUtils.isEmpty(struct.roles)) {
            struct.roles = Collections.singletonList(DEFAULT_COMMON_ROLE);
        }

        // check params
        if (accountInfoOperationService.checkParamExist(struct.username, struct.email, null)) {
            return new CommonResultStruct(false, CommonResultCode.ERROR_PARAM.code, "username or email already exist.");
        }

        //check roles.
        List<Role> roles = accountAuthService.getRoleTkService().queryRolesByNames(struct.roles);
        if (struct.createBy != null && !accountInfoOperationService.checkEnableModifyRoles(struct.createBy, roles)) {
            return new CommonResultStruct(false, CommonResultCode.ERROR_PARAM.code, "Not permission create user.");
        }

        try {
            UserDTO userDTO = new UserDTO(null, struct.username, struct.nickname, struct.email, null,
                    struct.password, struct.avatar, true, struct.roles);
            if (!accountInfoOperationService.registryAccount(userDTO, roles)) {
                return new CommonResultStruct(CommonResultCode.SYSTEM_ERROR_INSERT_FAIL);
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
    public List<AuthenticationStruct> getAuthoritiesResourcesByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return accountAuthService.getAuthoritiesResourcesByRoles(roles);
    }

    @Override
    public void updateAuthoritiesResource(String role, List<ResourceStruct> resourceStructs) {
        if (StringUtils.isBlank(role) || CollectionUtils.isEmpty(resourceStructs)) {
            log.warn("Role or resourceStructs should not be empty.");
            return;
        }
        //获取对应role数据。
        Role accountRole = accountAuthService.getRoleTkService().queryOne(new Role(role));
        AssertUtil.notNull(accountRole, "Not found role name: " + role);
        accountAuthService.getRoleResourcesTkService().insertOrUpdateRoleResources(accountRole.getId(), role, resourceStructs);

    }
}
