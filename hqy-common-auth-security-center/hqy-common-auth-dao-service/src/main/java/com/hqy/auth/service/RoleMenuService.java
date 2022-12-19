package com.hqy.auth.service;

import com.hqy.auth.entity.RoleMenu;
import com.hqy.base.PrimaryLessTkService;

/**
 * RoleMenuService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:25
 */
public interface RoleMenuService extends PrimaryLessTkService<RoleMenu> {

    /**
     * 删除角色菜单
     * @param id role id
     * @return   result.
     */
    boolean deleteByRoleId(Integer id);
}
