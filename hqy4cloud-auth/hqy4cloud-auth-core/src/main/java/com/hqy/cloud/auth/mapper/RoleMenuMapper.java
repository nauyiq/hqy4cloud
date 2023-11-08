package com.hqy.cloud.auth.mapper;

import com.hqy.cloud.auth.base.dto.PermissionDTO;
import com.hqy.cloud.auth.entity.RoleMenu;
import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * RoleMenuDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:22
 */
@Repository
public interface RoleMenuMapper extends PrimaryLessTkMapper<RoleMenu> {

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
