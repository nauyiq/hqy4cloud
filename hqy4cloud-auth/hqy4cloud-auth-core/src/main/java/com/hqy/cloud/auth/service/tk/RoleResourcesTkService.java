package com.hqy.cloud.auth.service.tk;

import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.entity.RoleResources;
import com.hqy.cloud.db.tk.PrimaryLessTkService;

import java.util.List;
import java.util.Map;

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
     * @param resources       角色资源.
     * @return                result.
     */
    boolean insertOrUpdateRoleResources(Integer roleId, String role, List<ResourceDTO> resources);

    /**
     * 根据角色列表获取资源
     * @param roles 角色列表
     * @return      AuthenticationDTO.
     */
    Map<String, List<ResourceDTO>> getAuthoritiesResourcesByRoles(List<String> roles);

    /**
     * 根据角色id和资源id列表删除数据
     * @param roleId      角色id
     * @param resourceIds 资源表id集合
     * @return            result.
     */
    boolean deleteByRoleAndResourceIds(Integer roleId, List<Integer> resourceIds);

    /**
     * 根据资源id和角色id列表删除数据
     * @param resourceId  资源id
     * @param roleIds     角色id集合
     * @return            result.
     */
    boolean deleteByResourceIdAndRoleIds(Integer resourceId, List<Integer> roleIds);

}
