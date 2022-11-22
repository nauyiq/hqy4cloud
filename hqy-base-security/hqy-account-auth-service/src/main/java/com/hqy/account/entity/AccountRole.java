package com.hqy.account.entity;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 11:30
 */
@Table(name = "t_account_role")
public class AccountRole extends BaseEntity<Integer> {
    
    private String name;

    private Boolean status;

    public AccountRole() {
    }

    public AccountRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
