package com.hqy.cloud.auth.account.service.impl;

import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hqy.cloud.auth.account.entity.OauthClient;
import com.hqy.cloud.auth.account.mapper.SysOauthClientMapper;
import com.hqy.cloud.auth.account.service.SysOauthClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:53
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOauthClientServiceImpl extends ServiceImpl<SysOauthClientMapper, OauthClient> implements SysOauthClientService {
    private final SysOauthClientMapper sysOauthClientMapper;

    @Override
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.MINUTES)
    @Cached(name = ":account:cache:oauth_client:", expire = 3000,  cacheType = CacheType.REMOTE, key = "#clientId", cacheNullValue = true)
    public OauthClient findByClientId(String clientId) {
        return sysOauthClientMapper.selectById(clientId);
    }
}
