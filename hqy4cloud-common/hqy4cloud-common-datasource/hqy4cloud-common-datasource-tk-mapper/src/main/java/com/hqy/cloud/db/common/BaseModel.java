package com.hqy.cloud.db.common;

import java.io.Serializable;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/23 16:04
 */
public class BaseModel implements Serializable {

    public BaseModel() {
    }

    public BaseModel(Date created, Date updated) {
        this.created = created;
        this.updated = updated;
    }

    /**
     *  创建时间
     */
    protected Date created;
    /**
     * 更新时间
     */
    protected Date updated;


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

    @Override
    public String toString() {
        return "BaseModel{" +
                "created=" + created +
                ", updated=" + updated +
                '}';
    }
}
