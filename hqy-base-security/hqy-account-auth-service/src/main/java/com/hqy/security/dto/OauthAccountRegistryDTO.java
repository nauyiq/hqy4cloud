package com.hqy.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25 15:10
 */
@Data
@ToString
@AllArgsConstructor
public class OauthAccountRegistryDTO {

    @NotEmpty(message = "Username cannot be empty.")
    private String username;

    @NotEmpty(message = "Email cannot be empty.")
    private String email;

    @NotEmpty(message = "Password cannot be empty.")
    private String password;

    @NotEmpty(message = "Registry code be empty.")
    private String code;

    public OauthAccountRegistryDTO() {
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
