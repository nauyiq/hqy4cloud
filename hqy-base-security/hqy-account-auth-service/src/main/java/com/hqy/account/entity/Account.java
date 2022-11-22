package com.hqy.account.entity;

import com.hqy.base.BaseEntity;
import com.hqy.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Table;
import java.util.Date;

/**
 * 账户表 t_account
 * @author qiyuan.hong
 * @date 2022-03-10 21:12
 */
@Slf4j
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
     * 手机号
     */
    private String phone;

    /**
     * 用户角色
     */
    private String roles;

    /**
     * 状态
     */
    private Boolean status = true;


    public Account() {
    }

    public Account(String usernameOrEmail) {
        if (ValidationUtil.validateEmail(usernameOrEmail)) {
            this.email = usernameOrEmail;
        } else {
            this.username = usernameOrEmail;
        }
    }


    public Account(String username, String password, String email, String roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        Date now = new Date();
        super.setCreated(now);
        super.setUpdated(now);
    }

    public Account(long id, String username, String password) {
        log.info("@@@ Create account entity, id:{}, username:{}", id, username);
        Date now = new Date();
        super.setId(id);
        super.setCreated(now);
        super.setUpdated(now);
        this.username = username;
        this.password = password;
    }

    public Account(long id, String username, String password, String email) {
        Date now = new Date();
        super.setId(id);
        super.setCreated(now);
        super.setUpdated(now);
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
