package com.hqy.cloud.admin.service;

import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.auth.base.dto.MenuDTO;

/**
 * AdminMenuRequestController.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:25
 */
public interface RequestAdminMenuService {

    /**
     * 获取后台菜单栏
     * @param id 用户id
     * @return response.
     */
    DataResponse getAdminMenu(Long id);

    /**
     * 新增菜单
     * @param menuDTO 菜单数据
     * @return        response
     */
    DataResponse addMenu(MenuDTO menuDTO);

    /**
     * 根据菜单id查找菜单
     * @param menuId  菜单id
     * @return        response.
     */
    DataResponse getMenuById(Long menuId);

    /**
     * 修改菜单
     * @param menuDTO 菜单数据
     * @return        response
     */
    DataResponse editMenu(MenuDTO menuDTO);

    /**
     * 删除菜单
     * @param menuId 菜单id
     * @return       response.
     */
    DataResponse deleteMenu(Long menuId);


    /**
     * 获取某个角色拥有的菜单权限
     * @param roleId 角色id
     * @return       response
     */
    DataResponse getMenuPermissionsIdByRoleId(Integer roleId);


    /**
     * 获取树形结构的菜单栏
     * @param id      用户id
     * @param status  菜单栏状态
     * @return   response.
     */
    DataResponse getAdminTreeMenu(Long id, Boolean status);



}
