package com.hqy.cloud.auth.api.support;

import com.hqy.cloud.auth.api.AuthUser;
import com.hqy.cloud.auth.common.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/25
 */
@Getter
@Setter
@AllArgsConstructor
public class DefaultAuthUser implements AuthUser {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private UserRole userRole;
    private List<String> authorities;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public List<String> authorities() {
        return this.authorities;
    }
}
