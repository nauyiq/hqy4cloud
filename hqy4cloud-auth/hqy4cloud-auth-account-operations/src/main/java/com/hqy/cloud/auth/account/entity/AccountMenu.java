package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 账号菜单中间表实体
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountMenu extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账号id
     */
    private Long accountId;

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 菜单锁对应permission
     */
    private String menuPermission;

}
