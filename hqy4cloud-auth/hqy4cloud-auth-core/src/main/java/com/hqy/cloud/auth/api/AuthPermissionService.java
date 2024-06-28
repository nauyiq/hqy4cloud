package com.hqy.cloud.auth.api;

import java.util.Collections;
import java.util.List;

/**
 * 权限校验service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10
 */
public interface AuthPermissionService {

    /**
     * 此次请求是否被允许访问.
     * @param request      当前请求
     * @return             result.
     */
    boolean isPermitRequest(AuthenticationRequest request);

    /**
     * 检查当前认证用户是否存在权限列表
     * @param authorities 权限列表
     * @return            是否存在
     */
    boolean hasAuthorities(String... authorities);

    /**
     * 返回白名单uri列表
     * @return 白名单列表
     */
    default List<String> getWhiteUris() {
        return Collections.emptyList();
    }


}
