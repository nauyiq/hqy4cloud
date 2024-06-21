package com.hqy.cloud.auth.account.cache;

import cn.hutool.extra.spring.SpringUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.Role;
import com.hqy.cloud.common.base.lang.DateMeasureConstants;
import lombok.Getter;

/**
 * 统一的账号认证缓存管理中心, 单例模式
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
@Getter
public class AccountAuthCacheManager {

    /**
     * 账号用户信息缓存key
     */
    public static final String ACCOUNT_USER_CACHE_KEY = ":account:cache:user:";

    /**
     * 账户角色缓存key
     */
    public static final String ACCOUNT_ROLE_CACHE_KEY = ":account:cache:role";


    private AccountAuthCacheManager() {
        doInit();
    }

    private void doInit() {
        CacheManager manager = SpringUtil.getBean(CacheManager.class);
        QuickConfig userQc = QuickConfig.newBuilder(ACCOUNT_USER_CACHE_KEY)
                .cacheType(CacheType.BOTH)
                .expire(DateMeasureConstants.ONE_HOUR)
                .syncLocal(true)
                .build();

        QuickConfig roleQc = QuickConfig.newBuilder(ACCOUNT_ROLE_CACHE_KEY)
                .cacheType(CacheType.BOTH)
                .expire(DateMeasureConstants.ONE_HOUR)
                .syncLocal(true)
                .build();
        this.roleCache = manager.getOrCreateCache(roleQc);
    }

    private static volatile AccountAuthCacheManager instance;
    public static AccountAuthCacheManager getInstance() {
        if (instance == null) {
            synchronized (AccountAuthCacheManager.class) {
                if (instance == null) {
                    instance = new AccountAuthCacheManager();
                }
            }
        }
        return instance;
    }

    private Cache<Long, Account> accountCache;
    private Cache<String, Role> roleCache;

    public void put(Long id, Account account) {
        this.accountCache.put(id, account);
    }

    public void remove(Long id) {
        this.accountCache.remove(id);
    }

    public void put(String roleName, Role role) {
        this.roleCache.put(roleName, role);
    }

    public void remove(String roleName) {
        this.roleCache.remove(roleName);
    }


}
