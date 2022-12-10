package com.hqy.auth.service;

import com.hqy.auth.entity.Resource;
import com.hqy.account.struct.ResourcesInRoleStruct;
import com.hqy.base.BaseTkService;

import java.util.List;

/**
 * ResourceTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 17:59
 */
public interface ResourceTkService extends BaseTkService<Resource, Integer> {

    /**
     * 根据角色列表获取资源
     * @param roles 角色列表
     * @return      ResourcesInRoleStruct.
     */
    List<ResourcesInRoleStruct> getResourcesByRoles(List<String> roles);
}
