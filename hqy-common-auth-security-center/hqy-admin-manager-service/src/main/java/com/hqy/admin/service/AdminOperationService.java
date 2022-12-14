package com.hqy.admin.service;

import com.hqy.auth.common.vo.menu.AdminMenuInfoVO;
import com.hqy.auth.service.MenuTkService;

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
     * menuTkService.
     * @return MenuTkService.
     */
    MenuTkService menuTkService();


}
