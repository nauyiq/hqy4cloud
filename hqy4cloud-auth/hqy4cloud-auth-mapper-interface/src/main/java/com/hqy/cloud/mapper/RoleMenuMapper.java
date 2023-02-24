package com.hqy.cloud.mapper;

import com.hqy.cloud.entity.RoleMenu;
import com.hqy.cloud.tk.PrimaryLessTkMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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


}
