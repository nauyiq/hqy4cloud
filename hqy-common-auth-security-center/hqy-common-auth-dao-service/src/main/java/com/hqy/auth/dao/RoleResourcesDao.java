package com.hqy.auth.dao;

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
    void insertOrUpdateRoleResources(@Param("roleId") Integer roleId, @Param("role") String role, @Param("resources") List<ResourceStruct> resourceStructs);
}
