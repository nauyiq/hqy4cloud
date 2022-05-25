package com.hqy.common.entity.storage;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;

/**
 * entity for t_order_storage
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/23 16:27
 */
@Table(name = "t_order_storage")
public class OrderStorage extends BaseEntity<Long> {

    private Long orderId;

    private Long storageId;

    private Integer count;

    private Boolean status;

    public OrderStorage() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
