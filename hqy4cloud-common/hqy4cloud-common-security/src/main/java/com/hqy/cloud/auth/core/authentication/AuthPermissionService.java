package com.hqy.cloud.auth.core.authentication;

import java.util.Collections;
import java.util.List;

/**
 * Oauth2Access.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 13:37
 */
public interface AuthPermissionService {

    /**
     * 此次请求是否允许放行.
     * @param authorities  用户拥有的权限
     * @param request      当前请求
     * @return             result.
     */
    boolean isPermitRequest(List<String> authorities, AuthenticationRequest request);

    /**
     * 当前请求是否是白名单请求
     * @param request  请求。
     * @return         result
     */
    boolean isWhiteRequest(AuthenticationRequest request);


    /**
     * 检查当前认证用户上下文是否存在permission
     * @param permissions 校验的permissions
     * @return            result.
     */
    boolean havePermissions(String... permissions);

    /**
     * 返回白名单uri列表
     * @return 白名单列表
     */
    default List<String> getWhites() {
        return Collections.emptyList();
    }


}
