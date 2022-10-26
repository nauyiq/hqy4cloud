package com.hqy.account.service;

import com.hqy.account.entity.Authorities;
import com.hqy.account.struct.ResourceStruct;
import com.hqy.base.BaseTkService;

import java.util.List;

/**
 * AuthoritiesService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 9:03
 */
public interface AuthoritiesTkService extends BaseTkService<Authorities, Integer> {

    /**
     * 修改权限表 角色资源.
     * @param roleId           角色表id
     * @param role             角色
     * @param resourceStructs  资源
     */
    void insertOrUpdateAuthoritiesResource(Integer roleId, String role, List<ResourceStruct> resourceStructs);
}
