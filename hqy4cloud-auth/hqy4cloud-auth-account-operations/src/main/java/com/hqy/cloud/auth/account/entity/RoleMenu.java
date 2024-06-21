package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RoleMenu.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_admin_role_menu")
public class RoleMenu extends BaseEntity {

    private Integer roleId;
    private Integer menuId;

    public RoleMenu(Integer roleId) {
        this.roleId = roleId;
    }

}
