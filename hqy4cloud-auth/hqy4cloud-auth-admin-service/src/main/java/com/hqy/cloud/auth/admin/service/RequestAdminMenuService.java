package com.hqy.cloud.auth.admin.service;

import com.hqy.cloud.auth.base.dto.MenuDTO;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.base.vo.AdminTreeMenuVO;
import com.hqy.cloud.common.bind.R;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/27
 */
public interface RequestAdminMenuService {

    /**
     * 获取后台菜单栏
     * @param id 用户id
     * @return R.
     */
    R<List<AdminMenuInfoVO>> getAdminMenu(Long id);

    /**
     * 新增菜单
     * @param menuDTO 菜单数据
     * @return        R
     */
    R<Boolean> addMenu(MenuDTO menuDTO);

    /**
     * 根据菜单id查找菜单
     * @param menuId  菜单id
     * @return        R.
     */
    R<AdminTreeMenuVO> getMenuById(Long menuId);

    /**
     * 修改菜单
     * @param menuDTO 菜单数据
     * @return        R
     */
    R<Boolean> editMenu(MenuDTO menuDTO);

    /**
     * 删除菜单
     * @param menuId 菜单id
     * @return       R.
     */
    R<Boolean> deleteMenu(Long menuId);


    /**
     * 获取用户角色拥有的菜单权限
     * @param userId 用户id
     * @return       R
     */
    R<List<Integer>> getMenuPermissionsIdByUserId(Long userId);


    /**
     * 获取树形结构的菜单栏
     * @param id      用户id
     * @param status  菜单栏状态
     * @return   R.
     */
    R<List<AdminTreeMenuVO>> getAdminTreeMenu(Long id, Boolean status);

}
