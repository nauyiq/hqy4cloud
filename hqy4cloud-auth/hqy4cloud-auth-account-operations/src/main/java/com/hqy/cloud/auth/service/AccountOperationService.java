package com.hqy.cloud.auth.service;

import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.Role;

import java.util.List;

/**
 * 账号db操作相关service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27
 */
public interface AccountOperationService {

    /**
     * get account information,
     * @param id account id.
     * @return    {@link AccountInfoDTO}
     */
    AccountInfoDTO getAccountInfo(Long id);

    /**
     * get account info by username or email.
     * @param usernameOrEmail username or email.
     * @return                {@link AccountInfoDTO}
     */
    AccountInfoDTO getAccountInfo(String usernameOrEmail);

    /**
     * get account information,
     * @param ids account id List.
     * @return AccountInfoDTO
     */
    List<AccountInfoDTO> getAccountInfo(List<Long> ids);

    /**
     * get account information by name
     * @param name username or nickname
     * @return     {@link AccountInfoDTO}
     */
    List<AccountInfoDTO> getAccountProfilesByName(String name);

    /**
     * 校验参数是否存在
     * @param username 用户名
     * @param email    邮箱
     * @param phone    电话
     * @return         result.
     */
    boolean checkParamExist(String username, String email, String phone);

    /**
     * 注册账号
     * @param userDTO         用户信息
     * @param roles           角色列表
     * @return                result.
     */
    boolean registryAccount(UserDTO userDTO, List<Role> roles);

    /**
     * 删除账号角色，
     * 需要注意的是：不会更新账户表冗余字段数据.
     * @param role 角色
     * @return     result.
     */
    boolean deleteAccountRole(Role role);

    /**
     * 修改账户
     * @param userDTO  用户信息.
     * @param roles    角色列表
     * @param account  被修改的账户
     * @param oldRoles 旧角色列表
     * @return         result.
     */
    boolean editAccount(UserDTO userDTO, List<Role> roles, Account account, List<Role> oldRoles);

    /**
     * 删除用户
     * @param account 用户
     * @return        result.
     */
    boolean deleteUser(Account account);





}
