package com.hqy.cloud.auth.api;

import com.hqy.cloud.auth.common.AuthenticationModuleInfo;

import java.util.List;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/20
 */
public interface AuthoritiesRoleService {

    /**
     * 获取权限模块配置.
     * @param authorities 权限、角色
     * @return            权限模块信息.
     */
    List<AuthenticationModuleInfo> loadAuthenticationModulesByAuthorities(List<String> authorities);

    /**
     * 全区权限标识配置
     * @param authorities 权限、角色
     * @return            权限列表 permissions
     */
    Set<String> loadAuthenticationPermissionsByAuthorities(List<String> authorities);

}
