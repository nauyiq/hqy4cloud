package com.hqy.cloud.auth.entity;

import com.hqy.cloud.tk.model.BaseEntity;
import com.hqy.cloud.util.ValidationUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Table;
import java.util.Date;

/**
 * 账户表 t_account
 * @author qiyuan.hong
 * @date 2022-03-10 21:12
 */

@Slf4j
@Data
@Table(name = "t_account")
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity<Long> {
    private static final long serialVersionUID = -7814298685660847656L;

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

    /**
     * 是否删除
     */
    private Boolean deleted = false;


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

}
