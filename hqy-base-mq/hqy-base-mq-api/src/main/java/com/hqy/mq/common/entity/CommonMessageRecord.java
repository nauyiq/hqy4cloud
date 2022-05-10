package com.hqy.mq.common.entity;

import com.google.common.base.Objects;
import com.hqy.base.BaseEntity;

import java.util.Date;

/**
 * 无事务的mq + 本地消息表
 * 本地消息表entity.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 10:02
 */
public abstract class CommonMessageRecord extends BaseEntity<Long>  {

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 重试次数
     */
    private Integer retries;

    /**
     * 状态
     */
    private Boolean status;

    public CommonMessageRecord() {
    }

    public CommonMessageRecord(String messageId) {
        this.messageId = messageId;
    }

    public CommonMessageRecord(String messageId, Integer retries, Boolean status) {
        super(new Date());
        this.messageId = messageId;
        this.retries = retries;
        this.status = status;
    }



    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommonMessageRecord that = (CommonMessageRecord) o;
        return Objects.equal(messageId, that.messageId) && Objects.equal(retries, that.retries) && Objects.equal(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), messageId, retries, status);
    }

}
