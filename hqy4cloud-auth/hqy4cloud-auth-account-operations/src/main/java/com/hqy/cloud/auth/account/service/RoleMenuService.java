package com.hqy.cloud.auth.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hqy.cloud.auth.account.entity.RoleMenu;

/**
 * RoleMenuService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:25
 */
public interface RoleMenuService extends IService<RoleMenu> {

    /**
     * 删除角色菜单
     * @param id role id
     * @return   result.
     */
    boolean deleteByRoleId(Integer id);
}
