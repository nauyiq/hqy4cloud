package com.hqy.auth.service;

import com.hqy.auth.common.vo.AccountRoleVO;
import com.hqy.auth.entity.Role;
import com.hqy.base.BaseTkService;
import com.hqy.base.common.result.PageResult;

import java.util.List;

/**
 * AccountRoleTkService.
 * @author qiyuan.hong
 * @date 2022-03-10 21:17
 */
public interface RoleTkService extends BaseTkService<Role, Integer> {

    /**
     * 根据角色名获取角色id
     * @param roleList 角色名列表
     * @return         角色id列表
     */
    List<Integer> selectIdByNames(List<String> roleList);

    /**
     * 根据角色名获取角色
     * @param roles 角色名列表
     * @return      AccountRole.
     */
    List<Role> queryRolesByNames(List<String> roles);

    /**
     * 获取角色列表
     * @param maxRoleLevel 角色Level
     * @param status       状态
     * @return             roles.
     */
    List<Role> getRolesList(Integer maxRoleLevel, Boolean status);


    /**
     * 分页获取角色列表
     * @param roleName      角色名
     * @param note          备注
     * @param maxRoleLevel  最大可访问等级
     * @param current       第几页
     * @param size          一共几页
     * @return              PageResult for AccountRoleVO.
     */
    PageResult<AccountRoleVO> getPageRoles(String roleName, String note, Integer maxRoleLevel, Integer current, Integer size);
}
