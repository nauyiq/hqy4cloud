package com.hqy.cloud.admin.service;

import com.hqy.cloud.auth.base.vo.AccountRoleVO;
import com.hqy.cloud.auth.account.entity.Role;
import com.hqy.cloud.auth.base.dto.RoleDTO;
import com.hqy.cloud.auth.base.dto.RoleMenuDTO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/14 15:29
 */
public interface RequestAdminRoleService {

    /**
     * 分页获取角色列表
     * @param roleName 角色名
     * @param note     角色备注
     * @param id       请求账号id
     * @param current  第几页
     * @param size     一页几行
     * @return         R.
     */
    R<PageResult<AccountRoleVO>> getPageRoles(String roleName, String note, Long id, Integer current, Integer size);

    /**
     * 获取角色列表
     * @param id 角色id
     * @return   R.
     */
    R<List<Role>> getRoles(Long id);

    /**
     * 检查当前账号需要设置的角色level是否合法
     * @param id    账号id
     * @param level 角色level
     * @return      R.
     */
    R<Boolean> checkLevel(Long id, Integer level);

    /**
     * 校验角色名是否存在
     * @param roleName 角色名
     * @return         R.
     */
    R<Boolean> checkRoleNameExist(String roleName);

    /**
     * 新增角色
     * @param id    角色id
     * @param role  {@link RoleDTO}
     * @return      R.
     */
    R<Boolean> addRole(Long id, RoleDTO role);

    /**
     * 修改角色
     * @param id   用户id
     * @param role 角色
     * @return     R.
     */
    R<Boolean> editRole(Long id, RoleDTO role);

    /**
     * 删除角色
     * @param roleId 角色id
     * @return       response.
     */
    R<Boolean> deleteRole(Integer roleId);

    /**
     * 更新角色菜单权限
     * @param roleMenus 角色菜单。
     * @return          R.
     */
    R<Boolean> updateRoleMenus(RoleMenuDTO roleMenus);


}
