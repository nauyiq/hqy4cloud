package com.hqy.account.entity;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 11:30
 */
@Table(name = "t_account_role")
public class AccountRole extends BaseEntity<Long> {

    /**
     * 账户id
     */
    private Long accountId;

    /**
     * 角色
     */
    private String role;

    /**
     * 状态
     */
    private Boolean status = true;

    public AccountRole() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
