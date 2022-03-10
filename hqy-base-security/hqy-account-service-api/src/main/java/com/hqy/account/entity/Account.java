package com.hqy.account.entity;

import com.hqy.base.BaseEntity;

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
     * 盐
     */
    private String salt;

    public Account() {
    }

    public Account(String username) {
        this.username = username;
    }

    public Account(String username, String password, String salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        Date now = new Date();
        super.setCreated(now);
        super.setUpdated(now);
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
}
