package com.hqy.auth.common.vo;

import com.hqy.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/13 9:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRoleVO {

    private Integer id;
    private String name;

    public AccountRoleVO(Role role) {
        this.id = role.getId();
        this.name = role.getName();
    }
}
