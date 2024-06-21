package com.hqy.cloud.auth.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hqy.cloud.auth.base.dto.PermissionDTO;
import com.hqy.cloud.auth.account.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RoleMenuDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:22
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 根据角色列表获取菜单权限
     * @param ids   角色id列表
     * @return      权限列表
     */
    List<String> getManuPermissionsByRoleIds(@Param("ids") List<Integer> ids);

    /**
     * 根据角色列表获取菜单权限
     * @param ids 角色id列表
     * @return    权限列表
     */
    List<PermissionDTO> getManuPermissionsByRoles(@Param("ids") List<Integer> ids);


}
