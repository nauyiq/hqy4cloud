package com.hqy.cloud.auth.api;

/**
 * 用户认证service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/19
 */
public interface AuthUserService {

    /**
     * 通过用户名获取认证用户
     * @param username 用户名
     * @return         认证用户
     */
    AuthUser getAuthUserByUsername(String username);

    /**
     * 通过邮箱获取认证用户
     * @param email 邮箱
     * @return      认证用户
     */
    AuthUser getAuthUserByEmail(String email);

    /**
     * 通用手机号码获取认证的用户
     * @param mobile 手机
     * @return       认证用户
     */
    AuthUser getAuthUserByMobile(String mobile);


    /**
     * 通用账号id获取认证的用户
     * @param id 账号id
     * @return   认证的用户
     */
    AuthUser getAuthUserById(Long id);

}
