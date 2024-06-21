package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色资源中间表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_role_resources")
public class RoleResources extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -918289194603353874L;

    private Integer roleId;

    private String roleName;

    private Integer resourceId;

    public RoleResources(Integer roleId) {
        this.roleId = roleId;
    }

    public RoleResources(String roleName) {
        this.roleName = roleName;
    }
}
