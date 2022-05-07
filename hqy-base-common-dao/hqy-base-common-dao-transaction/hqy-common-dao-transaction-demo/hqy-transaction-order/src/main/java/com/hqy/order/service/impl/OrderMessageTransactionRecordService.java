package com.hqy.order.service.impl;

import com.hqy.mq.common.service.DeliveryMessageService;
import com.hqy.mq.common.service.impl.MessageTransactionRecordServiceImpl;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 14:31
 */
@Service
public class OrderMessageTransactionRecordService<Long> extends MessageTransactionRecordServiceImpl<Long> {

    @Override
    public DeliveryMessageService getDeliveryMessageService() {
        return SpringContextHolder.getBean(DeliveryMessageService.class);
    }
}
