package com.hqy.cloud.auth.server.support;

import com.hqy.account.service.RemoteAuthService;
import com.hqy.account.struct.AuthenticationStruct;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.support.RedisHashCache;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ResourceInRoleCacheServer.
 * @see com.hqy.cloud.foundation.cache.support.RedisCacheTemplate
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 14:08
 */
public class ResourceInRoleCacheServer extends RedisHashCache<AuthenticationStruct, String> {
    private static final Logger log = LoggerFactory.getLogger(ResourceInRoleCacheServer.class);

    public ResourceInRoleCacheServer(RedissonClient redissonClient) {
        super(MicroServiceConstants.ACCOUNT_SERVICE, AuthenticationStruct.class.getSimpleName(), redissonClient);
    }

    @Override
    protected List<AuthenticationStruct> getCachesFromDb(List<String> roles) {
        RemoteAuthService remoteAuthService = RPCClient.getRemoteService(RemoteAuthService.class);
        List<AuthenticationStruct> resources = remoteAuthService.getAuthoritiesResourcesByRoles(roles);
        if (CollectionUtils.isEmpty(resources)) {
            return Collections.emptyList();
        }
        return resources.stream().map(e -> new AuthenticationStruct(e.getRole(), e.getResources())).collect(Collectors.toList());
    }

    @Override
    protected AuthenticationStruct getCacheFromDb(String role) {
        List<AuthenticationStruct> cachesFromDb = getCachesFromDb(Collections.singletonList(role));
        if (CollectionUtils.isEmpty(cachesFromDb)) {
            return new AuthenticationStruct();
        }
        return cachesFromDb.get(0);
    }

    @Override
    protected void updateDb(String role, AuthenticationStruct cache) {
        if (StringUtils.isBlank(role) || cache == null) {
            return;
        }
        RemoteAuthService remoteAuthService = RPCClient.getRemoteService(RemoteAuthService.class);
        remoteAuthService.updateAuthoritiesResource(role, cache.resources);
    }


}



