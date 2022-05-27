package com.hqy.auth.dto;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25 11:06
 */
@ToString
public class UserJwtPayloadDTO {

    /**
     * id
     */
    public Long id;

    /**
     * 密码
     */
    public String password;

    /**
     * 邮箱
     */
    public String email;

    /**
     * 用户名
     */
    public String username;

    /**
     * 权限
     */
    public Collection<? extends GrantedAuthority> authorities;

    public UserJwtPayloadDTO() {
        super();
    }

    public UserJwtPayloadDTO(Long id, String password, String email, String username, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.username = username;
        this.authorities = authorities;
    }
}
