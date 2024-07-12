package com.hqy.cloud.auth.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hqy.cloud.account.service.RemoteAccountService;
import com.hqy.cloud.account.struct.AccountStruct;
import com.hqy.cloud.account.struct.RegistryAccountStruct;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheDelayRemoveService;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheManager;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.AccountProfile;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.auth.base.converter.AccountConverter;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.enums.AccountResultCode;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.auth.service.tansactional.TccRegistryAccountService;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.file.domain.AccountAvatarUtil;
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


/**
 * remote account rpc service.
 * @author qiyuan.hong
 * @date 2022-03-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteAccountServiceImpl extends AbstractRPCService implements RemoteAccountService {
    private final PasswordEncoder passwordEncoder;
    private final AccountOperationService accountOperationService;
    private final AuthOperationService authOperationService;
    private final AccountService accountService;
    private final TccRegistryAccountService tccRegistryAccountService;
    private final AccountAuthCacheDelayRemoveService accountAuthCacheDelayRemoveService;

    @Override
    public String getAccountInfoJson(Long id) {
        AccountInfoDTO accountInfo = accountOperationService.getAccountInfo(id);
        return accountInfo == null ? StringConstants.EMPTY : JsonUtil.toJson(accountInfo);
    }

    @Override
    public Long getAccountIdByUsernameOrEmail(String usernameOrEmail) {
        return accountService.getAccountIdByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    public AccountStruct getAccountById(Long id) {
        AccountInfoDTO accountInfo = accountOperationService.getAccountInfo(id);
        if (accountInfo == null) {
            return null;
        }
        return AccountConverter.CONVERTER.convert(accountInfo);
    }

    @Override
    public List<AccountStruct> getAccountByIds(List<Long> ids) {
        List<AccountInfoDTO> accounts = accountOperationService.getAccountInfo(ids);
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }
        return accounts.stream().map(AccountConverter.CONVERTER::convert).toList();
    }

    @Override
    public AccountStruct getAccountByUsernameOrEmail(String usernameOrEmail) {
        if (StringUtils.isBlank(usernameOrEmail)) {
            return null;
        }
        Long id = accountService.getAccountIdByUsernameOrEmail(usernameOrEmail);
        if (id == null) {
            return null;
        }
        AccountInfoDTO account = accountService.getAccountInfo(id);
        return AccountConverter.CONVERTER.convert(account);
    }


    @Override
    public CommonResultStruct checkRegistryInfo(String username, String email) {
        if (!ValidationUtil.validateEmail(email)) {
            return CommonResultStruct.of(AccountResultCode.INVALID_EMAIL);
        }
        if (accountOperationService.checkParamExist(null, email, null)) {
            return CommonResultStruct.of(AccountResultCode.EMAIL_EXIST);
        }
        if (accountOperationService.checkParamExist(username, null, null)) {
            return CommonResultStruct.of(AccountResultCode.USERNAME_EXIST);
        }
        return CommonResultStruct.of();
    }

    @Override
    public CommonResultStruct registryAccount(RegistryAccountStruct struct) {
        // 检查一下参数是否可用.
        CommonResultStruct result = checkRegisterAccountParamsEnabled(struct);
        if (!result.isResult()) {
            return result;
        }
        Account account = Account.register(struct.username, struct.password, struct.email, struct.phone,
                StringUtils.isNotBlank(struct.role) ? UserRole.valueOf(struct.role) : null, struct.authorities);
        AccountProfile accountProfile = AccountProfile.register(account.getId(), struct.nickname, struct.username, struct.phone, struct.avatar);
        return accountOperationService.registryAccount(account, accountProfile) ? CommonResultStruct.of() : CommonResultStruct.of(AccountResultCode.REGISTER_ACCOUNT_FAILED);
    }


    @Override
    public CommonResultStruct tccRegistryAccount(RegistryAccountStruct struct) {
        // 检查一下参数是否可用.
        CommonResultStruct result = checkRegisterAccountParamsEnabled(struct);
        if (!result.isResult()) {
            return result;
        }
        Account account = Account.register(struct.username, passwordEncoder.encode(struct.password), struct.email, struct.phone,
                StringUtils.isNotBlank(struct.role) ? UserRole.valueOf(struct.role) : null, struct.authorities);
        AccountProfile accountProfile = AccountProfile.register(account.getId(), struct.nickname, struct.username, struct.phone, struct.avatar);
        return tccRegistryAccountService.register(account, accountProfile) ? new CommonResultStruct(true, ResultCode.SUCCESS.code, account.getId().toString()) : CommonResultStruct.of(AccountResultCode.REGISTER_ACCOUNT_FAILED);
    }

    private CommonResultStruct checkRegisterAccountParamsEnabled(RegistryAccountStruct struct) {
        if (StringUtils.isAnyBlank(struct.username, struct.password) || StringUtils.isAllBlank(struct.email, struct.phone)) {
            return CommonResultStruct.of(ResultCode.ERROR_PARAM);
        }
        if (accountOperationService.checkParamExist(struct.username, struct.email, struct.phone)) {
            return CommonResultStruct.of(AccountResultCode.USER_EXIST);
        }
        // 如果头像存在、校验头像是否可用
        if (StringUtils.isNotBlank(struct.avatar) && AccountAvatarUtil.availableAvatar(struct.avatar)) {
            return CommonResultStruct.of(ResultCode.ERROR_PARAM);
        }
        return CommonResultStruct.of();
    }

    @Override
    public CommonResultStruct updateAccountPassword(String usernameOrEmail, String newPassword) {
        if (StringUtils.isAnyBlank(usernameOrEmail, newPassword)) {
            return CommonResultStruct.of(ResultCode.ERROR_PARAM);
        }
        Account account = accountService.queryAccountByUniqueIndex(usernameOrEmail);
        if (account == null) {
            return CommonResultStruct.of(AccountResultCode.USER_NOT_FOUND);
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        // 删除缓存
        AccountAuthCacheManager.getInstance().remove(account.getId());
        boolean result = accountService.updateById(account);
        if (result) {
            // 第二次删除缓存
            accountAuthCacheDelayRemoveService.removeAccountAuthCache(account.getId());
            return CommonResultStruct.of();
        }
        return CommonResultStruct.of(ResultCode.SYSTEM_BUSY);
    }

    @Override
    public CommonResultStruct updateAccountPasswordByIdAndOldPassword(Long accountId, String oldPassword, String newPassword) {
        if (accountId == null || StringUtils.isAnyBlank(oldPassword, newPassword)) {
            return new CommonResultStruct(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        AccountInfoDTO account = accountService.getAccountInfo(accountId);
        if (account == null) {
            return CommonResultStruct.of(AccountResultCode.USER_NOT_FOUND);
        }
        //check password.
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            return CommonResultStruct.of(AccountResultCode.PASSWORD_ERROR);
        }

        UpdateWrapper<Account> wrapper = Wrappers.update();
        wrapper.set("password", passwordEncoder.encode(newPassword));
        wrapper.eq("id", accountId);
        // 第一次删除缓存
        AccountAuthCacheManager.getInstance().remove(accountId);
        if (accountService.update(wrapper)) {
            // 第二次删除缓存
            accountAuthCacheDelayRemoveService.removeAccountAuthCache(accountId);
            return CommonResultStruct.of();
        }
        return CommonResultStruct.of(ResultCode.SYSTEM_BUSY);
    }




}
