package com.hqy.account.entity;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;
import java.util.Date;

/**
 *
 * t_resource entity.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 17:55
 */
@Table(name = "t_resource")
public class Resource extends BaseEntity<Integer> {

    /**
     * 资源名
     */
    private String name;

    /**
     * 资源路径
     */
    private String path;

    /**
     * 状态
     */
    private Boolean status;

    public Resource(Date date, String name, String path) {
        super(date);
        this.name = name;
        this.path = path;
        this.status = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
