package com.hqy.base;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * 通用的实体映射, 基于tk实现 PK为主键类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 17:39
 */
@Data
public class BaseEntity<PK>{

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
}
