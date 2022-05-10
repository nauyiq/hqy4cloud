package com.hqy.order.common.entity;

import com.hqy.mq.common.entity.CommonMessageRecord;
import com.hqy.mq.common.listener.payload.RabbitPayload;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/10 17:01
 */
@Table(name = "t_message_record")
public class OrderMessageRecord extends CommonMessageRecord implements RabbitPayload {

    private Long businessId;

    public OrderMessageRecord() {
    }

    public OrderMessageRecord(String messageId) {
        super(messageId);
    }

    public OrderMessageRecord(Long businessId, String messageId, Integer retries, Boolean status) {
        super(messageId, retries, status);
        this.businessId = businessId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    @Override
    public String obtainMessageId() {
        return super.getMessageId();
    }
}
