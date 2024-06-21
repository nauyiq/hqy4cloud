package com.hqy.cloud.auth.service.security.support;

import com.hqy.cloud.auth.account.entity.SysOauthClient;
import com.hqy.cloud.auth.account.mapper.SysOauthClientMapper;
import com.hqy.cloud.auth.account.service.SysOauthClientService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import com.hqy.cloud.db.tk.support.PrimaryLessTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.hqy.cloud.foundation.cache.CacheConstants.DEFAULT_KEY_GENERATOR_NAME;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:53
 */
@Slf4j
@Service
@CacheConfig(cacheNames = MicroServiceConstants.ACCOUNT_SERVICE)
@RequiredArgsConstructor
public class SysOauthClientServiceImpl extends PrimaryLessTkServiceImpl<SysOauthClient> implements SysOauthClientService {
    private final SysOauthClientMapper sysOauthClientMapper;

    @Override
    public PrimaryLessTkMapper<SysOauthClient> getTkDao() {
        return sysOauthClientMapper;
    }

    @Override
    @Cacheable(keyGenerator = DEFAULT_KEY_GENERATOR_NAME)
    public SysOauthClient queryById(Object id) {
        return super.queryById(id);
    }
}
