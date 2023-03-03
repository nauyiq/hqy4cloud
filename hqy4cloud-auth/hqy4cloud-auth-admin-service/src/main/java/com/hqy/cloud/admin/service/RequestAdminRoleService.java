package com.hqy.cloud.admin.service;

import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.auth.base.dto.RoleDTO;
import com.hqy.cloud.auth.base.dto.RoleMenuDTO;

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
     * @return         response.
     */
    DataResponse getPageRoles(String roleName, String note, Long id, Integer current, Integer size);

    /**
     * 获取角色列表
     * @param id 角色id
     * @return   response.
     */
    DataResponse getRoles(Long id);

    /**
     * 检查当前账号需要设置的角色level是否合法
     * @param id    账号id
     * @param level 角色level
     * @return      response.
     */
    DataResponse checkLevel(Long id, Integer level);

    /**
     * 校验角色名是否存在
     * @param roleName 角色名
     * @return         response.
     */
    DataResponse checkRoleNameExist(String roleName);

    /**
     * 新增角色
     * @param id    角色id
     * @param role  {@link RoleDTO}
     * @return      response.
     */
    DataResponse addRole(Long id, RoleDTO role);

    /**
     * 修改角色
     * @param id   用户id
     * @param role 角色
     * @return     response.
     */
    DataResponse editRole(Long id, RoleDTO role);

    /**
     * 删除角色
     * @param roleId 角色id
     * @return       response.
     */
    DataResponse deleteRole(Integer roleId);

    /**
     * 更新角色菜单权限
     * @param roleMenus 角色菜单。
     * @return          response.
     */
    DataResponse updateRoleMenus(RoleMenuDTO roleMenus);


}
