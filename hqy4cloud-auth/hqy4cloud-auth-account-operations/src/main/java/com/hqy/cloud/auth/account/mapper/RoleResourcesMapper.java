package com.hqy.cloud.auth.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.dto.RoleOnResourcesDTO;
import com.hqy.cloud.auth.account.entity.RoleResources;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RoleResourcesDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 9:56
 */
@Mapper
public interface RoleResourcesMapper extends BaseMapper<RoleResources> {

    /**
     * 新增或修改角色资源表
     * @param roleId          角色id
     * @param role            角色名
     * @param resources       角色资源.
     * @return                rows.
     */
    int insertOrUpdateRoleResources(@Param("roleId") Integer roleId, @Param("role") String role, @Param("resources") List<ResourceDTO> resources);

    /**
     * 根据角色列表获取资源
     * @param roles 角色列表
     * @return      AuthenticationDTO.
     */
    List<RoleOnResourcesDTO> getAuthoritiesResourcesByRoles(@Param("roles") List<String> roles);

    /**
     * 根据资源id获取分配的角色名
     * @param resourceId 资源id
     * @return           角色名
     */
    List<String> getRolesByResource(@Param("resourceId") Integer resourceId);

    /**
     * 根据角色id和资源id列表删除数据
     * @param roleId      角色id
     * @param resourceIds 资源表id集合
     * @return            result.
     */
    int deleteByRoleAndResourceIds(@Param("roleId") Integer roleId, @Param("resourceIds") List<Integer> resourceIds);


}
