package com.hqy.access.auth;

import com.hqy.access.auth.support.EndpointAuthorizationManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import java.util.Collections;
import java.util.List;

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
    public boolean isPermitAuthority(String role, String resource) {
        return isPermitAuthorities(Collections.singletonList(role), resource);
    }

    @Override
    public boolean isPermitAuthorities(List<String> roles, String resource) {
        // Not found access roles.
        if (CollectionUtils.isEmpty(roles) || StringUtils.isBlank(resource)) {
            return false;
        }

        // If 'root', any resource can access.
        if (roles.stream().anyMatch(role -> role.equalsIgnoreCase(ROOT))) {
            return true;
        }

        // 获取角色对资源的配置项.
        List<ResourceConfig> accessResources = accessResourcesInRoles(roles);
        if (CollectionUtils.isEmpty(accessResources)) {
            //无任何配置项 则不可以访问.
            return false;
        } else {
            AntPathMatcher antPathMatcher = EndpointAuthorizationManager.getInstance().getAntPathMatcher();
            for (ResourceConfig resourceConfig : accessResources) {
                if (antPathMatcher.match(resourceConfig.path, resource)) {
                    return resourceConfig.status;
                }
            }
            // 当前访问资源没有配置项 表示可以访问
            return true;
        }
    }

    /**
     * 根据角色列表获取可以访问的资源列表
     * @param roles 角色列表
     * @return      资源列表.
     */
    protected abstract List<ResourceConfig> accessResourcesInRoles(List<String> roles);


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceConfig {
        private String path;
        private Boolean status;
    }

}
