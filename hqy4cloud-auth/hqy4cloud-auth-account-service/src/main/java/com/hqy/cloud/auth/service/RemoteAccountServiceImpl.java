package com.hqy.cloud.auth.service;

import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.account.service.RemoteAccountService;
import com.hqy.cloud.account.struct.AccountStruct;
import com.hqy.cloud.account.struct.RegistryAccountStruct;
import com.hqy.cloud.auth.base.converter.AccountConverter;
import com.hqy.cloud.auth.base.dto.AccountDTO;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.service.impl.AccountCacheService;
import com.hqy.cloud.auth.service.tk.AccountTkService;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.UpdateDbException;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.rpc.thrift.service.AbstractRPCService;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.hqy.cloud.auth.base.Constants.DEFAULT_COMMON_ROLE;
import static com.hqy.cloud.common.result.ResultCode.USER_NOT_FOUND;

/**
 * remote account rpc service.
 * @author qiyuan.hong
 * @date 2022-03-16 11:18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteAccountServiceImpl extends AbstractRPCService implements RemoteAccountService {
    private final PasswordEncoder passwordEncoder;
    private final AccountOperationService accountOperationService;
    private final AuthOperationService authOperationService;
    private final AccountTkService accountTkService;
    private final AccountCacheService accountCacheService;

    @Override
    public String getAccountInfoJson(Long id) {
        AccountInfoDTO accountInfo = accountOperationService.getAccountInfo(id);
        return accountInfo == null ? StringConstants.EMPTY : JsonUtil.toJson(accountInfo);
    }

    @Override
    public Long getAccountIdByUsernameOrEmail(String usernameOrEmail) {
        Account account = accountTkService.queryOne(new Account(usernameOrEmail));
        if (account == null) {
            return null;
        }
        return account.getId();
    }

    @Override
    public AccountStruct getAccountById(Long id) {
        AccountDTO data = accountCacheService.getData(id);
        if (data == null) {
            return new AccountStruct();
        }
        return AccountConverter.CONVERTER.convert(data);
    }

    @Override
    public AccountStruct getAccountByUsernameOrEmail(String usernameOrEmail) {
        if (StringUtils.isBlank(usernameOrEmail)) {
            return new AccountStruct();
        }
        Account account = accountTkService.queryOne(new Account(usernameOrEmail));
        if (account == null) {
            return new AccountStruct();
        }
        return AccountConverter.CONVERTER.convert(account);
    }


    @Override
    public CommonResultStruct checkRegistryInfo(String username, String email) {
        if (!ValidationUtil.validateEmail(email)) {
            return new CommonResultStruct(ResultCode.INVALID_EMAIL);
        }
        if (accountOperationService.checkParamExist(null, email, null)) {
            return new CommonResultStruct(ResultCode.EMAIL_EXIST);
        }
        if (accountOperationService.checkParamExist(username, null, null)) {
            return new CommonResultStruct(ResultCode.USERNAME_EXIST);
        }
        return new CommonResultStruct();
    }

    @Override
    public CommonResultStruct registryAccount(RegistryAccountStruct struct) {
        if (StringUtils.isAnyBlank(struct.username, struct.email, struct.password)) {
            return new CommonResultStruct(ResultCode.ERROR_PARAM);
        }
        // check username and email params
        if (accountOperationService.checkParamExist(struct.username, struct.email, struct.phone)) {
            return new CommonResultStruct(ResultCode.USER_EXIST);
        }
        // using default user role.
        List<Role> roles;
        if (CollectionUtils.isEmpty(struct.roles)) {
            struct.roles = Collections.singletonList(DEFAULT_COMMON_ROLE);
            roles = Collections.singletonList(Role.)
        } else {
            //check roles.
            List<Role> roles = accountOperationService.getRoleTkService().queryRolesByNames(struct.roles);
            if (struct.createBy != null && !authOperationService.checkEnableModifyRoles(struct.createBy, roles)) {
                return new CommonResultStruct(ResultCode.LIMITED_SETTING_ROLE_LEVEL);
            }
        }

        try {
            UserDTO userDTO = new UserDTO(null, struct.username, struct.nickname, struct.email, null,
                    struct.password, struct.avatar, true, struct.roles);
            if (!accountOperationService.registryAccount(userDTO, roles)) {
                return new CommonResultStruct(ResultCode.SYSTEM_ERROR_INSERT_FAIL);
            }
            return new CommonResultStruct();
        } catch (Throwable cause) {
            log.error("Failed execute to registry account. struct: {}, cause: {}.", JsonUtil.toJson(struct), cause.getMessage());
            throw new UpdateDbException(ResultCode.SYSTEM_ERROR_INSERT_FAIL.message, cause);
        }
    }

    @Override
    public CommonResultStruct updateAccountPassword(String usernameOrEmail, String newPassword) {
        if (StringUtils.isAnyBlank(usernameOrEmail, newPassword)) {
            return new CommonResultStruct(false, ResultCode.ERROR_PARAM.code, "Request param should not be empty.");
        }
        Account account = accountOperationService.getAccountTkService().queryOne(new Account(usernameOrEmail));
        if (account == null) {
            return new CommonResultStruct(USER_NOT_FOUND);
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        return accountOperationService.getAccountTkService().update(account) ? new CommonResultStruct() : new CommonResultStruct(ResultCode.FAILED);
    }

    @Override
    public CommonResultStruct updateAccountPasswordByIdAndOldPassword(Long accountId, String oldPassword, String newPassword) {
        if (accountId == null || StringUtils.isAnyBlank(oldPassword, newPassword)) {
            return new CommonResultStruct(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        Account account = accountOperationService.getAccountTkService().queryById(accountId);
        if (account == null) {
            return new CommonResultStruct(USER_NOT_FOUND);
        }

        //校验密码是否正确
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            return new CommonResultStruct(ResultCode.PASSWORD_ERROR);
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        accountOperationService.getAccountTkService().update(account);
        return accountOperationService.getAccountTkService().update(account) ? new CommonResultStruct() : new CommonResultStruct(ResultCode.FAILED);
    }




}
