package com.hqy.access.auth;

import com.hqy.access.auth.dto.ResourceConfig;
import com.hqy.access.auth.dto.RoleAuthenticationDTO;
import com.hqy.access.auth.support.EndpointAuthorizationManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * RbacAuthoritiesChecker.
 * 基于rbac模型进行账户权限模型.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 11:23
 */
public abstract class RbacAuthoritiesChecker implements RolesAuthoritiesChecker {
    private static final Logger log = LoggerFactory.getLogger(RbacAuthoritiesChecker.class);

    /**
     * root用户
     * 表示所有资源都可以访问. 无需做任何校验.
     */
    private final String ROOT = "root";


    @Override
    public boolean isPermitAuthority(String role, AuthenticationRequest request) {
        return isPermitAuthorities(Collections.singletonList(role), request);
    }

    @Override
    public boolean isPermitAuthorities(List<String> roles, AuthenticationRequest request) {
        // If 'root', any request can access.
        if (roles.stream().anyMatch(role -> role.equalsIgnoreCase(ROOT))) {
            return true;
        }

        // Not found access roles.
        if (CollectionUtils.isEmpty(roles) || Objects.isNull(request)) {
            return false;
        }

        // 获取角色对资源的配置项.
        List<RoleAuthenticationDTO> accessResources = accessResourcesInRoles(roles);
        if (CollectionUtils.isNotEmpty(accessResources)) {
            for (RoleAuthenticationDTO accessResource : accessResources) {
               if (checkResourceMatch(accessResource.getResourceConfigs(), request)) {
                   return true;
                }
            }
        }
        return false;
    }

    private boolean checkResourceMatch(List<ResourceConfig> resourceConfigs, AuthenticationRequest request) {
        if (CollectionUtils.isEmpty(resourceConfigs)) {
            return false;
        }
        AntPathMatcher antPathMatcher = EndpointAuthorizationManager.getInstance().getAntPathMatcher();
        String method = request.method();
        String uri = request.requestUri();
        return resourceConfigs.stream().anyMatch(resourceConfig ->  {
            String resourceConfigMethod = resourceConfig.getMethod();
            if (StringUtils.isBlank(resourceConfigMethod)) {
                return antPathMatcher.match(resourceConfig.getPath(), uri);
            } else {
                return method.equals(resourceConfigMethod) && antPathMatcher.match(resourceConfig.getPath(), uri);
            }
        });
    }


    /**
     * 根据角色列表获取可以访问的资源列表
     * @param roles 角色列表
     * @return      资源列表.
     */
    protected abstract List<RoleAuthenticationDTO> accessResourcesInRoles(List<String> roles);

}
