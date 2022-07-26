package com.hqy.mq.common.transaction.stategy;

import com.hqy.mq.common.transaction.entity.CommonMessageRecord;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 10:48
 */
public interface DeliveryMessageStrategy {

    /**
     * 投递消息到中间件.
     * @param commonMessageRecord 本地消息记录表 entity
     * @return                    是否投递成功.
     */
    boolean deliveryMessage(CommonMessageRecord commonMessageRecord);

}
