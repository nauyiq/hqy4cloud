package com.hqy.auth.dao;

import com.hqy.account.struct.AuthenticationStruct;
import com.hqy.auth.entity.RoleResources;
import com.hqy.account.struct.ResourceStruct;
import com.hqy.base.PrimaryLessTkDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RoleResourcesDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 9:56
 */
@Repository
public interface RoleResourcesDao extends PrimaryLessTkDao<RoleResources> {

    /**
     * 新增或修改角色资源表
     * @param roleId          角色id
     * @param role            角色名
     * @param resourceStructs 角色资源.
     */
    int insertOrUpdateRoleResources(@Param("roleId") Integer roleId, @Param("role") String role, @Param("resources") List<ResourceStruct> resourceStructs);

    /**
     * 根据角色列表获取资源
     * @param roles 角色列表
     * @return      ResourcesInRoleStruct.
     */
    List<AuthenticationStruct> getAuthoritiesResourcesByRoles(@Param("roles") List<String> roles);

    /**
     * 根据角色id和资源id列表删除数据
     * @param roleId      角色id
     * @param resourceIds 资源表id集合
     * @return            result.
     */
    int deleteByRoleAndResourceIds(@Param("roleId") Integer roleId, @Param("resourceIds") List<Integer> resourceIds);
}
