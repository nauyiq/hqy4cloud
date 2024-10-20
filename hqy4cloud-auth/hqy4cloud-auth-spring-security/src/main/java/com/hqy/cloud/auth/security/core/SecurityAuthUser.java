package com.hqy.cloud.auth.security.core;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.auth.api.AuthUser;
import com.hqy.cloud.auth.common.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * oauth2 认证用户实体
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
@Getter
public class SecurityAuthUser extends User implements OAuth2AuthenticatedPrincipal, AuthUser {

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
     * 用户角色
     */
    private final UserRole userRole;

    /**
     * 用户属性，用于特定情况下拓展使用
     */
    private final Map<String, Object> attributes = MapUtil.newHashMap();


    public SecurityAuthUser(Long id, String username, String password, String email, String phone, Boolean status,
                            UserRole userRole,
                            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.userRole = userRole;

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

    @Override
    public List<String> authorities() {
        return getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.status) ;
    }
}
