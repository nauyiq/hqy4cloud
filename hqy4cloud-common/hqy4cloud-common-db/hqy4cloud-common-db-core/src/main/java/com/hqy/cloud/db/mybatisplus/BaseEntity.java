package com.hqy.cloud.db.mybatisplus;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础的entity类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/29
 */
@Setter
@Getter
public abstract class BaseEntity implements Serializable {

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


}
