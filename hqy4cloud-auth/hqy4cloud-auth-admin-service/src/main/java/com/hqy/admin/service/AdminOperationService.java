package com.hqy.admin.service;

import com.hqy.cloud.common.dto.RoleMenuDTO;
import com.hqy.cloud.common.vo.menu.AdminMenuInfoVO;
import com.hqy.cloud.common.vo.menu.AdminTreeMenuVo;
import com.hqy.cloud.entity.Role;
import com.hqy.cloud.service.MenuTkService;
import com.hqy.cloud.service.RoleMenuService;

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
     * @return      {@link AdminTreeMenuVo}
     */
    List<AdminTreeMenuVo> getAdminTreeMenu(List<String> roles, Boolean status);

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
    MenuTkService menuTkService();


    /**
     * roleMenuService
     * @return RoleMenuService.
     */
    RoleMenuService roleMenuService();



}
