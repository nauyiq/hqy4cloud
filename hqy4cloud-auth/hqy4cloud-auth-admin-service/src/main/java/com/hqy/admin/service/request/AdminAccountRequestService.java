package com.hqy.admin.service.request;

import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.dto.UserDTO;

/**
 * AdminAccountRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 15:27
 */
public interface AdminAccountRequestService {

    /**
     * 获取用户信息
     * @param id  用户id
     * @return    response.
     */
    DataResponse getLoginUserInfo(Long id);

    /**
     * 分页获取用户列表
     * @param username      用户名模糊查询
     * @param nickname      昵称模糊查询
     * @param id            登录用户id
     * @param current       当前页
     * @param size          每页多少行
     * @return              response.
     */
    DataResponse getPageUsers(String username, String nickname, Long id, Integer current, Integer size);

    /**
     * 获取当前用户可以查看的角色列表
     * @param id 账号id
     * @return   response
     */
    DataResponse getUserRoles(Long id);

    /**
     * 检查参数是否存在
     * @param username 用户名
     * @param email    邮箱
     * @param phone    电话
     * @return         response。
     */
    DataResponse checkParamExist(String username, String email, String phone);

    /**
     * 新增用户
     * @param accessAccountId 请求用户id
     * @param userDTO         用户数据
     * @return                response
     */
    DataResponse addUser(Long accessAccountId, UserDTO userDTO);


    /**
     * 修改用户
     * @param accessAccountId 请求用户id
     * @param userDTO         用户数据
     * @return                response
     */
    DataResponse editUser(Long accessAccountId, UserDTO userDTO);

    /**
     * 删除用户
     * @param accessId 修改者id
     * @param id       被删除用户id
     * @return         response
     */
    DataResponse deleteUser(Long accessId, Long id);
}
