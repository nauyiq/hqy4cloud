package com.hqy.auth.dto;

import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25 15:10
 */
@ToString
public class OauthAccountDTO {

    @NotEmpty(message = "Username cannot be empty.")
    private String username;

    @NotEmpty(message = "Password cannot be empty.")
    private String password;

    public OauthAccountDTO() {
    }

    public OauthAccountDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
