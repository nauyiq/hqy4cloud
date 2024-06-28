package com.hqy.cloud.auth.service;

import com.hqy.cloud.auth.account.entity.AccountMenu;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.base.vo.AdminTreeMenuVO;

import java.util.List;
import java.util.Map;

/**
 * 授权数据相关service
 * 基于RBAC逻辑
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10
 */
public interface AuthOperationService {

    /**
     * 检查当前认证用户上下文是否存在permission
     * @param accountId 账号id
     * @return          result.
     */
    List<AccountMenu> getAccountMenus(Long accountId);

    /**
     * 获取菜单信息
     * @param id 账号id
     * @return   {@link AdminMenuInfoVO}
     */
    List<AdminMenuInfoVO> getAdminMenuInfo(Long id);

    /**
     * 获取树形菜单
     * @param accountId  账号id
     * @param status     菜单栏状态
     * @return          {@link AdminTreeMenuVO}
     */
    List<AdminTreeMenuVO> getAdminTreeMenu(Long accountId, Boolean status);

    /**
     * 根据角色列表获取资源
     * @param roles 资源
     * @return      AuthenticationDTO.
     */
    Map<String, List<ResourceDTO>> getAuthoritiesResourcesByRoles(List<String> roles);

    /**
     * 根据角色列表获取菜单权限列表
     * @param roles 角色列表
     * @return      PermissionDTO.
     */
    Map<String, List<String>> getPermissionsByRoles(List<String> roles);





}
