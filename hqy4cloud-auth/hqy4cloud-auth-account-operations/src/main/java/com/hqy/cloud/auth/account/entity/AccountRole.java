package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.*;

/**
 * 账户角色中间表 t_account_role
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_account_role")
public class AccountRole extends BaseEntity {

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
