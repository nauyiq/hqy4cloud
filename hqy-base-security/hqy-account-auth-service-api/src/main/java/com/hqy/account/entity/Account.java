package com.hqy.account.entity;

import com.hqy.base.BaseEntity;
import com.hqy.util.ValidationUtil;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Table;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:12
 */
@Table(name = "t_account")
public class Account extends BaseEntity<Long> {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 盐
     */
    private String salt;

    /**
     * 状态
     */
    private Boolean status = true;


    public Account() {
    }

    public Account(String usernameOrEmail) {
        if (ValidationUtil.validateEmail(email)) {
            this.email = usernameOrEmail;
        } else {
            this.username = usernameOrEmail;
        }
    }


    public Account(String username, String password, String salt, String email) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        Date now = new Date();
        super.setCreated(now);
        super.setUpdated(now);
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
