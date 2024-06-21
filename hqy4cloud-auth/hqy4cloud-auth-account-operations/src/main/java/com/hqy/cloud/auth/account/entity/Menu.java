package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;


/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_admin_menu")
@EqualsAndHashCode(callSuper = true)
public class Menu extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 6353760484084075881L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long parentId;

    private String path;

    private String permission;

    private String icon;

    private Integer type;

    private Integer sortOrder;

    private Boolean status = true;

    private Boolean deleted = false;


}
