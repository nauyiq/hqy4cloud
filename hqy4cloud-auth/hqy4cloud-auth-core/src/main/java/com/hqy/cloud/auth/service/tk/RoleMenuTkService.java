package com.hqy.cloud.auth.service.tk;

import com.hqy.cloud.auth.entity.RoleMenu;
import com.hqy.cloud.tk.PrimaryLessTkService;

/**
 * RoleMenuService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:25
 */
public interface RoleMenuTkService extends PrimaryLessTkService<RoleMenu> {

    /**
     * 删除角色菜单
     * @param id role id
     * @return   result.
     */
    boolean deleteByRoleId(Integer id);
}
