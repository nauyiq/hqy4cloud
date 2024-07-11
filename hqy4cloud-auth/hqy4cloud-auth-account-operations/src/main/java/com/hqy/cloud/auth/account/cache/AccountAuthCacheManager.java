package com.hqy.cloud.auth.account.cache;

import cn.hutool.extra.spring.SpringUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.hqy.cloud.auth.account.entity.SysOauthClient;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.common.base.lang.DateMeasureConstants;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 统一的账号认证缓存管理中心, 单例模式
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
@Getter
@Component
@RequiredArgsConstructor
public class AccountAuthCacheManager {

    private final CacheManager cacheManager;

    /**
     * 账号用户信息缓存key
     */
    public static final String ACCOUNT_USER_CACHE_KEY = ":account:cache:user:";

    /**
     * oauth2租户缓存key
     */
    public static final String OAUTH2_CLIENT_CACHE_KEY = ":account:cache:oauth_client:";


    @PostConstruct
    public void doInit() {
        QuickConfig userQc = QuickConfig.newBuilder(ACCOUNT_USER_CACHE_KEY)
                .cacheType(CacheType.BOTH)
                .expire(DateMeasureConstants.ONE_HOUR)
                .syncLocal(true)
                .build();
        this.accountCache = cacheManager.getOrCreateCache(userQc);

        QuickConfig clientQc = QuickConfig.newBuilder(OAUTH2_CLIENT_CACHE_KEY)
                .cacheType(CacheType.REMOTE)
                .expire(DateMeasureConstants.ONE_HOUR)
                .syncLocal(true)
                .build();
        this.oauthClientCache = cacheManager.getOrCreateCache(clientQc);
    }


    public static AccountAuthCacheManager getInstance() {
        return SpringUtil.getBean(AccountAuthCacheManager.class);
    }

    private Cache<Long, AccountInfoDTO> accountCache;
    private Cache<String, SysOauthClient> oauthClientCache;

    public void put(Long id, AccountInfoDTO account) {
        this.accountCache.put(id, account);
    }

    public void remove(Long id) {
        this.accountCache.remove(id);
    }


}
