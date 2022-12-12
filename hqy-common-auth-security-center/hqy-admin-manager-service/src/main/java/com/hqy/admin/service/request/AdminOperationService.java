package com.hqy.admin.service.request;

import java.util.List;

/**
 * AdminOperationService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:13
 */
public interface AdminOperationService {

    /**
     * 根据角色列表获取菜单权限
     * @param roles 角色列表
     * @return      权限列表
     */
    List<String> getManuPermissionsByRoles(List<String> roles);
}
