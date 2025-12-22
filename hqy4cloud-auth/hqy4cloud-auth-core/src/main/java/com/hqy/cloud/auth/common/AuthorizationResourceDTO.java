package com.hqy.cloud.auth.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * 权限控制的资源
 * @author hongqy
 * @date 2025/12/15
 */
@Getter
@Setter
public class AuthorizationResourceDTO {

    /**
     * 资源ID， 唯一标识
     */
    private String id;

    /**
     * 允许访问的权限列表
     */
    private Set<String> authorities;


}
