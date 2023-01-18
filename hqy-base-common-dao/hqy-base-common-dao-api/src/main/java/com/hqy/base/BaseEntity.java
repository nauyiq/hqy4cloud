package com.hqy.base;

import lombok.Data;

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
@Data
public abstract class BaseEntity<PK> implements Serializable {
    private static final long serialVersionUID = 6377578732656651520L;

    /**
     * 主键：id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private PK id;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

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
}
