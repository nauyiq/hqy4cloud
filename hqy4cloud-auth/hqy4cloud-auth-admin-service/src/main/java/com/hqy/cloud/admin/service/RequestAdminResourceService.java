package com.hqy.cloud.admin.service;

import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.dto.RoleResourcesDTO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/20 13:37
 */
public interface RequestAdminResourceService {

    /**
     * 分页获取资源列表
     * @param name    资源名
     * @param current 当前页
     * @param size    第几页
     * @return        R.
     */
    R<PageResult<ResourceDTO>> getPageResources(String name, Integer current, Integer size);

    /**
     * 获取资源配置的角色列表
     * @param resourceId 资源id
     * @return           R
     */
    R<List<Integer>> getResourceTree(Integer resourceId);

    /**
     * 添加资源
     * @param resourceDTO 资源dto
     * @return            R.
     */
    R<Boolean> addResource(ResourceDTO resourceDTO);

    /**
     * 修改资源
     * @param resourceDTO 资源DTO
     * @return            R.
     */
    R<Boolean> editResource(ResourceDTO resourceDTO);

    /**
     * 删除资源
     * @param resourceId 资源id
     * @return           R
     */
    R<Boolean> delResource(Integer resourceId);

    /**
     * 修改资源可以被哪些角色访问
     * @param roleResourcesDTO {@link RoleResourcesDTO}
     * @return                 R
     */
    R<Boolean> editRoleResources(RoleResourcesDTO roleResourcesDTO);


}
