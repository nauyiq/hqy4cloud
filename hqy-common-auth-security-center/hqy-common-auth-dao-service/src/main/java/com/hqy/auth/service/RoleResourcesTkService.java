package com.hqy.auth.service;

import com.hqy.account.struct.ResourcesInRoleStruct;
import com.hqy.auth.entity.RoleResources;
import com.hqy.account.struct.ResourceStruct;
import com.hqy.base.PrimaryLessTkService;

import java.util.List;

/**
 * RoleResourcesService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 10:01
 */
public interface RoleResourcesTkService extends PrimaryLessTkService<RoleResources> {

    /**
     * 新增或修改角色资源表
     * @param roleId          角色id
     * @param role            角色名
     * @param resourceStructs 角色资源.
     */
    void insertOrUpdateRoleResources(Integer roleId, String role, List<ResourceStruct> resourceStructs);

    /**
     * 根据角色列表获取资源
     * @param roles 角色列表
     * @return      ResourcesInRoleStruct.
     */
    List<ResourcesInRoleStruct> getResourcesByRoles(List<String> roles);

}
