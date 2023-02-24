package com.hqy.cloud.mapper;

import com.hqy.cloud.common.vo.AccountRoleVO;
import com.hqy.cloud.entity.Role;
import com.hqy.cloud.tk.BaseTkMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AccountRoleDao.
 * @author qiyuan.hong
 * @date 2022-03-10 21:42
 */
@Repository
public interface RoleTkMapper extends BaseTkMapper<Role, Integer> {

    /**
     * 根据角色名获取角色id
     * @param roleList 角色名列表
     * @return         角色id列表
     */
    List<Integer> selectIdByNames(@Param("roles") List<String> roleList);

    /**
     * 根据角色名获取角色
     * @param roles 角色名列表
     * @return      AccountRole.
     */
    List<Role> queryRolesByNames(@Param("roles")List<String> roles);

    /**
     * 获取角色列表
     * @param maxRoleLevel 角色Level
     * @param status       状态
     * @return             roles.
     */
    List<Role> queryRoles(@Param("maxRoleLevel") Integer maxRoleLevel, @Param("status") Boolean status);

    /**
     * 根据id列表查询角色列表
     * @param roleIds id列表
     * @return        roles
     */
    List<Role> queryByIds(@Param("ids") List<Integer> roleIds);

    /**
     * 分页获取角色vo
     * @param roleName      模糊查询-角色名
     * @param note          模糊查询-备注
     * @param maxRoleLevel  最大可访问角色等级
     * @return              List for AccountRoleVO.
     */
    List<AccountRoleVO> getPageRoleVo(@Param("roleName") String roleName, @Param("note")String note, @Param("maxRoleLevel")Integer maxRoleLevel);

}
