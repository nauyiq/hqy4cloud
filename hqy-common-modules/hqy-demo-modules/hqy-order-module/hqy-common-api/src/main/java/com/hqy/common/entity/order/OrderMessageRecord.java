package com.hqy.common.entity.order;

import com.hqy.mq.common.transaction.entity.CommonMessageRecord;
import com.hqy.mq.common.listener.payload.MessagePayload;

import javax.persistence.Table;

/**
 * 订单本地消息表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/10 17:01
 */
@Table(name = "t_message_record")
public class OrderMessageRecord extends CommonMessageRecord implements MessagePayload {

    /**
     * 订单id
     */
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
