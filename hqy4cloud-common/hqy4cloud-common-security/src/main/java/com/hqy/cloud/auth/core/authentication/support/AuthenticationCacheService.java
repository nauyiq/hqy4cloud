package com.hqy.cloud.auth.core.authentication.support;

import com.hqy.account.service.RemoteAuthService;
import com.hqy.account.struct.AuthenticationStruct;
import com.hqy.account.struct.ResourceStruct;
import com.hqy.cloud.auth.base.dto.ResourceConfigDTO;
import com.hqy.cloud.auth.base.dto.RoleAuthenticationDTO;
import com.hqy.cloud.auth.core.authentication.RoleAuthenticationService;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import com.hqy.cloud.foundation.cache.support.RedisCacheDataService;
import com.hqy.cloud.foundation.cache.support.RedisHashCacheDataService;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ResourceInRoleCacheServer.
 * @see RedisCacheDataService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 14:08
 */
@Slf4j
public class AuthenticationCacheService extends RedisHashCacheDataService<AuthenticationStruct, String> implements RoleAuthenticationService {

    public AuthenticationCacheService(RedissonClient redissonClient) {
        super(new RedisNamedKey("hqy4cloud-common-auth", AuthenticationStruct.class.getSimpleName()), redissonClient);
    }

    @Override
    protected List<AuthenticationStruct> getDataBySource(List<String> roles) {
        RemoteAuthService remoteAuthService = RPCClient.getRemoteService(RemoteAuthService.class);
        List<AuthenticationStruct> resources = remoteAuthService.getAuthoritiesResourcesByRoles(roles);
        if (CollectionUtils.isEmpty(resources)) {
            return Collections.emptyList();
        }
        return resources;
    }

    @Override
    protected boolean updateData(String role, AuthenticationStruct cache) {
        if (StringUtils.isBlank(role) || Objects.isNull(cache)) {
            return false;
        }
        RemoteAuthService remoteAuthService = RPCClient.getRemoteService(RemoteAuthService.class);
        remoteAuthService.updateAuthoritiesResource(role, cache.resources);
        return true;
    }


    @Override
    public List<RoleAuthenticationDTO> getAuthenticationByRoles(List<String> roles) {
        List<AuthenticationStruct> structs = this.getData(roles);
        if (CollectionUtils.isEmpty(structs)) {
            return Collections.emptyList();
        }
        return structs.stream().map(this::convert).collect(Collectors.toList());
    }

    private RoleAuthenticationDTO convert(AuthenticationStruct struct) {
        List<ResourceConfigDTO> resourceConfigs;
        List<ResourceStruct> resources = struct.getResources();
        if (CollectionUtils.isEmpty(resources)) {
            resourceConfigs = Collections.emptyList();
        } else {
            resourceConfigs = resources.stream().map(ResourceConfigDTO::new).collect(Collectors.toList());
        }
        return new RoleAuthenticationDTO(struct.role, resourceConfigs, struct.permissions);
    }

}



