package com.hqy.account.entity;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 18:04
 */
@Table(name = "t_authorities")
public class Authorities extends BaseEntity<Integer> {

    private Integer roleId;
    private String roleName;
    private Integer resourceId;
    private String resourcePath;
    private Boolean status;


    public Authorities() {
    }

    public Authorities(Integer roleId, Integer resourceId) {
        super(new Date());
        this.roleId = roleId;
        this.resourceId = resourceId;
        this.status = true;
    }

    public Authorities(Integer roleId, String roleName, Integer resourceId, String resourcePath) {
        super(new Date());
        this.roleId = roleId;
        this.roleName = roleName;
        this.resourceId = resourceId;
        this.resourcePath = resourcePath;
        this.status = true;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
