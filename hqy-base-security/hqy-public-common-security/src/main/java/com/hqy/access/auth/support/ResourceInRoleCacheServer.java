package com.hqy.access.auth.support;

import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.account.struct.ResourcesInRoleStruct;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.fundation.cache.support.RedisHashCache;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ResourceInRoleCacheServer.
 * @see com.hqy.fundation.cache.support.RedisCacheTemplate
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 14:08
 */
public class ResourceInRoleCacheServer extends RedisHashCache<ResourcesInRoleStruct, String> {
    private static final Logger log = LoggerFactory.getLogger(ResourceInRoleCacheServer.class);

    public ResourceInRoleCacheServer() {
        super(MicroServiceConstants.ACCOUNT_SERVICE, ResourcesInRoleStruct.class.getSimpleName());
    }

    @Override
    protected List<ResourcesInRoleStruct> getCachesFromDb(List<String> roles) {
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        List<ResourcesInRoleStruct> resources = accountRemoteService.getAuthoritiesResourcesByRoles(roles);
        if (CollectionUtils.isEmpty(resources)) {
            return Collections.emptyList();
        }
        return resources.stream().map(e -> new ResourcesInRoleStruct(e.getRole(), e.getResources())).collect(Collectors.toList());
    }

    @Override
    protected ResourcesInRoleStruct getCacheFromDb(String role) {
        List<ResourcesInRoleStruct> cachesFromDb = getCachesFromDb(Collections.singletonList(role));
        if (CollectionUtils.isEmpty(cachesFromDb)) {
            return new ResourcesInRoleStruct();
        }
        return cachesFromDb.get(0);
    }

    @Override
    protected void updateDb(String role, ResourcesInRoleStruct cache) {
        if (StringUtils.isBlank(role) || cache == null) {
            return;
        }
        AccountRemoteService remoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        remoteService.updateAuthoritiesResource(role, cache.resources);
    }

    @Override
    public String StringConvertPkType(String pkStr) {
        return pkStr;
    }


}



