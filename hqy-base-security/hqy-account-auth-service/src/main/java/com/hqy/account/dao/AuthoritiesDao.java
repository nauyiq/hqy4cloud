package com.hqy.account.dao;

import com.hqy.account.entity.Authorities;
import com.hqy.account.struct.ResourceStruct;
import com.hqy.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 9:01
 */
public interface AuthoritiesDao extends BaseDao<Authorities, Integer> {

    /**
     * 新增或修改权限表 角色资源.
     * @param roleId           角色id
     * @param role             角色
     * @param resourceStructs  资源 struct.
     * @param date             时间
     */
    void insertOrUpdateAuthoritiesResource(@Param("roleId") Integer roleId, @Param("role") String role, @Param("resources") List<ResourceStruct> resourceStructs, @Param("date")Date date);
}
