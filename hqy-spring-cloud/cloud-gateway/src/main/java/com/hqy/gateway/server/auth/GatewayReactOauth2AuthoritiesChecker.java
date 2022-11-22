package com.hqy.gateway.server.auth;

import com.hqy.access.auth.RbacAuthoritiesChecker;
import com.hqy.access.auth.support.ResourceInRoleCacheServer;
import com.hqy.account.struct.ResourceStruct;
import com.hqy.account.struct.ResourcesInRoleStruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GatewayReactOauth2AuthoritiesChecker.
 * @see com.hqy.access.auth.RbacAuthoritiesChecker
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 14:00
 */
@Component
@RequiredArgsConstructor
public class GatewayReactOauth2AuthoritiesChecker extends RbacAuthoritiesChecker {

    private final ResourceInRoleCacheServer resourceInRoleCacheServer;

    @Override
    protected List<ResourceConfig> accessResourcesInRoles(List<String> roles) {
        List<ResourcesInRoleStruct> caches = resourceInRoleCacheServer.getCaches(roles);
        if (CollectionUtils.isEmpty(caches)) {
            return Collections.emptyList();
        }
        List<ResourceConfig> allResources = new ArrayList<>();
        for (ResourcesInRoleStruct struct : caches) {
            List<ResourceStruct> resources = struct.getResources();
            if (CollectionUtils.isNotEmpty(resources)) {
                allResources.addAll(resources.stream().map(e -> new ResourceConfig(e.path, e.status)).collect(Collectors.toList()));
            }
        }
        return allResources;
    }
}
