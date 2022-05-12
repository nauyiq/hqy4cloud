package com.hqy.mq.common.transaction.stategy;

import com.hqy.mq.common.MessageQueueType;
import com.hqy.util.AssertUtil;
import com.hqy.util.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/12 15:55
 */
public class DeliveryMessageContext {

    private static final Logger log = LoggerFactory.getLogger(DeliveryMessageContext.class);

    private DeliveryMessageContext() {}

    public static DeliveryMessageStrategy deliveryMessageStrategy(MessageQueueType messageQueueType) {
        AssertUtil.notNull(messageQueueType, "Get delivery strategy failure, queue type not found.");

        DeliveryMessageStrategy deliveryMessageStrategy = null;
        Class<? extends DeliveryMessageStrategy> strategyClass = messageQueueType.strategyClass;

        try {
            deliveryMessageStrategy = SpringContextHolder.getBean(strategyClass);
        } catch (Exception e) {
            log.error("@@@ Cannot get DeliveryMessageStrategy instance in spring container. messageQueueType:{}", messageQueueType);
            log.error(e.getMessage(), e);
        }

        AssertUtil.notNull(deliveryMessageStrategy, "Get delivery strategy failure, messageQueueType = " + messageQueueType);

        return deliveryMessageStrategy;
    }

}
