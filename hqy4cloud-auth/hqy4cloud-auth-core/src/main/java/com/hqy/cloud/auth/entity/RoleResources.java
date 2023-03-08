package com.hqy.cloud.auth.entity;

import com.hqy.cloud.tk.PrimaryLessBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * 角色资源中间表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 9:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_role_resources")
public class RoleResources implements PrimaryLessBaseEntity, Serializable {
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
