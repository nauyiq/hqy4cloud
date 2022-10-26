package com.hqy.access.auth;

import java.util.List;

/**
 * RolesAuthoritiesChecker.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 11:02
 */
public interface RolesAuthoritiesChecker {

    /**
     * 检查当前角色是否有权限访问该资源
     * @param role     角色
     * @param resource 资源
     * @return         result.
     */
    boolean isPermitAuthority(String role, String resource);

    /**
     * 判断当前角色列表是否有资格访问该资源
     * @param roles     角色列表
     * @param resource  资源
     * @return          result.
     */
    boolean isPermitAuthorities(List<String> roles, String resource);




}
