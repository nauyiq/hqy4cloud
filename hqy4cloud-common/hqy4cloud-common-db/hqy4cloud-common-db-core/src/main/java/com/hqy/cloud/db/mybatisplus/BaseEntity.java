package com.hqy.cloud.db.mybatisplus;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/29
 */
public abstract class BaseEntity {

    public BaseEntity() {
    }

    public BaseEntity(Date now) {
        this.created = now;
        this.updated = now;
    }

    /**
     * 创建时间
     */
    @TableField(value = "created", fill = FieldFill.INSERT)
    private Date created;

    /**
     * 更新时间
     */
    @TableField(value = "updated", fill = FieldFill.INSERT_UPDATE)
    private Date updated;


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
