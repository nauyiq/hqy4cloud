package com.hqy.cloud.auth.service;

import com.hqy.cloud.auth.account.service.MenuService;
import com.hqy.cloud.auth.account.service.ResourceService;
import com.hqy.cloud.auth.account.service.RoleMenuService;
import com.hqy.cloud.auth.account.service.RoleResourcesService;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.dto.RoleMenuDTO;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.base.vo.AdminTreeMenuVO;
import com.hqy.cloud.auth.account.entity.Role;

import java.util.List;
import java.util.Map;

/**
 * 授权数据相关service
 * 基于RBAC逻辑
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:13
 */
public interface AuthOperationService {

    /**
     * 校验当前账号id是有拥有修改角色权限
     * @param id    账号id
     * @param roles 被修改的角色列表
     * @return      result.
     */
    boolean checkEnableModifyRoles(Long id, List<Role> roles);

    /**
     * 根据id获取账号的最高级别的权限角色级别
     * @param id 账号id
     * @return   最高权限角色级别
     */
    int getAccountMaxAuthorityRoleLevel(Long id);


    /**
     * 根据角色列表获取菜单权限
     * @param roles 角色列表
     * @return      权限列表
     */
    List<String> getMenuPermissionsByRoles(List<String> roles);


    /**
     * 获取菜单信息
     * @param roles 角色列表
     * @return      {@link AdminMenuInfoVO}
     */
    List<AdminMenuInfoVO> getAdminMenuInfo(List<String> roles);

    /**
     * 获取树形菜单
     * @param roles  角色列表
     * @param status 菜单栏状态
     * @return      {@link AdminTreeMenuVO}
     */
    List<AdminTreeMenuVO> getAdminTreeMenu(List<String> roles, Boolean status);

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

    /**
     * 更新角色拥有的菜单权限
     * @param role      角色
     * @param roleMenus 拥有的菜单
     * @return          result.
     */
    boolean updateRoleMenus(Role role, RoleMenuDTO roleMenus);

    /**
     * menuTkService.
     * @return MenuTkService.
     */
    MenuService menuTkService();


    /**
     * resourceTkService
     * @return ResourceTkService.
     */
    ResourceService resourceTkService();

    /**
     * roleResourcesTkService.
     * @return RoleResourcesTkService
     */
    RoleResourcesService roleResourcesTkService();



}
