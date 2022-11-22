package com.hqy.account.dao;

import com.hqy.account.entity.Resource;
import com.hqy.account.struct.ResourcesInRoleStruct;
import com.hqy.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 17:58
 */
@Repository
public interface ResourceDao extends BaseDao<Resource, Integer> {

    /**
     * 根据角色列表获取资源
     * @param roles 角色列表
     * @return      ResourcesInRoleStruct.
     */
    List<ResourcesInRoleStruct> getResourcesByRoles(@Param("roles") List<String> roles);
}
