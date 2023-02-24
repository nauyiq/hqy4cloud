package com.hqy.cloud.service;

import com.hqy.cloud.common.dto.UserDTO;
import com.hqy.cloud.entity.Account;
import com.hqy.cloud.entity.Role;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/13 16:24
 */
public interface AccountInfoOperationService {

    /**
     * 校验参数是否存在
     * @param username 用户名
     * @param email    邮箱
     * @param phone    电话
     * @return         result.
     */
    boolean checkParamExist(String username, String email, String phone);

    /**
     * 校验当前账号id是有拥有修改角色权限
     * @param id    账号id
     * @param roles 被修改的角色列表
     * @return      result.
     */
    boolean checkEnableModifyRoles(Long id, List<Role> roles);


    /**
     * 根据id获取账号的最高级别的权限角色级别
     * @param id 账号id
     * @return   最高权限角色级别
     */
    int getAccountMaxAuthorityRoleLevel(Long id);

    /**
     * 注册账号
     * @param userDTO         用户信息
     * @param roles           角色列表
     * @return                result.
     */
    boolean registryAccount(UserDTO userDTO, List<Role> roles);

    /**
     * 修改账户
     * @param userDTO  用户信息.
     * @param roles    角色列表
     * @param account  被修改的账户
     * @param oldRoles 旧角色列表
     */
    void editAccount(UserDTO userDTO, List<Role> roles, Account account,  List<Role> oldRoles);

    /**
     * 删除用户
     * @param account 用户
     */
    void deleteUser(Account account);


}
