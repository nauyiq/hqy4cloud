package com.hqy.auth.service;

import com.hqy.auth.entity.Role;
import com.hqy.base.BaseTkService;

import java.util.List;

/**
 * AccountRoleTkService.
 * @author qiyuan.hong
 * @date 2022-03-10 21:17
 */
public interface RoleTkService extends BaseTkService<Role, Integer> {

    /**
     * 根据角色名获取角色id
     * @param roleList 角色名列表
     * @return         角色id列表
     */
    List<Integer> selectIdByNames(List<String> roleList);

    /**
     * 根据角色名获取角色
     * @param roles 角色名列表
     * @return      AccountRole.
     */
    List<Role> queryRolesByNames(List<String> roles);

    /**
     * 获取角色列表
     * @param maxRoleLevel 角色Level
     * @param status       状态
     * @return             roles.
     */
    List<Role> getRolesList(Integer maxRoleLevel, Boolean status);

    /**
     * 根据ids查询角色列表
     * @param roleIds roles 角色id集合
     * @return              角色列表.
     */
    List<Role> queryByIds(List<Integer> roleIds);
}
