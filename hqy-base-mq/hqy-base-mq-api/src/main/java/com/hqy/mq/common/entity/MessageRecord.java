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
public class MessageRecord<T> extends BaseEntity<Long> {

    /**
     * 业务id
     */
    private T businessId;

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

    public MessageRecord() {
    }

    public MessageRecord(String messageId) {
        this.messageId = messageId;
    }

    public MessageRecord(T businessId, String messageId, Integer retries, Boolean status) {
        super(new Date());
        this.businessId = businessId;
        this.messageId = messageId;
        this.retries = retries;
        this.status = status;
    }

    public T getBusinessId() {
        return businessId;
    }

    public void setBusinessId(T businessId) {
        this.businessId = businessId;
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
        MessageRecord<?> that = (MessageRecord<?>) o;
        return Objects.equal(businessId, that.businessId) && Objects.equal(messageId, that.messageId) && Objects.equal(retries, that.retries) && Objects.equal(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), businessId, messageId, retries, status);
    }
}
