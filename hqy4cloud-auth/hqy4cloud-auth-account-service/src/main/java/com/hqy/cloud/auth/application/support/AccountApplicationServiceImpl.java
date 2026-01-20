package com.hqy.cloud.auth.application.support;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.hqy.cloud.account.constants.AccountResultCode;
import com.hqy.cloud.account.constants.GrantType;
import com.hqy.cloud.account.request.AuthenticateRequest;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheManager;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.service.AccountDomainService;
import com.hqy.cloud.auth.account.service.SysOauthClientDomainService;
import com.hqy.cloud.auth.application.AccountApplicationService;
import com.hqy.cloud.auth.base.AccountConstants;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.auth.infrastructure.certification.service.AuthService;
import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.infrastructure.random.RandomCodeScene;
import com.hqy.cloud.infrastructure.random.RandomCodeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountApplicationServiceImpl implements AccountApplicationService {
    private final RedissonClient redissonClient;
    private final RandomCodeService randomCodeService;

    private final AuthService authService;
    private final AccountDomainService accountDomainService;
    private final SysOauthClientDomainService sysOauthClientDomainService;


    // 用户名布隆过滤器
    private RBloomFilter<String> usernameBloomFilter;


    @PostConstruct
    public void init() {
        usernameBloomFilter = redissonClient.getBloomFilter("account-auth-service:username");
        if (usernameBloomFilter != null && !usernameBloomFilter.isExists()) {
            usernameBloomFilter.tryInit(100000L, 0.01);
        }
    }

    @Override
    public Account register(AuthenticateRequest request) {
        if (StringUtils.isAllBlank(request.getAccessAccount(), request.getAccessSecret())) {
            throw new BizException(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        // 1. 查询用户是否存在
        Account existAccount = accountDomainService.queryAccountByUniqueIndex(request.getAccessAccount());
        if (existAccount != null) {
            return existAccount;
        }

        // 2. 验证密钥
        validRegisterSecret(request);

        // 3. 新增account
        Account account = Account.register(
                request.getClientId(),
                getUsername(request),
                request.getPassword(),
                request.getEmail(),
                request.getPhone(),
                UserRole.CUSTOMER);
        if (accountDomainService.save(account)) {
            addUsername(account.getUsername());
            return account;
        }
        throw new BizException(AccountResultCode.REGISTER_ACCOUNT_FAILED);
    }


    @Override
    @CacheInvalidate(name = AccountAuthCacheManager.ACCOUNT_USER_CACHE_KEY, key = "#request.id")
    public boolean realNameAuth(Account request) {
        if (authService.checkAuth(request.getRealName(), request.getIdCard())) {
            return accountDomainService.updateById(request);
        }
        return false;
    }

    private void validRegisterSecret(AuthenticateRequest request) {
        if (request.getGrantType() == GrantType.SMS || request.getGrantType() == GrantType.EMAIL) {
            Assert.isTrue(randomCodeService.isExist(request.getAccessSecret(), request.getAccessAccount(), request.getClientId(),
                    request.getGrantType() == GrantType.SMS ? RandomCodeScene.SMS_AUTH : RandomCodeScene.EMAIL_AUTH), () -> new BizException(AccountResultCode.VERIFY_CODE_ERROR));
        }
    }

    private String getUsername(AuthenticateRequest request) {
        GrantType grantType = request.getGrantType();
        if (grantType == GrantType.PASSWORD) {
            return request.getAccessAccount();
        }
        String username;
        do {
            String randomString = RandomUtil.randomString(6).toUpperCase();
            String tailStr = grantType == GrantType.SMS ? request.getAccessAccount().substring(7, 11) :
                    DateUtil.format(new Date(), DatePattern.PURE_TIME_PATTERN);
            username = AccountConstants.DEFAULT_NICKNAME_PREFIX + randomString + tailStr;
        } while (usernameExists(username));
        return username;
    }

    private boolean usernameExists(String username) {
        if (this.usernameBloomFilter != null && this.usernameBloomFilter.contains(username)) {
            return accountDomainService.queryAccountByUniqueIndex(username) != null;
        }
        return false;
    }

    private boolean addUsername(String username) {
        return this.usernameBloomFilter != null && this.usernameBloomFilter.add(username);
    }

}
