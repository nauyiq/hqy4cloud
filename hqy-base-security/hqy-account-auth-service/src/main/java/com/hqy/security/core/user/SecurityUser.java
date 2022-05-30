package com.hqy.security.core.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 14:44
 */
public class SecurityUser extends User {

    private static final long serialVersionUID = 5166170463969575324L;


    public SecurityUser(Long id, String username, String password, String email, boolean status, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.status = status;
        this.email = email;
    }


    /**
     * id
     */
    private Long id;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     */
    private Boolean status;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
