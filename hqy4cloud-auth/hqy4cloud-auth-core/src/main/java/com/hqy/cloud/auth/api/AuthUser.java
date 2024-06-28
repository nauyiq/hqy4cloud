package com.hqy.cloud.auth.api;

import com.hqy.cloud.auth.common.UserRole;

import java.util.List;

/**
 * 标志性接口、 表示当前登录认证的用户.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/19
 */
public interface AuthUser {

    Long getId();

    String getUsername();

    String getEmail();

    String getPhone();

    List<String> authorities();

    UserRole getUserRole();

}
