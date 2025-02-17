package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.entity.CommonEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


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
public class Menu extends CommonEntity {

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
