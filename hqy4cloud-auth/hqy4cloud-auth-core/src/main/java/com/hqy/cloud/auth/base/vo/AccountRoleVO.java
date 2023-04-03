package com.hqy.cloud.auth.base.vo;

import com.hqy.cloud.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

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
    @NotEmpty(message = "Role name should not be empty.")
    private String name;
    private String note;
    @NotNull(message = "Role level should not be null.")
    private Integer level;
    private String created;

    public AccountRoleVO(Role role) {
        this.id = role.getId();
        this.name = role.getName();
    }
}
