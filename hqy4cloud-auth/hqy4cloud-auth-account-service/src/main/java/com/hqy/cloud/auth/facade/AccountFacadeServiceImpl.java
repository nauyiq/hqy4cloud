package com.hqy.cloud.auth.facade;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.hqy.cloud.account.constants.AccountResultCode;
import com.hqy.cloud.account.constants.AccountStatus;
import com.hqy.cloud.account.request.AccountAuthRequest;
import com.hqy.cloud.account.request.AccountModifyRequest;
import com.hqy.cloud.account.request.AccountQueryParams;
import com.hqy.cloud.account.request.RegistryAccountByPhoneParams;
import com.hqy.cloud.account.response.AccountInfo;
import com.hqy.cloud.account.response.AccountOperationInfo;
import com.hqy.cloud.account.response.RegisterInfo;
import com.hqy.cloud.account.service.AccountFacadeService;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheManager;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.service.AccountDomainService;
import com.hqy.cloud.auth.base.AccountConstants;
import com.hqy.cloud.auth.base.converter.AccountConverter;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.auth.infrastructure.certification.service.AuthService;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.result.BsResultCode;
import com.hqy.cloud.common.result.R;
import com.hqy.cloud.infrastructure.random.RandomCodeScene;
import com.hqy.cloud.infrastructure.random.RandomCodeService;
import com.hqy.cloud.rpc.dubbo.DubboConstants;
import com.hqy.cloud.rpc.dubbo.facade.Facade;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/11
 */
@RequiredArgsConstructor
@DubboService(version = DubboConstants.DEFAULT_DUBBO_SERVICE_VERSION)
public class AccountFacadeServiceImpl implements AccountFacadeService, InitializingBean {
    private final PasswordEncoder passwordEncoder;
    private final RedissonClient redissonClient;
    private final AccountDomainService accountDomainService;
    private final AuthService authService;
    private final RandomCodeService randomCodeService;
    private final AccountOperationService accountOperationService;

    /**
     * 用户名布隆过滤器
     */
    private RBloomFilter<String> usernameBloomFilter;;

    @Facade
    @Override
    public R<AccountInfo> query(AccountQueryParams queryParams) {
        Account account = null;
        Long id = queryParams.getId();
        if (id != null) {
            account = accountDomainService.findById(id);
        } else if (StringUtils.isNotBlank(queryParams.getPhone())) {
            account = accountDomainService.queryAccountByUniqueIndex(queryParams.getPhone());
        } else if (StringUtils.isNotBlank(queryParams.getEmail())) {
            account = accountDomainService.queryAccountByUniqueIndex(queryParams.getEmail());
        }
        if (account == null) {
            return R.failed(AccountResultCode.USER_NOT_FOUND);
        }

        AccountInfo accountInfo = AccountConverter.CONVERTER.mapToVo(account);
        return R.ok(accountInfo);
    }

    @Facade
    @Override
    public R<List<AccountInfo>> queryList(Collection<Long> ids) {
        List<Account> accounts = accountDomainService.listByIds(ids);
        return R.ok(accounts.stream().map(AccountConverter.CONVERTER::mapToVo).toList());
    }

    @Facade
    @Override
    public R<RegisterInfo> registerByPhone(RegistryAccountByPhoneParams registryParams) {
        String phone = registryParams.getPhone();
        String code = registryParams.getCode();
        // 检查一下验证码是否正确.
        if (!randomCodeService.isExist(code, phone, RandomCodeScene.SMS_AUTH)) {
            return R.failed(AccountResultCode.VERIFY_CODE_ERROR);
        }
        String username = getUsername(registryParams);
        String password = StringUtils.isNotBlank(registryParams.getPassword()) ? registryParams.getPassword() : registryParams.getPhone();
        password = passwordEncoder.encode(password);
        Account account = Account.register(registryParams.getClientId(), username, password, null, phone, UserRole.CUSTOMER);
        R<Void> result = accountOperationService.registryAccount(account);
        if (result.isSuccess()) {
            addUsername(username);
            // 异步对验证进行续期.防止验证码过期无法登录
            Thread.ofVirtual().start(() -> randomCodeService.saveCode(code, phone, RandomCodeScene.SMS_AUTH));
            return R.ok(new RegisterInfo(account.getId(), account.getUsername()));
        }
        return R.failed();
    }

    private String getUsername(RegistryAccountByPhoneParams registryParams) {
        String username = registryParams.getUsername();
        if (StringUtils.isNotBlank(username) && !usernameExist(username)) {
            return username;
        }
        do {
            String randomString = RandomUtil.randomString(6).toUpperCase();
            username = AccountConstants.DEFAULT_NICKNAME_PREFIX + randomString + registryParams.getPhone().substring(7, 11);
        } while (usernameExist(username));
        return username;
    }

    private boolean usernameExist(String username) {
        if (this.usernameBloomFilter != null && this.usernameBloomFilter.contains(username)) {
            return accountDomainService.queryAccountByUniqueIndex(username) != null;
        }
        return false;
    }

    @Facade
    @Override
    @Transactional
    @CacheInvalidate(name = AccountAuthCacheManager.ACCOUNT_USER_CACHE_KEY, key = "#request.id")
    public R<AccountOperationInfo> auth(AccountAuthRequest request) {
        Account account = accountDomainService.findById(request.getId());
        Assert.notNull(account, () -> new BizException(AccountResultCode.USER_NOT_FOUND));

        if (account.getCertification()) {
            // 已经实名
            return R.ok(AccountOperationInfo.of(AccountConverter.CONVERTER.mapToVo(account)));
        }
        if (account.getStatus() == AccountStatus.DISABLED) {
            throw new BizException(AccountResultCode.USER_DISABLED);
        }
        // 验证实名是否正常
        if (!authService.checkAuth(request.getRearName(), request.getIdCard())) {
            return R.failed(AccountResultCode.USER_AUTH_FAIL);
        }
        account.auth(request.getRearName(), request.getIdCard());
        if (accountDomainService.updateById(account)) {
            return R.ok(AccountOperationInfo.of(AccountConverter.CONVERTER.mapToVo(account)));
        }
        return R.failed(BsResultCode.UPDATE_FAILED);
    }

    @Override
    public R<Boolean> updatePassword(AccountModifyRequest request) {
        Account account = accountDomainService.findById(request.getId());
        if (account == null) {
            return R.failed(AccountResultCode.USER_NOT_FOUND);
        }
        String password = account.getPassword();
        if (!passwordEncoder.matches(request.getOldPassword(), password)) {
            return R.failed(AccountResultCode.PASSWORD_ERROR);
        }
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return accountDomainService.updateById(account) ? R.ok() : R.failed(BsResultCode.UPDATE_FAILED);
    }

    private boolean addUsername(String username) {
        return this.usernameBloomFilter != null && this.usernameBloomFilter.add(username);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        usernameBloomFilter = redissonClient.getBloomFilter("account-auth-service:username");
        if (usernameBloomFilter != null && !usernameBloomFilter.isExists()) {
            usernameBloomFilter.tryInit(100000L, 0.01);
        }
    }
}
