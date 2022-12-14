package com.hqy.auth.entity;

import com.hqy.base.PrimaryLessBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * RoleMenu.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 13:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_admin_role_menu")
public class RoleMenu implements PrimaryLessBaseEntity {

    private Integer roleId;

    private Integer menuId;

}