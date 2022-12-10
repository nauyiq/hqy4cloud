package com.hqy.auth.entity;

import com.hqy.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 11:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_admin_menu")
@EqualsAndHashCode(callSuper = true)
public class Menu extends BaseEntity<Long> {
    private static final long serialVersionUID = 6353760484084075881L;

    private String name;

    private Long parentId;

    private String path;

    private String permission;

    private String icon;

    private Integer sort_order;

    private Integer resourceId;

    private Integer type;

    private Boolean status;

    private Boolean deleted;


}
