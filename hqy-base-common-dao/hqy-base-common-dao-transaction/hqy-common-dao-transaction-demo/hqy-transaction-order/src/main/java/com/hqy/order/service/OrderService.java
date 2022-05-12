package com.hqy.order.service;

import com.hqy.base.BaseTkService;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.order.common.entity.Order;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:46
 */
public interface OrderService extends BaseTkService<Order, Long> {

    /**
     * 下单
     * @param storageId 商品id
     * @param count 数目
     * @return MessageResponse
     */
    MessageResponse order(Long storageId, Integer count);

    /**
     * 下单
     * @param storageId 商品id
     * @param count 数目
     * @return MessageResponse
     */
    MessageResponse tccOrder(Long storageId, Integer count);

    /**
     * 基于mq 本地消息表的 分布式事务下单
     * @param storageId 库存id
     * @param count     数目
     * @return
     */
    MessageResponse mqOrderDemo(Long storageId, Integer count);

    /**
     * 基于kafka 实现本地消息表的 分布式事务下单demo
     * @param storageId 库存id
     * @param count     数目
     * @return messageResponse
     */
    MessageResponse kafkaOrder(Long storageId, Integer count);
}
