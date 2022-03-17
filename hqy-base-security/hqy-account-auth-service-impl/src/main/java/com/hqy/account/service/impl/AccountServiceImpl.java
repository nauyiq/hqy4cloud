package com.hqy.account.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.account.entity.Account;
import com.hqy.account.dao.AccountDao;
import com.hqy.account.service.AccountService;
import com.hqy.auth.dto.UserInfoDTO;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
public class AccountServiceImpl extends BaseTkServiceImpl<Account, Long> implements AccountService {

    private static final Cache<String, UserInfoDTO>  USER_CACHE =
            CacheBuilder.newBuilder().initialCapacity(1024).expireAfterAccess(10, TimeUnit.MINUTES).build();

    @Resource
    private AccountDao accountDao;

    @Override
    public BaseDao<Account, Long> selectDao() {
        return accountDao;
    }

    @Override
    public UserInfoDTO queryUserInfo(String usernameOrEmail) {
        UserInfoDTO userInfo = USER_CACHE.getIfPresent(usernameOrEmail);
        if (Objects.isNull(userInfo)) {
            //查库
            userInfo = accountDao.queryUserInfo(usernameOrEmail);
            if (Objects.nonNull(userInfo)) {
                USER_CACHE.put(usernameOrEmail, userInfo);
            }
        }
        return userInfo;
    }
}
