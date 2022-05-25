package com.hqy.common.entity.storage;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @date 2022-05-25 23:32
 */
@Table(name = "t_sec_kill_order")
public class SecKillOrder extends BaseEntity<Long> {

    private Long accountId;

    private Long storageId;

    private Boolean status;

    public SecKillOrder() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
