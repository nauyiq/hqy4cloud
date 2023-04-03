package com.hqy.cloud.auth.core.authentication;

import com.hqy.cloud.auth.base.dto.RoleAuthenticationDTO;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/7 14:20
 */
public interface RoleAuthenticationService {

    /**
     * 获取角色拥有的权限
     * @param roles 角色
     * @return      {@link RoleAuthenticationDTO}
     */
    List<RoleAuthenticationDTO> getAuthenticationByRoles(List<String> roles);

}
