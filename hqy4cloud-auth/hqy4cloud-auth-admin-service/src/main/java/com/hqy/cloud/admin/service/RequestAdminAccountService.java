package com.hqy.cloud.admin.service;

import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.auth.base.vo.AccountRoleVO;
import com.hqy.cloud.auth.base.vo.AdminUserInfoVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;

import java.util.List;

/**
 * AdminAccountRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 15:27
 */
public interface RequestAdminAccountService {

    /**
     * 获取用户信息
     * @param id  用户id
     * @return    R.
     */
    R<AdminUserInfoVO> getLoginUserInfo(Long id);

    /**
     * 分页获取用户列表
     * @param username      用户名模糊查询
     * @param nickname      昵称模糊查询
     * @param id            登录用户id
     * @param current       当前页
     * @param size          每页多少行
     * @return              R.
     */
    R<PageResult<AccountInfoVO>> getPageUsers(String username, String nickname, Long id, Integer current, Integer size);

    /**
     * 获取当前用户可以查看的角色列表
     * @param id 账号id
     * @return   R
     */
    R<List<AccountRoleVO>> getUserRoles(Long id);

    /**
     * 检查参数是否存在
     * @param username 用户名
     * @param email    邮箱
     * @param phone    电话
     * @return         R。
     */
    R<Boolean> checkParamExist(String username, String email, String phone);

    /**
     * 新增用户
     * @param accessAccountId 请求用户id
     * @param userDTO         用户数据
     * @return                R
     */
    R<Boolean> addUser(Long accessAccountId, UserDTO userDTO);


    /**
     * 修改用户
     * @param accessAccountId 请求用户id
     * @param userDTO         用户数据
     * @return                R
     */
    R<Boolean> editUser(Long accessAccountId, UserDTO userDTO);

    /**
     * 删除用户
     * @param accessId 修改者id
     * @param id       被删除用户id
     * @return         R
     */
    R<Boolean> deleteUser(Long accessId, Long id);
}
