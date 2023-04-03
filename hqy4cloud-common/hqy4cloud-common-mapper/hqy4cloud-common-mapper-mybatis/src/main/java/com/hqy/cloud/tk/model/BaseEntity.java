package com.hqy.cloud.tk.model;

import com.hqy.cloud.common.BaseModel;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 通用的实体映射, 基于tk实现 PK为主键类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 17:39
 */
public abstract class BaseEntity<PK> extends BaseModel implements Serializable {
    private static final long serialVersionUID = 6377578732656651520L;

    /**
     * 主键：id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private PK id;

    public BaseEntity() {
    }

    public BaseEntity(Date date) {
        this.created = date;
        this.updated = date;
    }

    public BaseEntity(PK id, Date date) {
        this.id = id;
        this.created = date;
        this.updated = date;
    }

    public void setDateTime() {
        Date now = new Date();
        this.created = now;
        this.updated = now;
    }

    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }
}
