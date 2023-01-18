package com.hqy.admin.service.request;

import com.hqy.auth.common.dto.ResourceDTO;
import com.hqy.auth.common.dto.RoleResourcesDTO;
import com.hqy.base.common.bind.DataResponse;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/20 13:37
 */
public interface AdminResourceRequestService {

    /**
     * 分页获取资源列表
     * @param name    资源名
     * @param current 当前页
     * @param size    第几页
     * @return        response.
     */
    DataResponse getPageResources(String name, Integer current, Integer size);

    /**
     * 获取资源配置的角色列表
     * @param resourceId 资源id
     * @return           response
     */
    DataResponse getResourceTree(Integer resourceId);

    /**
     * 添加资源
     * @param resourceDTO 资源dto
     * @return            response.
     */
    DataResponse addResource(ResourceDTO resourceDTO);

    /**
     * 修改资源
     * @param resourceDTO 资源DTO
     * @return            response.
     */
    DataResponse editResource(ResourceDTO resourceDTO);

    /**
     * 修改资源可以被哪些角色访问
     * @param roleResourcesDTO {@link RoleResourcesDTO}
     * @return                 response
     */
    DataResponse editRoleResources(RoleResourcesDTO roleResourcesDTO);

    /**
     * 删除资源
     * @param resourceId 资源id
     * @return           response
     */
    DataResponse delResource(Integer resourceId);
}
