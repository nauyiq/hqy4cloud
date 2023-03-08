package com.hqy.cloud.auth.core;

import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.Map;

/**
 * 构建jwt token用户对象. 存储用户不经常变更的数据. 不存储用户经常可能变化的数据，比如昵称、头像
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 14:44
 */
public class SecurityUser extends User implements OAuth2AuthenticatedPrincipal {
    private static final long serialVersionUID = 5166170463969575324L;

    public SecurityUser(Long id, String username, String password, String email, boolean status, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.status = status;
        this.email = email;
    }

    @Getter
    private final Long id;
    @Getter
    private final String email;
    @Getter
    private final Boolean status;

    @Override
    public Map<String, Object> getAttributes() {
        return MapUtil.newHashMap();
    }

    @Override
    public String getName() {
        return this.getUsername();
    }
}
