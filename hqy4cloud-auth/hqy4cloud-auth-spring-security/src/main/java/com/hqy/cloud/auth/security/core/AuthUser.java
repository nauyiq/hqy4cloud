package com.hqy.cloud.auth.security.core;

import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.Map;

/**
 * oauth2 认证用户实体
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
@Getter
public class AuthUser extends User implements OAuth2AuthenticatedPrincipal {

    /**
     * 用户id
     */
    private final Long id;

    /**
     * 用户邮箱
     */
    private final String email;

    /**
     * 用户手机
     */
    private final String phone;

    /**
     * 用户状态， 是否可用
     */
    private final Boolean status;

    /**
     * 用户属性，用于特定情况下拓展使用
     */
    private final Map<String, Object> attributes = MapUtil.newHashMap();


    public AuthUser(Long id, String username, String password, String email, String phone, Boolean status, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public Map<String, Object> addAttribute(String key, Object value) {
        this.attributes.put(key, value);
        return this.attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return this.getUsername();
    }
}
