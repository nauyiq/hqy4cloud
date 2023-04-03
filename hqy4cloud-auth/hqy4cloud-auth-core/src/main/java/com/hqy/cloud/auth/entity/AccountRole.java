package com.hqy.cloud.auth.entity;

import com.hqy.cloud.tk.PrimaryLessBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * 账户角色中间表 t_account_role
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 16:24
 */
@Data
@Table(name = "t_account_role")
@AllArgsConstructor
@NoArgsConstructor
public class AccountRole implements PrimaryLessBaseEntity {

    private Long accountId;
    private Integer roleId;
    private Integer level;

    public AccountRole(Long accountId) {
        this.accountId = accountId;
    }

    public AccountRole(Integer roleId) {
        this.roleId = roleId;
    }

    public AccountRole(Long accountId, Integer roleId) {
        this.accountId = accountId;
        this.roleId = roleId;
    }
}
