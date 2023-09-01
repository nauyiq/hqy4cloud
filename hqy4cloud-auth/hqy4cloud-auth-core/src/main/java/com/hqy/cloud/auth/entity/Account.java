package com.hqy.cloud.auth.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import com.hqy.cloud.util.ValidationUtil;
import lombok.*;

import javax.persistence.Table;
import java.util.Date;

/**
 * 账户表 t_account
 * @author qiyuan.hong
 * @date 2022-03-10 21:12
 */


@Data
@ToString
@Table(name = "t_account")
@NoArgsConstructor
@AllArgsConstructor
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
    private Boolean status;

    /**
     * 是否删除
     */
    private Boolean deleted = false;


    public Account(String usernameOrEmail) {
        if (ValidationUtil.validateEmail(usernameOrEmail)) {
            this.email = usernameOrEmail;
        } else {
            this.username = usernameOrEmail;
        }
    }

    public Account(Long id, String username, String password) {
        this(id, username, password, null, null, null);
    }

    public Account(String username, String password, String email, String roles) {
        this(null, username, password, email, roles, null);
    }

    public Account(Long id, String username, String password, String email, String roles, String phone) {
        super(id, new Date());
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.phone = phone;
    }



}
